<%-- 
    Document   : index
    Created on : Oct 31, 2025, 8:40:19 PM
    Author     : mohammed
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ include file="navbar.jsp" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello World!</h1>
         <form action="auth/logout" method="post">
         <button type="submit">Logout</button>
        </form>
    </body>
</html>
