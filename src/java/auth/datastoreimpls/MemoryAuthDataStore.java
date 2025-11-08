/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package auth.datastoreimpls;

import auth.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 *
 * @author mohammed
 */
public class MemoryAuthDataStore implements AuthDataStore{
    
     private final Map<String, User> usersById = new ConcurrentHashMap<>();
    private final Map<String, User> usersByEmail = new ConcurrentHashMap<>();
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    @Override
    public void createUser(User user) throws Exception {
        if (usersByEmail.containsKey(user.email)) {
            throw new IllegalStateException("EMAIL_TAKEN");
        }
        usersById.put(user.id, user);
        usersByEmail.put(user.email, user);
    }

    @Override
    public Optional<User> findUserByEmail(String username) {
        return Optional.ofNullable(usersByEmail.get(username));
    }

    @Override
    public Optional<User> findUserById(String userId) {
        return Optional.ofNullable(usersById.get(userId));
    }

    @Override
    public void updateUser(User user) {
        usersById.put(user.id, user);
        usersByEmail.put(user.email, user);
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

    @Override
    public Set<User> getAllUsers() {
        return new HashSet<>(this.usersById.values());
    }

    @Override
    public PageableUsers getUsersByPage(int page) {
        if (page < 1) page = 1;

        List<User> userList = new ArrayList<>(usersById.values());
        int totalUsers = userList.size();
        int maxPages = (int) Math.ceil((double) totalUsers / PageableUsers.MAX_PAGE_SIZE);

        if (totalUsers == 0) {
            return new PageableUsers(Collections.emptySet(), 0, page);
        }

        int fromIndex = (page - 1) * PageableUsers.MAX_PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PageableUsers.MAX_PAGE_SIZE, totalUsers);

        if (fromIndex >= totalUsers) {
            // if page number too large, return last page
            fromIndex = Math.max(0, (maxPages - 1) * PageableUsers.MAX_PAGE_SIZE);
            toIndex = totalUsers;
        }

        Set<User> pageUsers = userList.subList(fromIndex, toIndex)
                                      .stream()
                                      .collect(Collectors.toCollection(LinkedHashSet::new));

        return new PageableUsers(pageUsers, maxPages, page);
    }
    
    
}
