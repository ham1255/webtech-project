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
        <link rel="stylesheet" href="style-auth.css">
    </head>
    <body>
        <div class="login-container">
            <h2>Login</h2>
            <form method="POST" action="auth/login">
                <input type="text" name="username" placeholder="Username" required>
                <input type="password" name="password" placeholder="Password" required>
                <button type="submit">Login</button>
            </form>
            <div class="footer">
                <!-- <p>Don't have an account? <a href="#">Sign up</a></p>  ddd-->
            </div>
        </div>
    </body>
</html>
