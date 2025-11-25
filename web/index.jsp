<%-- 
    Document   : index
    Created on : Nov 25, 2025, 1:45:12 PM
    Author     : mohammed
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <%
        String ctx = request.getContextPath();
        response.sendRedirect(ctx + "/login");
    %>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
    </body>
</html>
