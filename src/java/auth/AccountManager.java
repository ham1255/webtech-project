/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package auth;

import auth.AuthDataStore.PageableUsers;
import auth.User.Role;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 *
 * @author mohammed
 */
public class AccountManager {

    private static final String PBKDF2_ALG = "PBKDF2WithHmacSHA256";
    private static final int DEFAULT_ITERATIONS = 150_000;
    private static final int DEFAULT_KEYLEN_BYTES = 32; // 256-bit derived key
    private static final int SALT_BYTES = 16;
    private static final SecureRandom RNG = new SecureRandom();

    private final AuthDataStore store;
    private final Duration sessionTtl;

    public AccountManager(AuthDataStore store, Duration sessionTtl) {
        this.store = store;
        this.sessionTtl = sessionTtl;
    }

    /**
     * Registers a user; throws if username is taken.
     *
     * @param id
     * @param fullName
     * @param email
     * @param password
     * @param roles
     * @return
     * @throws java.lang.Exception
     */
    public String register(String id, String fullName, String email, String password, Set<Role> roles) throws Exception {
        Objects.requireNonNull(email);
        Objects.requireNonNull(password);

        if (store.findUserByEmail(email).isPresent()) {
            throw new IllegalStateException("USERNAME_TAKEN");
        }

        byte[] salt = randomBytes(SALT_BYTES);
        HashOut ho = hashPassword(password.toCharArray(), salt, DEFAULT_ITERATIONS, DEFAULT_KEYLEN_BYTES);

        User user = new User(
                id,
                fullName,
                email,
                Base64.getEncoder().encodeToString(ho.dk),
                Base64.getEncoder().encodeToString(salt),
                DEFAULT_ITERATIONS,
                DEFAULT_KEYLEN_BYTES,
                Instant.now(),
                roles
        );

        store.createUser(user);
        return user.id;
    }

    public String register(String fullName, String email, String password, Set<Role> roles) throws Exception {
        return register(UUID.randomUUID().toString(), fullName, email, password, roles);
    }

    /**
     * Verifies credentials; on success creates and returns a new session ID.
     *
     * @param email
     * @param password
     * @return
     * @throws java.lang.Exception
     */
    public String login(String email, String password) throws Exception {
        User user = store.findUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("INVALID_CREDENTIALS"));

        if (!verifyPassword(password.toCharArray(), user)) {
            throw new IllegalArgumentException("INVALID_CREDENTIALS");
        }

        String sessionId = randomSessionId();
        Instant now = Instant.now();
        Session session = new Session(sessionId, user.id, now, now.plus(sessionTtl));
        store.createSession(session);
        return sessionId;
    }

    /**
     * Returns userId if the session is valid (and not expired); otherwise
     * empty.
     *
     * @param sessionId
     * @return user id
     * @throws java.lang.Exception
     */
    public Optional<String> validateSession(String sessionId) throws Exception {
        Optional<Session> s = store.findSessionById(sessionId);
        if (s.isEmpty()) {
            return Optional.empty();
        }

        Session sr = s.get();
        if (Instant.now().isAfter(sr.expiresAt)) {
            // Expired: delete and reject
            store.deleteSession(sessionId);
            return Optional.empty();
        }
        return Optional.of(sr.userId);
    }

    /**
     * Revokes a single session.
     *
     * @param sessionId
     * @throws java.lang.Exception
     */
    public void logout(String sessionId) throws Exception {
        store.deleteSession(sessionId);
    }

    /**
     * Revokes all sessions for a user (force logout everywhere).
     *
     * @param userId
     * @throws java.lang.Exception
     */
    public void logoutAll(String userId) throws Exception {
        store.deleteSessionsForUser(userId);
    }

    /**
     * Changes password after verifying the old one; revokes all sessions.
     *
     * @param user
     * @param oldPassword
     * @param newPassword
     * @throws java.lang.Exception
     */
    public void changePassword(User user, String oldPassword, String newPassword) throws Exception {
        if (!verifyPassword(oldPassword.toCharArray(), user)) {
            throw new IllegalArgumentException("INVALID_CREDENTIALS");
        }
        changePassword(user, newPassword);
    }

    /**
     * Changes password WITHOUT verifying the old one; revokes all sessions.
     *
     * @param user
     * @param newPassword
     * @throws java.lang.Exception
     */
    public void changePassword(User user, String newPassword) throws Exception {

        byte[] newSalt = randomBytes(SALT_BYTES);
        HashOut ho = hashPassword(newPassword.toCharArray(), newSalt, DEFAULT_ITERATIONS, DEFAULT_KEYLEN_BYTES);

        user.updatePassword(
                Base64.getEncoder().encodeToString(ho.dk),
                Base64.getEncoder().encodeToString(newSalt),
                DEFAULT_ITERATIONS,
                DEFAULT_KEYLEN_BYTES
        );

        store.updateUser(user);
        store.deleteSessionsForUser(user.id); // rotate sessions after password change
    }


    /* ===== Helpers ===== */
    private static class HashOut {

        final byte[] dk;

        HashOut(byte[] dk) {
            this.dk = dk;
        }
    }

    private static HashOut hashPassword(char[] password, byte[] salt, int iterations, int keyLenBytes) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLenBytes * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALG);
        byte[] dk = skf.generateSecret(spec).getEncoded();
        return new HashOut(dk);
    }

    private static boolean verifyPassword(char[] candidate, User user) throws Exception {
        byte[] salt = Base64.getDecoder().decode(user.saltB64);
        byte[] expected = Base64.getDecoder().decode(user.passwordHashB64);
        HashOut ho = hashPassword(candidate, salt, user.iterations, user.keyLenBytes);
        // constant-time comparison
        return MessageDigest.isEqual(expected, ho.dk);
    }

    private static String randomSessionId() {
        // Opaque, unguessable token (server-side lookup). 256-bit entropy.
        byte[] buf = randomBytes(32);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
    }

    private static byte[] randomBytes(int n) {
        byte[] b = new byte[n];
        RNG.nextBytes(b);
        return b;
    }

    public User getUserById(String userId) throws Exception {
        return store.findUserById(userId).orElseThrow(() -> new IllegalArgumentException("USER_NOT_FOUND"));
    }

    public PageableUsers getUsersByPage(int page) {
        return store.getUsersByPage(page);
    }

    public AuthDataStore getStore() {
        return store;
    }

}
