/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package auth.filters;

import auth.AccountManagerServiceProvider;
import jakarta.inject.Inject;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * @author mohammed
 */
@WebFilter("/index.jsp")
public class AuthFilter extends HttpFilter  {
    @Inject
    private AccountManagerServiceProvider provider;

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        var session = req.getSession(false);
        if (session == null || session.getAttribute("sessionId") == null) {
            res.sendRedirect("login.jsp");
            return;
        }

        String sid = (String) session.getAttribute("sessionId");
        try {
            if (provider.getAccountManager().validateSession(sid).isEmpty()) {
                res.sendRedirect("login.jsp");
                return;
            }
        } catch (Exception e) {
            res.sendRedirect("login.jsp");
            return;
        }

        chain.doFilter(req, res);
    }
}
