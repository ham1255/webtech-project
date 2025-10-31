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
@WebServlet(name = "LogoutServlet", urlPatterns = {"/auth/logout"})
public class LogoutServlet extends HttpServlet {

    @Inject
    private AccountManagerServiceProvider serviceProvider;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        if (session != null) {
            String sid = (String) session.getAttribute("sessionId");

            // 1️⃣ Remove backend (AccountManager) session record
            if (sid != null) {
                try {
                    AccountManager am = serviceProvider.getAccountManager();
                    am.logout(sid); // deletes session from DataStore
                } catch (Exception e) {
                    e.printStackTrace(); // optional logging
                }
            }

            
            session.invalidate();
        }

        
        res.sendRedirect(req.getContextPath() +"/login.jsp");
    }
}
