/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package auth.datastoreimpls;

import auth.*;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author mohammed
 */
public class MemoryAuthDataStore implements AuthDataStore{
    
     private final Map<String, User> usersById = new ConcurrentHashMap<>();
    private final Map<String, User> usersByName = new ConcurrentHashMap<>();
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    @Override
    public void createUser(User user) throws Exception {
        if (usersByName.containsKey(user.username)) {
            throw new IllegalStateException("USERNAME_TAKEN");
        }
        usersById.put(user.id, user);
        usersByName.put(user.username, user);
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        return Optional.ofNullable(usersByName.get(username));
    }

    @Override
    public Optional<User> findUserById(String userId) {
        return Optional.ofNullable(usersById.get(userId));
    }

    @Override
    public void updateUser(User user) {
        usersById.put(user.id, user);
        usersByName.put(user.username, user);
    }

    @Override
    public void createSession(Session session) {
        sessions.put(session.id, session);
    }

    @Override
    public Optional<Session> findSessionById(String sessionId) {
        return Optional.ofNullable(sessions.get(sessionId));
    }

    @Override
    public void deleteSession(String sessionId) {
        sessions.remove(sessionId);
    }

    @Override
    public void deleteSessionsForUser(String userId) {
        sessions.entrySet().removeIf(e -> Objects.equals(e.getValue().userId, userId));
    }
    
    
}
