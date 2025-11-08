<%-- 
    Document   : app
    Created on : Nov 6, 2025, 9:46:00 PM
    Author     : mohammed
--%>

<%@page import="auth.User.Role"%>
<%@page import="java.util.Set"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% String ctx = request.getContextPath();%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Candidate</title>
    </head>
    <style>
        
     
    </style>
    <body>
        <jsp:include page="/app/navbar.jsp"/>
        <%
                // Get attributes sent by servlet
                String name = (String) request.getAttribute("name");
                String id = (String) request.getAttribute("id");
                Set<Role> roles = (Set<Role>)request.getAttribute("roles");
            %>
        <p>Dear <%=name%> roles: <%= roles%> there is No active election</p>
    </body>
</html>
