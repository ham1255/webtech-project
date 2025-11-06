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
@WebFilter({"/app", "/app/*"})
public class AuthFilter extends HttpFilter  {
    @Inject
    private AccountManagerServiceProvider provider;

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        var session = request.getSession(false);
        if (session == null || session.getAttribute("sessionId") == null) {
            response.sendRedirect(request.getContextPath() +"/auth/login");
            return;
        }

        String sid = (String) session.getAttribute("sessionId");
        try {
            if (provider.getAccountManager().validateSession(sid).isEmpty()) {
                response.sendRedirect(request.getContextPath() +"/auth/login");
                return;
            }
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() +"/auth/login");
            return;
        }

        chain.doFilter(request, response);
    }
}
