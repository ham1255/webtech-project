<%-- 
    Document   : profile
    Created on : Nov 7, 2025, 10:18:13 PM
    Author     : mohammed
--%>

<%@page import="auth.User.Role"%>
<%@page import="java.util.Set"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Profile</title>
    </head>
    <style>

        .container {
            background: rgba(255, 255, 255, 0.15);
            border-radius: 20px;
            padding: 30px;
            display: block; /* must be block or flex/grid */
            box-shadow: 0 4px 10px rgba(0,0,0,0.3);
            margin: 0 auto; /* centers horizontally */
            width: fit-content; /* optional, shrinks to content width */
            margin-top: 80px; /* 👈 pushes it down from the top/navbar */

        }
        ul {
            list-style-type: none;
            padding: 0;
        }
        li {
            background: rgba(255,255,255,0.2);
            margin: 5px;
            border-radius: 10px;
            padding: 5px 10px;
        }
    </style>
    <body>
        <jsp:include page="/app/navbar.jsp"/>
        
        <%
            // Get attributes sent by servlet
            String name = (String) request.getAttribute("name");
            String id = (String) request.getAttribute("id");
            Set<Role> roles = (Set<Role>) request.getAttribute("roles");
        %>

        <div class="container">
            <h1>User Profile</h1>
            <p><strong>Name:</strong> <%=name%></p>
            <p><strong>ID:</strong> <%=id%></p>

            <h3>Roles:</h3>
            <ul>
                <%
                    if (roles != null) {
                        for (Role role : roles) {
                %>
                <li><%= role%></li>
                    <%
                        }
                    } else {
                    %>
                <li>No roles assigned.</li>
                    <%
                        }
                    %>
            </ul>
        </div>



    </body>
</html>