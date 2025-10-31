/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package auth.servlets;

import auth.AccountManager;
import auth.AccountManagerServiceProvider;
import jakarta.inject.Inject;
import java.io.IOException;
import java.io.PrintWriter;
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
@WebServlet(name = "LoginServlet", urlPatterns = {"/auth/login"})
public class LoginServlet extends HttpServlet {

  
   @Inject
    private AccountManagerServiceProvider serviceProvider;
   
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        AccountManager am = serviceProvider.getAccountManager();
        try {
            String sessionId = am.login(username, password);

            // store sessionId in servlet session or cookie
            HttpSession httpSession = req.getSession(true);
            httpSession.setAttribute("sessionId", sessionId);

            res.sendRedirect(req.getContextPath() +"/index.jsp");
        } catch (Exception e) {
            res.getWriter().println("Login failed: " + e.getMessage());
        }
    }


}
