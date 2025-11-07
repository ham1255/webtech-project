<%-- 
    Document   : app
    Created on : Nov 6, 2025, 9:46:00 PM
    Author     : mohammed
--%>

<%@page import="auth.User.Role"%>
<%@page import="java.util.Set"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Support Page</title>
    </head>
    <style>
        .support-card {
            width: 600px; /* increased width */
            margin: 80px auto; /* center horizontally */
            padding: 40px; /* more padding */
            border-radius: 20px;
            background: rgba(255, 255, 255, 0.15);
            box-shadow: 0 6px 18px rgba(0,0,0,0.35);
            backdrop-filter: blur(6px);
            text-align: left;
            font-family: Arial, sans-serif;
            color: #fff;
        }

        .support-card h2 {
            text-align: center;
            margin-bottom: 25px;
            font-size: 26px;
        }

        .user-info {
            background: rgba(255,255,255,0.2);
            padding: 15px;
            border-radius: 12px;
            margin-bottom: 20px;
            font-size: 15px;
        }

        .textarea {
            width: 100%;
            height: 220px; /* increased height */
            border: none;
            border-radius: 12px;
            padding: 15px;
            font-size: 16px;
            resize: vertical;
            outline: none;
            box-sizing: border-box;
        }

        .button {
            background: linear-gradient(135deg, #5C62D6, #43A5BE);
            border: none;
            border-radius: 10px;
            color: white;
            font-size: 16px;
            padding: 12px 30px;
            cursor: pointer;
            margin-top: 15px;
            display: block;
            margin-left: auto;
        }

        button:hover {
            opacity: 0.9;
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
        <div class="support-card">
            <h2>Contact for Support</h2>

            <div class="user-info">
                <p><strong>Name</strong> <%= name%></p>
                <p><strong>ID:</strong> <%= id%></p>
            </div>

            <form action="<%= request.getContextPath()%>/app" method="get">
                <textarea class="textarea" id="message" name="message" rows="6" placeholder="Type your message..." required></textarea><br>
                <button class="button" type="submit">Send</button>
            </form>
        </div>
    </body>
</html>
