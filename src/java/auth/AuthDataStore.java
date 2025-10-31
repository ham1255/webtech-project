/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package auth;

import java.util.Optional;

/**
 *
 * @author mohammed
 */
public interface AuthDataStore {
    
     // Users
    void createUser(User user) throws Exception;
    Optional<User> findUserByUsername(String username) throws Exception;
    Optional<User> findUserById(String userId) throws Exception;
    void updateUser(User user) throws Exception;

    // Sessions
    void createSession(Session session) throws Exception;
    Optional<Session> findSessionById(String sessionId) throws Exception;
    void deleteSession(String sessionId) throws Exception;
    void deleteSessionsForUser(String userId) throws Exception;
    
    
}
