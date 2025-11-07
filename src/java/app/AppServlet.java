/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package app;

import auth.AccountManager;
import auth.AccountManagerServiceProvider;
import auth.User;
import jakarta.inject.Inject;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author mohammed
 */
@WebServlet(name = "AppServlet", urlPatterns = 
        {
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
        
        String userId = null;
        try {
            userId = am.validateSession(sid).orElseThrow(() -> new IllegalArgumentException("INVAILD_SESSION"));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        try {
            User user = am.getUserById(userId);
            request.setAttribute("name", user.fullName);
            request.setAttribute("id", user.id);
            request.setAttribute("roles", user.roles);
            
            
            String servletPath = request.getServletPath();
            
            switch (servletPath) {
                case "/app/profile" -> request.getRequestDispatcher("/app/profile.jsp").forward(request, response);
                case "/app/support" -> request.getRequestDispatcher("/app/support.jsp").forward(request, response);
                case  "/app/voting" -> {
                    if (!user.roles.contains(User.Role.STUDENT)) {
                        request.getRequestDispatcher("/app/access_denied.jsp").forward(request, response);
                    } else {
                        request.getRequestDispatcher("/app/voting.jsp").forward(request, response);
                    }
                
                }
                case "/app/candidate" -> {
                
                 if (!user.roles.contains(User.Role.CANDIDATE)) {
                        request.getRequestDispatcher("/app/access_denied.jsp").forward(request, response);
                    } else {
                        request.getRequestDispatcher("/app/candidate.jsp").forward(request, response);
                    }
                
                
                }
                case "/app/admin" -> {
                     if (!user.roles.contains(User.Role.ADMIN)) {
                        request.getRequestDispatcher("/app/access_denied.jsp").forward(request, response);
                    } else {
                        request.getRequestDispatcher("/app/admin.jsp").forward(request, response);
                    }
                }
                
                
                default -> request.getRequestDispatcher("/app/home.jsp").forward(request, response);
            }
            
        } catch (Exception ex) {
             throw new RuntimeException(ex);
        }
        
        
    }

}
