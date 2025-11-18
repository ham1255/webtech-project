/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package auth;

import database.Pageable;
import java.util.Optional;
import java.util.Set;

/**
 *
 * @author mohammed
 */
public interface AuthDataStore {
    
    public static class PageableUsers extends Pageable<User> {

        public PageableUsers(Set<User> users, int maxPages, int currentPage) {
            super(users, maxPages, currentPage);
        }
        
        public Set<User> getUsers() {
            return getT();
        }

    }
    
     // Users
    void createUser(User user) throws Exception;
    Optional<User> findUserByEmail(String email) throws Exception;
    Optional<User> findUserById(String userId) throws Exception;
    void updateUser(User user) throws Exception;
    Set<User> getAllUsers();
    PageableUsers getUsersByPage(int page);
    

    // Sessions
    void createSession(Session session) throws Exception;
    Optional<Session> findSessionById(String sessionId) throws Exception;
    void deleteSession(String sessionId) throws Exception;
    void deleteSessionsForUser(String userId) throws Exception;
    
    
}
