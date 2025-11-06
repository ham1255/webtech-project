/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package auth.servlets;

import auth.AccountManager;
import auth.AccountManagerServiceProvider;
import jakarta.inject.Inject;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author mohammed
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/auth/login", "/login"})
public class LoginServlet extends HttpServlet {

  
   @Inject
    private AccountManagerServiceProvider serviceProvider;
   
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        AccountManager am = serviceProvider.getAccountManager();
        try {
            String sessionId = am.login(username, password);

            // store sessionId in servlet session or cookie
            HttpSession httpSession = request.getSession(true);
            httpSession.setAttribute("sessionId", sessionId);

            response.sendRedirect(request.getContextPath() +"/app");
        } catch (Exception e) {
            request.setAttribute("wrongPassword", "1");
            request.setAttribute("username", username);
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
                
                request.getRequestDispatcher("/login.jsp").forward(request, response);

    }
    
      

}
