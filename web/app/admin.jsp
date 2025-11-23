<%-- 
    Document   : app
    Created on : Nov 6, 2025, 9:46:00 PM
    Author     : mohammed
--%>

<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="polling.election.ElectionDataStore.PageableElections"%>
<%@page import="polling.election.*"%>
<%@page import="auth.User"%>
<%@page import="auth.User.Role"%>
<%@page import="java.util.Set"%>
<%@ page import="auth.AuthDataStore.PageableUsers" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% String ctx = request.getContextPath();%>
<% String appMode = (String) request.getAttribute("appMode");%>
<% DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");%>

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

        .page-btn-red {
            background: linear-gradient(135deg, #D62323, #BE4343); /* red gradient */
            color: white;
            padding: 8px 18px;
            border-radius: 999px;
            text-decoration: none;
            font-weight: 600;
            transition: all 0.2s ease;
            border: none;
            outline: none;
            box-shadow: none;
        }

        .page-btn-red:hover {
            background: rgba(255, 255, 255, 0.25);
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

        input[type="datetime-local"] {
            width: 100%;
            padding: 12px 14px;
            margin-bottom: 20px;
            border: 1px solid #ccc;
            border-radius: 10px;

            font-size: 15px;

            background: #fff;
            transition: 0.25s ease;
            box-sizing: border-box;
        }
    </style>
    <body>
        <jsp:include page="/app/navbar.jsp"/>
        <h1>Debug: admin app mode: <%= appMode%></h1>

        <% if (appMode.equals("users") || appMode.equals("elections")) {%>
        <a style= "margin-top:10px; display:inline-block;" href="<%= ctx%>/app/admin?appMode=users" class="page-btn">Edit users</a>
        <a style= "margin-top:10px; display:inline-block;" href="<%= ctx%>/app/admin?appMode=elections" class="page-btn">Edit elections</a>
        <% }%>


        <% if (appMode.equals("edit-user")) {%>
        <a style= "margin-top:10px; display:inline-block;" href="<%= ctx%>/app/admin?appMode=users" class="page-btn">Go back</a>
        <% }%>
        <% if (appMode.equals("edit-election") || appMode.equals("create-election")) {%>
        <a style= "margin-top:10px; display:inline-block;" href="<%= ctx%>/app/admin?appMode=elections" class="page-btn">Go back</a>
        <% }%>


        <% if (appMode.equals("users")) {%>



        <div class="container">
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
                <span>Page <%= currentPage%> of <%= maxPages > 1 ? maxPages : 1%></span>
                <%
                    if (currentPage < maxPages) {
                %>
                <a href="<%= ctx%>/app/admin?usersPage=<%= currentPage + 1%>" class="page-btn">Next</a>
                <%
                    }
                %>
            </div>
        </div>





        <% } else if (appMode.equals("edit-user")) { %>

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
        <% } else if (appMode.equals("elections")) {%>


        <div class="container">
            <%
                PageableElections pageData = (PageableElections) request.getAttribute("electionsPageData");

                if (pageData == null) {
                    out.println("<p style='color:red;text-align:center;'>No elections were found. or page data not provided.</p>");
                    return;
                }

                Set<Election> elections = pageData.getElections();
                int currentPage = pageData.getCurrentPage();
                int maxPages = pageData.getMaxPages();
            %>
            <h2>Election List</h2> 
            <p><a href="<%= ctx%>/app/admin?appMode=create-election" class="page-btn">Create election</a><p>
            <table class="user-table">
                <thead>
                    <tr>
                        <th>Election ID</th>
                        <th>Name</th>
                        <th></th>
                        <th></th>

                    </tr>
                </thead>
                <tbody>
                    <%  if (elections != null && !elections.isEmpty()) {
                            for (Election election : elections) {
                    %>
                    <tr>
                        <td><%= election.getElectionId()%></td>
                        <td><%= election.getName()%></td>
                        <td><a href="<%= ctx%>/app/admin?appMode=edit-election&edit-who=<%= election.getElectionId()%>" class="page-btn">edit</a></td>
                        <td> <form action="<%=ctx%>/app/admin" method="post">
                                <input type="hidden" name="operation" value="delete-election">
                                <input type="hidden" name="election-id" value="<%= election.getElectionId()%>">
                                <input type="submit" value="Delete" class="page-btn-red">
                            </form></td>
                    </tr>
                    <%
                        }
                    } else {
                    %>
                    <tr><td colspan="4" style="text-align:center;">No elections available.</td></tr>
                    <%
                        }
                    %>
                </tbody>
            </table>




            <!-- Pagination -->
            <div class="pagination">
                <%            if (currentPage > 1) {
                %>
                <a href="<%= ctx%>/app/admin?appMode=elections&electionsPage=<%= currentPage - 1%>" class="page-btn">Previous</a>
                <%
                    }
                %>
                <span>Page <%= currentPage%> of <%= maxPages > 1 ? maxPages : 1%></span>
                <%
                    if (currentPage < maxPages) {
                %>
                <a href="<%= ctx%>/app/admin?appMode=elections&electionsPage=<%= currentPage + 1%>" class="page-btn">Next</a>
                <%
                    }
                %>
            </div>
        </div>




        <% } else if (appMode.equals("create-election")) {%>

        <div class="container">
            <form custom-type="electionForm" action="<%=ctx%>/app/admin" method="post" class="update-form">
                <input type="hidden" name="operation" value="create-election">
                <label>Election Name:</label>
                <input type="text" name="name" required>

                <label>Registration Starts At:</label>
                <input type="datetime-local" name="registrationStartsAt" id="registrationStartsAt" required>

                <label>Starts At:</label>
                <input type="datetime-local" name="startsAt" id="startsAt" required>

                <label>Ends At:</label>
                <input type="datetime-local" name="endsAt" id="endsAt" required>

                <input type="submit" value="Create election">
            </form>
        </div>

        <% } else if (appMode.equals("edit-election")) {%>

        <div class="container">
            <form custom-type="electionForm" action="<%=ctx%>/app/admin" method="post" class="update-form">
                <% Election election = (Election) request.getAttribute("edit-who");%>

                <input type="hidden" name="operation" value="update-election">
                <input type="hidden" name="election-id" value="<%=election.getElectionId()%>">

                <label>Election ID: <%=election.getElectionId()%></label>

                <label>Election Name:</label>
                <input type="text" name="name" value="<%= election.getName()%>" required>

                <label>Registration Starts At:</label>
                <input type="datetime-local" name="registrationStartsAt" id="registrationStartsAt" value="<%= election.registerationStartsAt().format(dtf)%>" required>

                <label>Starts At:</label>
                <input type="datetime-local" name="startsAt" id="startsAt" value="<%= election.getStartsAt().format(dtf)%>" required>

                <label>Ends At:</label>
                <input type="datetime-local" name="endsAt" id="endsAt" value="<%= election.getEndsAt().format(dtf)%>" required>

                <input type="submit" value="Update election">
            </form>
        </div>


        <% }%>

        <script>
            document.addEventListener("DOMContentLoaded", () => {

                const electionForms = document.querySelectorAll("form[custom-type='electionForm']");

                electionForms.forEach(form => {

                    form.addEventListener("submit", function (e) {

                        const reg = new Date(form.querySelector("[name='registrationStartsAt']").value);
                        const start = new Date(form.querySelector("[name='startsAt']").value);
                        const end = new Date(form.querySelector("[name='endsAt']").value);

                        if (reg >= start) {
                            e.preventDefault();
                            alert("Registration start time must be BEFORE the election start time.");
                            return false;
                        }

                        if (start >= end) {
                            e.preventDefault();
                            alert("Election start time must be BEFORE the election end time.");
                            return false;
                        }

                    });

                });

            });
        </script>

    </body>
</html>
