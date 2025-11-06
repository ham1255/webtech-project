/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package auth;

import java.time.Instant;
import java.util.Set;

/**
 *
 * @author mohammed
 */
public class User{
    public final String id;
    public final String username;
    public final String fullName;
    public String passwordHashB64; // derived key
    public String saltB64;         // per-user salt
    public int iterations;         // PBKDF2 rounds
    public int keyLenBytes;        // dk length in bytes
    public final Instant createdAt;
    public final Set<Role> roles;
    
    public static enum Role {
        ADMIN, CANDIDATE, STUDENT
    }
    
    public User(String id,String fullName, String username, String passwordHashB64, String saltB64,
                      int iterations, int keyLenBytes, Instant createdAt, Set<Role> roles) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.passwordHashB64 = passwordHashB64;
        this.saltB64 = saltB64;
        this.iterations = iterations;
        this.keyLenBytes = keyLenBytes;
        this.createdAt = createdAt;
        this.roles = roles;
    }

    public void updatePassword(String passwordHashB64, String saltB64, int iterations, int keyLenBytes) {
        this.passwordHashB64 = passwordHashB64;
        this.saltB64 = saltB64;
        this.iterations = iterations;
        this.keyLenBytes = keyLenBytes;
    }
    
    public void addRole(Role role) {
        roles.add(role);
    }

    public void removeRole(Role role) {
        roles.remove(role);
    }

    public boolean hasRole(Role role) {
        return roles.contains(role);
    }

    public Set<Role> getRoles() {
        return roles;
    }
}