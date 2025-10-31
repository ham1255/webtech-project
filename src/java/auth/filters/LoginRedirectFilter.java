/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package auth.filters;

import auth.AccountManager;
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
@WebFilter("/login.jsp")
public class LoginRedirectFilter extends HttpFilter {

    @Inject
    private AccountManagerServiceProvider serviceProvider;

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        var session = req.getSession(false);

        if (session != null) {
            String sid = (String) session.getAttribute("sessionId");
            if (sid != null) {
                try {
                    AccountManager am = serviceProvider.getAccountManager();
                    if (am.validateSession(sid).isPresent()) {
                        res.sendRedirect("index.jsp");
                        return;
                    }
                } catch (Exception e) {
                    // ignore — continue to login.jsp
                }
            }
        }

        chain.doFilter(req, res);
    }
}
