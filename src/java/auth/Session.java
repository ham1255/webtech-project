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
public class Session {
    public final String id;      // opaque session token (store server-side)
    public final String userId;
    public final Instant createdAt;
    public final Instant expiresAt;

    public Session(String id, String userId, Instant createdAt, Instant expiresAt) {
        this.id = id;
        this.userId = userId;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }
}
