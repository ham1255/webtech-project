/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package app;

import auth.AccountManager;
import auth.AccountManagerServiceProvider;
import auth.AuthDataStore.PageableUsers;
import auth.User;
import auth.User.Role;
import polling.election.Candidate;
import polling.election.Election;
import polling.election.ElectionDataStore;
import polling.election.ElectionServiceProvider;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import polling.election.ElectionDataStore.PageableElections;
import polling.election.StudentCouncilElectionChair;

/**
 *
 * @author mohammed
 */
@WebServlet(name = "AppServlet", urlPatterns
        = {
            "/app", "/app/profile", "/app/support",
            "/app/voting", "/app/candidate", "/app/admin", "/app/admin/random_election"

        }
)
public class AppServlet extends HttpServlet {

    @Inject
    private AccountManagerServiceProvider accountProvider;

    @Inject
    private ElectionServiceProvider electionProvider;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        var session = request.getSession(false);

        String sid = (String) session.getAttribute("sessionId");
        AccountManager am = accountProvider.getAccountManager();
        ElectionDataStore es = electionProvider.getElectionDataStore();

        String vistorUserId = null;
        try {
            vistorUserId = am.validateSession(sid).orElseThrow(() -> new IllegalArgumentException("INVAILD_SESSION"));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        try {
            User vistor = am.getUserById(vistorUserId);
            request.setAttribute("name", vistor.fullName);
            request.setAttribute("id", vistor.id);
            request.setAttribute("roles", vistor.roles);
            String ctx = request.getContextPath();
            String servletPath = request.getServletPath();

            switch (servletPath) {
                case "/app/admin/random_election" -> {

                    if (!vistor.roles.contains(User.Role.ADMIN)) {
                        request.getRequestDispatcher("/app/access_denied.jsp").forward(request, response);
                    } else {

                        Election election = es.createElection("Student council election (2025)", LocalDateTime.now(), LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusMinutes(70));
                        Queue<User> candidatesPossible = new ArrayDeque<>();
                        for (User user : am.getStore().getAllUsers()) {
                            if (user.hasRole(Role.CANDIDATE)) {
                                candidatesPossible.add(user);
                            }
                        }
                        for (User user : candidatesPossible) {
                            Candidate candidate = new Candidate(user.id, StudentCouncilElectionChair.random(), election.getElectionId());
                            es.addCandidate(election, candidate);
                        }
                        response.getWriter().println("done: created election for demo");
                    }
                }
                case "/app/profile" ->
                    request.getRequestDispatcher("/app/profile.jsp").forward(request, response);
                case "/app/support" -> {

                    request.getRequestDispatcher("/app/support.jsp").forward(request, response);

                }
                case "/app/voting" -> {
                    if (!vistor.roles.contains(User.Role.STUDENT)) {
                        request.getRequestDispatcher("/app/access_denied.jsp").forward(request, response);
                    } else {
                        String appMode = request.getParameter("appMode");
                        if (appMode == null || appMode.isEmpty() || appMode.equals("election-select")) {
                            request.setAttribute("appMode", "election-select");
                            request.setAttribute("elections", es.findActiveElections());
                        } else if (appMode.equals("election-json-votes")) {
                            String electionId = request.getParameter("electionId");
                            if (electionId == null || electionId.isEmpty() || es.findElectionById(electionId).isEmpty()) {
                                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Election doesn't exists");
                                return;
                            } else {
                                Election election = es.findElectionById(electionId).get();

                                JsonObjectBuilder mainBuilder = Json.createObjectBuilder();
                                JsonObjectBuilder votesBuilder = Json.createObjectBuilder();
                                JsonObjectBuilder namesBuilder = Json.createObjectBuilder();

                                int votes = es.getTotalVotes(election);
                                for (Map.Entry<StudentCouncilElectionChair, Map<String, Integer>> entry : es.getCandidatesVotesPerChair(election).entrySet()) {

                                    StudentCouncilElectionChair chair = entry.getKey();
                                    Map<String, Integer> candidateVotes = entry.getValue();

                                    JsonObjectBuilder chairObj = Json.createObjectBuilder();

                                    for (Map.Entry<String, Integer> cv : candidateVotes.entrySet()) {
                                        chairObj.add(cv.getKey(), cv.getValue());
                                        namesBuilder.add(cv.getKey(), am.getUserById(cv.getKey()).fullName);
                                    }

                                    votesBuilder.add(chair.getDisplayName(), chairObj);
                                }

                                mainBuilder.add("total_votes", votes);
                                mainBuilder.add("votes", votesBuilder.build());
                                mainBuilder.add("names", namesBuilder.build());

                                JsonObject json = mainBuilder.build();

                                response.setContentType("application/json");
                                response.getWriter().write(json.toString());
                                return;
                            }

                        } else if (appMode.equals("election-vote")) {
                            String electionId = request.getParameter("electionId");
                            request.setAttribute("appMode", "election-vote");
                            Election election = es.findElectionById(electionId).get();

                            if (es.hasUserVoted(election, vistor)) {

                                response.sendRedirect(buildRedirect(ctx, "/app/voting",
                                        Map.of("alert-message", "You have already voted in this election"
                                        )));
                                return;
                            }
                            List<Candidate> candidates = es.findCandidatesByElection(election);

                            Map<StudentCouncilElectionChair, List<Candidate>> candidatesByChair = new LinkedHashMap<>();

                            for (Candidate c : candidates) {
                                User user = am.getUserById(c.getUserID());
                                c.setName(user.fullName);
                                StudentCouncilElectionChair chair = c.getChair();
                                candidatesByChair.computeIfAbsent(chair, k -> new ArrayList<>())
                                        .add(c);
                            }

                            request.setAttribute("candidatesByChair", candidatesByChair);
                            request.setAttribute("election", election);

                        }
                        request.getRequestDispatcher("/app/voting.jsp").forward(request, response);

                    }

                }
                case "/app/candidate" -> {

                    if (!vistor.roles.contains(User.Role.CANDIDATE)) {
                        request.getRequestDispatcher("/app/access_denied.jsp").forward(request, response);
                    } else {

                        String appMode = request.getParameter("appMode");
                        if (appMode == null || appMode.isEmpty() || appMode.equals("election-select")) {
                            request.setAttribute("appMode", "election-select");
                            request.setAttribute("elections", es.findActiveElections());
                        } else if (appMode.equals("election-candidate-register")) {
                            request.setAttribute("appMode", "election-candidate-register");
                            String electionId = request.getParameter("electionId");
                            Optional<Election> electionOptional = es.findElectionById(electionId);
                            if (electionOptional.isEmpty()) {
                                response.sendRedirect(buildRedirect(ctx, "/app/candidate",
                                        Map.of("alert-message", "election doesn't exists"
                                        )));
                                return;
                            }
                            Election election = electionOptional.get();

                            if (es.findCandidateById(election, vistor.id).isPresent()) {
                                response.sendRedirect(buildRedirect(ctx, "/app/candidate",
                                        Map.of("alert-message", "You have already registered in this election"
                                        )));
                                return;
                            }

                            request.setAttribute("election", election);
                        }

                        request.getRequestDispatcher("/app/candidate.jsp").forward(request, response);
                    }

                }
                case "/app/admin" -> {
                    if (!vistor.roles.contains(User.Role.ADMIN)) {
                        request.getRequestDispatcher("/app/access_denied.jsp").forward(request, response);
                    } else {
                        String appMode = request.getParameter("appMode");
                        if (appMode == null || appMode.isEmpty() || appMode.equals("users")) {
                            // users list
                            int page = 1;
                            try {
                                page = Integer.parseInt(request.getParameter("usersPage"));
                            } catch (NumberFormatException ignored) {
                                // ignore
                            }

                            PageableUsers pageable = am.getUsersByPage(page);
                            request.setAttribute("usersPageData", pageable);
                            request.setAttribute("appMode", "users");

                        } else if (appMode.equals("edit-user")) {
                            String editee = request.getParameter("edit-who");

                            User user = am.getUserById(editee);

                            request.setAttribute("edit-who", user);

                            request.setAttribute("appMode", "edit-user");
                        } else if (appMode.equals("elections")) {
                            // users list
                            int page = 1;
                            try {
                                page = Integer.parseInt(request.getParameter("electionsPage"));
                            } catch (NumberFormatException ignored) {
                                // ignore
                            }
                            PageableElections pageable = es.findAllElectionsByPage(page);
                            request.setAttribute("electionsPageData", pageable);
                            request.setAttribute("appMode", "elections");

                        } else if (appMode.equals("edit-election")) {
                            String editee = request.getParameter("edit-who");
                            request.setAttribute("appMode", "edit-election");
                            request.setAttribute("edit-who", es.findElectionById(editee).get());
                        } else if (appMode.equals("create-election")) {
                            request.setAttribute("appMode", "create-election");
                        }

                        request.getRequestDispatcher("/app/admin.jsp").forward(request, response);

                    }
                }

                default -> {
                    request.getRequestDispatcher("/app/home.jsp").forward(request, response);

                }
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    private static final DateTimeFormatter FORMATTER_DATE_HTML = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        var session = request.getSession(false);

        String sid = (String) session.getAttribute("sessionId");
        AccountManager am = accountProvider.getAccountManager();
        ElectionDataStore es = electionProvider.getElectionDataStore();
        String vistorUserId = null;
        try {
            vistorUserId = am.validateSession(sid).orElseThrow(() -> new IllegalArgumentException("INVAILD_SESSION"));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        try {
            User vistor = am.getUserById(vistorUserId);
            request.setAttribute("name", vistor.fullName);
            request.setAttribute("id", vistor.id);
            request.setAttribute("roles", vistor.roles);

            String servletPath = request.getServletPath();
            String ctx = request.getContextPath();

            switch (servletPath) {
                case "/app/admin" -> {
                    if (!vistor.roles.contains(User.Role.ADMIN)) {
                        response.sendError(403, "Access denied");
                    } else {

                        String operation = request.getParameter("operation");
                        switch (operation) {
                            case "update_user" -> {
                                String userId = request.getParameter("user-id");
                                User user = am.getUserById(userId);

                                user.fullName = request.getParameter("full-name");
                                user.email = request.getParameter("email");

                                String[] selectedRoles = request.getParameterValues("roles");

                                if (selectedRoles == null) {
                                    selectedRoles = new String[]{};
                                }

                                Set<Role> newRoleSet = new HashSet<>();
                                if (selectedRoles != null) {
                                    newRoleSet = Arrays.stream(selectedRoles)
                                            .map(User.Role::valueOf)
                                            .collect(Collectors.toSet());
                                }
                                // check if user in a active election.
                                boolean inElection = false;

                                outerLoop:
                                for (Election election : es.findActiveElections()) {
                                    for (Candidate candidate : es.findCandidatesByElection(election)) {
                                        if (candidate.getUserID().equals(user.id)) {
                                            inElection = true;
                                            break outerLoop;
                                        }
                                    }

                                }
                                if (inElection && !newRoleSet.contains(Role.CANDIDATE)) {

                                    response.sendRedirect(buildRedirect(ctx, "/app/admin",
                                            Map.of("alert-message", "User currently in a election(s). We can't remove the role: Candidate",
                                                    "appMode", "edit-user",
                                                    "edit-who", userId
                                            )));
                                } else {

                                    user.roles = newRoleSet;

                                    am.getStore().updateUser(user);

                                    response.sendRedirect(buildRedirect(ctx, "/app/admin",
                                            Map.of("alert-message", "User updated succesfully!",
                                                    "appMode", "edit-user",
                                                    "edit-who", userId
                                            )));

                                }

                            }
                            case "create-election" -> {

                                String name = request.getParameter("name");
                                String regStartStr = request.getParameter("registrationStartsAt");
                                String startStr = request.getParameter("startsAt");
                                String endStr = request.getParameter("endsAt");

                                // Convert to LocalDateTime
                                LocalDateTime registrationStartsAt = LocalDateTime.parse(regStartStr, FORMATTER_DATE_HTML);
                                LocalDateTime startsAt = LocalDateTime.parse(startStr, FORMATTER_DATE_HTML);
                                LocalDateTime endsAt = LocalDateTime.parse(endStr, FORMATTER_DATE_HTML);

                                Election election = es.createElection(name, registrationStartsAt, startsAt, endsAt);

                                response.sendRedirect(buildRedirect(ctx, "/app/admin",
                                        Map.of("alert-message", "Election was created: " + election.getElectionId(),
                                                "appMode", "elections"
                                        )));

                            }
                            case "update-election" -> {

                                String electionId = request.getParameter("election-id");
                                String name = request.getParameter("name");
                                String regStartStr = request.getParameter("registrationStartsAt");
                                String startStr = request.getParameter("startsAt");
                                String endStr = request.getParameter("endsAt");
                                // Convert to LocalDateTime
                                LocalDateTime registrationStartsAt = LocalDateTime.parse(regStartStr, FORMATTER_DATE_HTML);
                                LocalDateTime startsAt = LocalDateTime.parse(startStr, FORMATTER_DATE_HTML);
                                LocalDateTime endsAt = LocalDateTime.parse(endStr, FORMATTER_DATE_HTML);

                                System.out.println(electionId);
                                Election election = es.findElectionById(electionId).get();

                                election.setName(name);
                                election.setStartsAt(startsAt);
                                election.setEndsAt(endsAt);
                                election.setRegisterationStartsAt(registrationStartsAt);

                                es.updateElection(election);

                                response.sendRedirect(buildRedirect(ctx, "/app/admin",
                                        Map.of("alert-message", "Election was updated: " + election.getElectionId(),
                                                "appMode", "elections"
                                        )));

                            }
                            case "delete-election" -> {

                                String electionId = request.getParameter("election-id");

                                es.deleteElection(electionId);

                                response.sendRedirect(buildRedirect(ctx, "/app/admin",
                                        Map.of("alert-message", "Election was deleted: " + electionId,
                                                "appMode", "elections"
                                        )));

                            }
                            default -> {

                                response.sendRedirect(buildRedirect(ctx, "/app/admin",
                                        Map.of("alert-message", "Wrong request?",
                                                "appMode", "users"
                                        )));
                            }

                        }

                    }
                }
                case "/app/voting" -> {
                    if (!vistor.roles.contains(User.Role.STUDENT)) {
                        response.sendError(403, "Access denied");
                    } else {
                        String operation = request.getParameter("operation");
                        if (operation.equals("vote")) {
                            User voter = vistor;
                            String electionId = request.getParameter("electionId");
                            Election election = es.findElectionById(electionId).get();
                            if (es.hasUserVoted(election, voter)) {

                                response.sendRedirect(buildRedirect(ctx, "/app/voting",
                                        Map.of("alert-message", "You have already voted in this election"
                                        )));
                                return;
                            }
                            for (StudentCouncilElectionChair chair : StudentCouncilElectionChair.values()) {
                                String candidateId = request.getParameter(chair.toString());
                                Optional<Candidate> optional = es.findCandidateById(election, candidateId);
                                if (optional.isEmpty()) {
                                    continue;
                                }

                                Candidate candidate = optional.get();

                                es.addVoteToCandidate(election, voter, candidate);
                            }
                            response.sendRedirect(buildRedirect(ctx, "/app/voting",
                                    Map.of("alert-message", "You have voted successfully"
                                    )));
                        }
                    }
                }
                case "/app/candidate" -> {
                    if (!vistor.roles.contains(User.Role.CANDIDATE)) {
                        response.sendError(403, "Access denied");
                    } else {
                        String operation = request.getParameter("operation");
                        if (operation.equals("register")) {
                            String chairParam = request.getParameter("chair");
                            if (chairParam == null || chairParam.isBlank()) {
                                throw new IllegalArgumentException("No chair selected");
                            }

                            StudentCouncilElectionChair chair = StudentCouncilElectionChair.valueOf(chairParam);

                            String electionId = request.getParameter("electionId");

                            Optional<Election> electionOptional = es
                                    .findElectionById(electionId);

                            if (electionOptional.isEmpty()) {
                                response.sendRedirect(buildRedirect(ctx, "/app/candidate",
                                        Map.of("alert-message", "Election doesn't exists",
                                                "appMode", "election-select"
                                        )));
                                return;
                            }

                            Election election = electionOptional.get();
                            Candidate candidate = new Candidate(
                                    vistor.id,
                                    chair,
                                    election.getElectionId()
                            );

                            es.addCandidate(election, candidate);
                            response.sendRedirect(buildRedirect(ctx, "/app/candidate",
                                    Map.of("alert-message", "you have been registered in the election.",
                                            "appMode", "election-select"
                                    )));
                        }
                    }

                }
                default ->
                    response.sendRedirect("/app");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }

    }

    public static String buildRedirect(String ctx, String path, Map<String, String> params) {
        StringBuilder sb = new StringBuilder().append(ctx).append(path);
        if (params != null && !params.isEmpty()) {
            sb.append("?");
            params.forEach((k, v) -> {
                if (v != null) {
                    sb.append(URLEncoder.encode(k, StandardCharsets.UTF_8))
                            .append("=")
                            .append(URLEncoder.encode(v, StandardCharsets.UTF_8))
                            .append("&");
                }
            });
            sb.setLength(sb.length() - 1); // remove last &
        }
        return sb.toString();
    }

}
