<%-- 
    Document   : app
    Created on : Nov 6, 2025, 9:46:00 PM
    Author     : mohammed
--%>

<%@page import="election.*"%>
<%@page import="auth.User"%>
<%@page import="auth.User.Role"%>
<%@page import="java.util.Set"%>
<%@ page import="auth.AuthDataStore.PageableUsers" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% String ctx = request.getContextPath();%>
<% String appMode = (String) request.getAttribute("appMode");%>


<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Admin Page</title>
    </head>
    <style>

        .container {
            background: rgba(255,255,255,0.15);
            backdrop-filter: blur(6px);
            border-radius: 16px;
            padding: 40px;
            margin: 50px auto;
            color: #fff;
            box-shadow: 0 6px 18px rgba(0,0,0,0.3);
            font-family: Arial, sans-serif;
            max-width: 1300px;
        }

        h2 {
            text-align: center;
            margin-bottom: 25px;
        }

        .user-table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 25px;
        }

        .user-table th,
        .user-table td {
            padding: 12px;
            border-bottom: 1px solid rgba(255,255,255,0.2);
            text-align: left;
        }

        .user-table th {
            background: rgba(255,255,255,0.15);
        }

        .pagination {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 20px;
        }

        .page-btn {
            background: linear-gradient(135deg, #5C62D6, #43A5BE);
            color: white;
            padding: 8px 18px;
            border-radius: 999px;
            text-decoration: none;
            font-weight: 600;
            transition: all 0.2s ease;
        }

        .page-btn:hover {
            background: rgba(255,255,255,0.25);
            transform: scale(1.05);
        }


        .update-form {
            display: flex;
            flex-direction: column;
            gap: 14px;
        }

        .update-form h2 {
            text-align: center;
            margin-bottom: 20px;
        }

        .update-form label {
            font-weight: 600;
            margin-top: 8px;
        }

        .update-form input[type="text"],
        .update-form input[type="email"] {
            width: 100%;
            padding: 12px 14px;
            border: none;
            border-radius: 10px;
            font-size: 15px;
            outline: none;
            box-sizing: border-box;
        }

        .roles-box {
            display: flex;
            flex-direction: column;
            gap: 6px;
            background: rgba(255,255,255,0.1);
            padding: 10px 15px;
            border-radius: 10px;
        }

        .roles-box label {
            display: flex;
            align-items: center;
            gap: 8px;
            font-weight: normal;
        }

        .roles-box input[type="checkbox"] {
            accent-color: #43A5BE; /* modern browsers */
            width: 18px;
            height: 18px;
            cursor: pointer;
        }

        .update-form input[type="submit"] {
            background: linear-gradient(135deg, #5C62D6, #43A5BE);
            border: none;
            border-radius: 999px;
            color: #fff;
            padding: 12px 22px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            margin-top: 15px;
            transition: all 0.2s ease;
            align-self: center;
        }

        .update-form input[type="submit"]:hover {
            transform: scale(1.05);
            background: rgba(255, 255, 255, 0.25);
        }

    </style>
    <body>
        <% String alertMessage = (String) request.getParameter("alert-message");%>
        <%
            if (alertMessage != null) {
        %>
        <script>
        alert("<%= alertMessage%>");
        
         if (window.history.replaceState) {
            const url = new URL(window.location);
            url.searchParams.delete('alert-message');
            window.history.replaceState({}, document.title, url.pathname + url.search);
        }
        
        </script>
        <%
            }
        %>
        
        <jsp:include page="/app/navbar.jsp"/>

        <% if (!appMode.equals("main")) {%>
        <a style= "margin-top:10px; display:inline-block;" href="<%= ctx%>/app/admin" class="page-btn">Go back</a>
        <% }%>

        <h1>Debug: admin app mode: <%= appMode%></h1>
        <% if (appMode.equals("main")) {%>


        <%
            PageableUsers pageData = (PageableUsers) request.getAttribute("usersPageData");

            if (pageData == null) {
                out.println("<p style='color:red;text-align:center;'>No users found or page data not provided.</p>");
                return;
            }

            Set<User> users = pageData.getUsers();
            int currentPage = pageData.getCurrentPage();
            int maxPages = pageData.getMaxPages();
        %>



        <div class="container">
            <h2>Users List</h2>

            <table class="user-table">
                <thead>
                    <tr>
                        <th>User ID</th>
                        <th>Name</th>
                        <th>Roles</th>
                        <th></th>

                    </tr>
                </thead>
                <tbody>
                    <%  if (users != null && !users.isEmpty()) {
                            for (User user : users) {
                    %>
                    <tr>
                        <td><%= user.id%></td>
                        <td><%= user.fullName%></td>
                        <td><%= user.roles%></td>
                        <td><a href="<%= ctx%>/app/admin?appMode=edit-user&edit-who=<%= user.id%>" class="page-btn">edit</a></td>

                    </tr>
                    <%
                        }
                    } else {
                    %>
                    <tr><td colspan="2" style="text-align:center;">No users available.</td></tr>
                    <%
                        }
                    %>
                </tbody>
            </table>




            <!-- Pagination -->
            <div class="pagination">
                <%            if (currentPage > 1) {
                %>
                <a href="<%= ctx%>/app/admin?usersPage=<%= currentPage - 1%>" class="page-btn">Previous</a>
                <%
                    }
                %>
                <span>Page <%= currentPage%> of <%= maxPages%></span>
                <%
                    if (currentPage < maxPages) {
                %>
                <a href="<%= ctx%>/app/admin?usersPage=<%= currentPage + 1%>" class="page-btn">Next</a>
                <%
                    }
                %>
            </div>
        </div>
        

            
          

        <% }
            if (appMode.equals("edit-user")) { %>

        <% User user = (User) request.getAttribute("edit-who");%>

        <p> edit mode user: <%= user.fullName%></p>

        <div class="container"> 

            <form method="post" action="<%=ctx%>/app/admin" class="update-form">
                <input type="hidden" name="operation" value="update_user">
                <input type="hidden" name="user-id" value="<%=user.id%>">
                <label>Full Name:</label>
                <input type="text" name="full-name" placeholder="Enter full name for the user" value="<%=user.fullName%>"><br>
                <label>Email:</label>
                <input type="email" name="email" value="<%=user.email%>">

                <label>Roles:</label>
                <div class="roles-box">
                    <label><input type="checkbox" name="roles" value="STUDENT"

                                  <%= user.roles.contains(User.Role.STUDENT) ? "checked" : ""%>

                                  > Student</label>
                    <label><input type="checkbox" name="roles" value="ADMIN"

                                  <%= user.roles.contains(User.Role.ADMIN) ? "checked" : ""%>

                                  > Admin</label>
                    <label><input type="checkbox" name="roles" value="CANDIDATE"

                                  <%= user.roles.contains(User.Role.CANDIDATE) ? "checked" : ""%>

                                  > Candidate</label>
                </div>

                <input type="submit" value="Update User">
            </form>


        </div>
        <% }%>

    </body>
</html>
