/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package auth.datastoreimpls;

import auth.*;
import database.MysqlProvider;
import jakarta.inject.Inject;

/**
 *
 * @author mohammed
 */
import java.sql.*;
import java.time.Instant;
import java.util.*;

public class MySqlAuthDataStore implements AuthDataStore {

    // TABLES NEEDED
    /*
    CREATE TABLE users (
    id               VARCHAR(64)   PRIMARY KEY,
    full_name        VARCHAR(255)  NOT NULL,
    email            VARCHAR(255)  NOT NULL UNIQUE,
    password_hash_b64 VARCHAR(255) NOT NULL,
    salt_b64         VARCHAR(255)  NOT NULL,
    iterations       INT           NOT NULL,
    key_len_bytes    INT           NOT NULL,
    created_at       TIMESTAMP(6)  NOT NULL,
    INDEX idx_users_email (email)
    );
    
    CREATE TABLE user_roles (
    user_id VARCHAR(64)  NOT NULL,
    role    VARCHAR(32)  NOT NULL,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );
    
    
    CREATE TABLE sessions (
    id         VARCHAR(128) PRIMARY KEY,
    user_id    VARCHAR(64)  NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    expires_at TIMESTAMP(6) NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );
    
    
    */
    
    
    //private final String jdbcUrl;
    //private final String jdbcUser;
    //private final String jdbcPassword;

    
    public MySqlAuthDataStore() {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC driver not found", e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(MysqlProvider.URL, MysqlProvider.USERNAME, MysqlProvider.PASSWORD);
    }

    @Override
    public void createUser(User user) throws Exception {
        final String insertUserSql
                = "INSERT INTO users "
                + "(id, full_name, email, password_hash_b64, salt_b64, iterations, key_len_bytes, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        final String insertRoleSql
                = "INSERT INTO user_roles (user_id, role) VALUES (?, ?)";

        try (Connection con = getConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(insertUserSql)) {
                ps.setString(1, user.id);
                ps.setString(2, user.fullName);
                ps.setString(3, user.email);
                ps.setString(4, user.passwordHashB64);
                ps.setString(5, user.saltB64);
                ps.setInt(6, user.iterations);
                ps.setInt(7, user.keyLenBytes);
                ps.setTimestamp(8, Timestamp.from(user.createdAt));
                ps.executeUpdate();
            }

            if (user.roles != null && !user.roles.isEmpty()) {
                try (PreparedStatement psRoles = con.prepareStatement(insertRoleSql)) {
                    for (User.Role role : user.roles) {
                        psRoles.setString(1, user.id);
                        psRoles.setString(2, role.name());
                        psRoles.addBatch();
                    }
                    psRoles.executeBatch();
                }
            }

            con.commit();
        } catch (SQLIntegrityConstraintViolationException e) {
            // unique email, PK, etc.
            throw new IllegalStateException("EMAIL_TAKEN", e);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Optional<User> findUserByEmail(String email) throws Exception {
        final String sql
                = "SELECT id, full_name, email, password_hash_b64, salt_b64, "
                + "       iterations, key_len_bytes, created_at "
                + "FROM users WHERE email = ?";

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = mapUser(con, rs);
                    return Optional.of(u);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findUserById(String userId) throws Exception {
        final String sql
                = "SELECT id, full_name, email, password_hash_b64, salt_b64, "
                + "       iterations, key_len_bytes, created_at "
                + "FROM users WHERE id = ?";

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = mapUser(con, rs);
                    return Optional.of(u);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void updateUser(User user) throws Exception {
        final String updateUserSql
                = "UPDATE users SET full_name = ?, email = ?, password_hash_b64 = ?, "
                + "                 salt_b64 = ?, iterations = ?, key_len_bytes = ? "
                + "WHERE id = ?";

        final String deleteRolesSql = "DELETE FROM user_roles WHERE user_id = ?";
        final String insertRoleSql = "INSERT INTO user_roles (user_id, role) VALUES (?, ?)";

        try (Connection con = getConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(updateUserSql)) {
                ps.setString(1, user.fullName);
                ps.setString(2, user.email);
                ps.setString(3, user.passwordHashB64);
                ps.setString(4, user.saltB64);
                ps.setInt(5, user.iterations);
                ps.setInt(6, user.keyLenBytes);
                ps.setString(7, user.id);
                ps.executeUpdate();
            }

            try (PreparedStatement psDel = con.prepareStatement(deleteRolesSql)) {
                psDel.setString(1, user.id);
                psDel.executeUpdate();
            }

            if (user.roles != null && !user.roles.isEmpty()) {
                try (PreparedStatement psAdd = con.prepareStatement(insertRoleSql)) {
                    for (User.Role role : user.roles) {
                        psAdd.setString(1, user.id);
                        psAdd.setString(2, role.name());
                        psAdd.addBatch();
                    }
                    psAdd.executeBatch();
                }
            }

            con.commit();
        }
    }

    @Override
    public Set<User> getAllUsers() {
        final String sql
                = "SELECT id, full_name, email, password_hash_b64, salt_b64, "
                + "       iterations, key_len_bytes, created_at "
                + "FROM users";

        Set<User> users = new LinkedHashSet<>();

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapUser(con, rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to get all users", e);
        }

        return users;
    }

    @Override
    public PageableUsers getUsersByPage(int page) {
        if (page < 1) {
            page = 1;
        }

        final int pageSize = PageableUsers.MAX_PAGE_SIZE;
        final String countSql = "SELECT COUNT(*) FROM users";
        final String pageSql
                = "SELECT id, full_name, email, password_hash_b64, salt_b64, "
                + "       iterations, key_len_bytes, created_at "
                + "FROM users ORDER BY id LIMIT ? OFFSET ?";

        try (Connection con = getConnection()) {

            int totalUsers;
            try (PreparedStatement ps = con.prepareStatement(countSql); ResultSet rs = ps.executeQuery()) {
                rs.next();
                totalUsers = rs.getInt(1);
            }

            if (totalUsers == 0) {
                return new PageableUsers(Collections.emptySet(), 0, page);
            }

            int maxPages = (int) Math.ceil((double) totalUsers / pageSize);
            if (page > maxPages) {
                page = maxPages;
            }

            int offset = (page - 1) * pageSize;
            Set<User> pageUsers = new LinkedHashSet<>();

            try (PreparedStatement ps = con.prepareStatement(pageSql)) {
                ps.setInt(1, pageSize);
                ps.setInt(2, offset);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        pageUsers.add(mapUser(con, rs));
                    }
                }
            }

            return new PageableUsers(pageUsers, maxPages, page);

        } catch (Exception e) {
            throw new RuntimeException("Failed to get users by page", e);
        }
    }

    private User mapUser(Connection con, ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String fullName = rs.getString("full_name");
        String email = rs.getString("email");
        String pwdHash = rs.getString("password_hash_b64");
        String salt = rs.getString("salt_b64");
        int iterations = rs.getInt("iterations");
        int keyLenBytes = rs.getInt("key_len_bytes");
        Timestamp created = rs.getTimestamp("created_at");
        Instant createdAt = created != null ? created.toInstant() : Instant.now();

        Set<User.Role> roles = loadRolesForUser(con, id);

        return new User(id, fullName, email, pwdHash, salt,
                iterations, keyLenBytes, createdAt, roles);
    }

    private Set<User.Role> loadRolesForUser(Connection con, String userId) throws SQLException {
        final String sql = "SELECT role FROM user_roles WHERE user_id = ?";
        Set<User.Role> roles = EnumSet.noneOf(User.Role.class);

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String roleName = rs.getString("role");
                    try {
                        roles.add(User.Role.valueOf(roleName));
                    } catch (IllegalArgumentException ex) {
                        // unknown role string in DB; ignore or log
                    }
                }
            }
        }
        return roles;
    }

    @Override
    public void createSession(Session session) throws Exception {
        final String sql = "INSERT INTO sessions (id, user_id, created_at, expires_at) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, session.id);
            ps.setString(2, session.userId);

            // createdAt (never null normally)
            ps.setTimestamp(3, Timestamp.from(session.createdAt));

            // expiresAt may be null
            if (session.expiresAt != null) {
                ps.setTimestamp(4, Timestamp.from(session.expiresAt));
            } else {
                ps.setNull(4, Types.TIMESTAMP);
            }

            ps.executeUpdate();
        }
    }

    @Override
    public Optional<Session> findSessionById(String sessionId) throws Exception {
        final String sql
                = "SELECT id, user_id, created_at, expires_at FROM sessions WHERE id = ?";

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, sessionId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Session s = mapSession(rs);
                    return Optional.of(s);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void deleteSession(String sessionId) throws Exception {
        final String sql = "DELETE FROM sessions WHERE id = ?";

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, sessionId);
            ps.executeUpdate();
        }
    }

    @Override
    public void deleteSessionsForUser(String userId) throws Exception {
        final String sql = "DELETE FROM sessions WHERE user_id = ?";

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, userId);
            ps.executeUpdate();
        }
    }

    private Session mapSession(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String userId = rs.getString("user_id");

        Timestamp createdTs = rs.getTimestamp("created_at");
        Timestamp expiresTs = rs.getTimestamp("expires_at");

        Instant createdAt = createdTs != null ? createdTs.toInstant() : Instant.now();
        Instant expiresAt = (expiresTs != null ? expiresTs.toInstant() : null);

        return new Session(id, userId, createdAt, expiresAt);
    }

}
