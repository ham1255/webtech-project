<%-- 
    Document   : login
    Created on : Oct 31, 2025, 8:02:56 PM
    Author     : mohammed
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Login Page</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/style-auth.css">
    </head>
    <body>
        <div class="login-container">
            <h2>Login</h2>
            <%
                // Get attributes sent by servlet
                String username = (String) request.getAttribute("username");
                boolean wrongPassword = request.getAttribute("wrongPassword") != null ? true : false;
            %>
            <form method="POST" action="${pageContext.request.contextPath}/auth/login">
                <input type="text" name="username" placeholder="Username" value="<%= (username != null) ? username : "" %>" required>
                <input type="password" name="password" placeholder="Password" required>
                <button type="submit">Login</button>
            </form>
            <div class="footer">
                <% if (wrongPassword) { %>
                    <p style="color:red;">Incorrect username or password</p>
                <% } %>
            </div>
        </div>
    </body>
</html>
