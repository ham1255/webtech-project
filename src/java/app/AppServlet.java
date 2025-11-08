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
import jakarta.inject.Inject;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author mohammed
 */
@WebServlet(name = "AppServlet", urlPatterns
        = {
            "/app", "/app/profile", "/app/support",
            "/app/voting", "/app/candidate", "/app/admin"

        }
)
public class AppServlet extends HttpServlet {

    @Inject
    private AccountManagerServiceProvider provider;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        var session = request.getSession(false);

        String sid = (String) session.getAttribute("sessionId");
        AccountManager am = provider.getAccountManager();

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

            switch (servletPath) {
                case "/app/profile" ->
                    request.getRequestDispatcher("/app/profile.jsp").forward(request, response);
                case "/app/support" ->
                    request.getRequestDispatcher("/app/support.jsp").forward(request, response);
                case "/app/voting" -> {
                    if (!vistor.roles.contains(User.Role.STUDENT)) {
                        request.getRequestDispatcher("/app/access_denied.jsp").forward(request, response);
                    } else {
                        request.getRequestDispatcher("/app/voting.jsp").forward(request, response);
                    }

                }
                case "/app/candidate" -> {

                    if (!vistor.roles.contains(User.Role.CANDIDATE)) {
                        request.getRequestDispatcher("/app/access_denied.jsp").forward(request, response);
                    } else {
                        request.getRequestDispatcher("/app/candidate.jsp").forward(request, response);
                    }

                }
                case "/app/admin" -> {
                    if (!vistor.roles.contains(User.Role.ADMIN)) {
                        request.getRequestDispatcher("/app/access_denied.jsp").forward(request, response);
                    } else {
                        String appMode = request.getParameter("appMode");
                        if (appMode == null || appMode.isEmpty() || appMode.equals("main")) {
                            // users list
                            int page = 1;
                            try {
                                page = Integer.parseInt(request.getParameter("usersPage"));
                            } catch (NumberFormatException ignored) {
                                // ignore
                            }

                            PageableUsers pageable = am.getUsersByPage(page);
                            request.setAttribute("usersPageData", pageable);
                            request.setAttribute("appMode", "main");

                        } else if (appMode.equals("edit-user")) {
                            String editee = request.getParameter("edit-who");

                            User user = am.getUserById(editee);

                            request.setAttribute("edit-who", user);

                            request.setAttribute("appMode", "edit-user");
                        } else {
                            System.out.println("No mode: " + appMode);
                        }

                        request.getRequestDispatcher("/app/admin.jsp").forward(request, response);

                    }
                }

                default ->
                    request.getRequestDispatcher("/app/home.jsp").forward(request, response);
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        var session = request.getSession(false);

        String sid = (String) session.getAttribute("sessionId");
        AccountManager am = provider.getAccountManager();

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
                        if ("update_user".equals(operation)) {
                            String userId = request.getParameter("user-id");
                            User user = am.getUserById(userId);
                            
                            user.fullName = request.getParameter("full-name");
                            user.email = request.getParameter("email");

                            String[] selectedRoles = request.getParameterValues("roles");
                            
                            if (selectedRoles == null) selectedRoles = new String[]{};
                               
                            Set<Role> newRoleSet = new HashSet<>();
                            if (selectedRoles != null) {
                                newRoleSet = Arrays.stream(selectedRoles)
                                        .map(User.Role::valueOf)
                                        .collect(Collectors.toSet());
                            }
                            user.roles = newRoleSet;
                            
                            am.getStore().updateUser(user);

                            response.sendRedirect(buildRedirect(ctx, "/app/admin",
                                    Map.of("alert-message", "User updated succesfully!",
                                            "appMode", "edit-user",
                                            "edit-who", userId
                                    )));

                        } else {
                            response.sendRedirect(buildRedirect(ctx, "/app/admin",
                                    Map.of("alert-message", "Wrong request?",
                                            "appMode", "main"
                                    )));
                        }

                    }
                }

                default ->
                    response.sendRedirect("/app");
            }

        } catch (Exception ex) {
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
