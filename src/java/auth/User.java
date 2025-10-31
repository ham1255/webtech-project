/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package auth;

import java.time.Instant;

/**
 *
 * @author mohammed
 */
public class User{
    public final String id;
    public final String username;
    public final String fullName;
    public final String passwordHashB64; // derived key
    public final String saltB64;         // per-user salt
    public final int iterations;         // PBKDF2 rounds
    public final int keyLenBytes;        // dk length in bytes
    public final Instant createdAt;

    public User(String id,String fullName, String username, String passwordHashB64, String saltB64,
                      int iterations, int keyLenBytes, Instant createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.passwordHashB64 = passwordHashB64;
        this.saltB64 = saltB64;
        this.iterations = iterations;
        this.keyLenBytes = keyLenBytes;
        this.createdAt = createdAt;
    }

    public User withPassword(String newHashB64, String newSaltB64, int newIterations, int newKeyLen) {
        return new User(this.id, this.fullName, this.username, newHashB64, newSaltB64, newIterations, newKeyLen, this.createdAt);
    }
}