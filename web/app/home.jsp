<%-- 
    Document   : app
    Created on : Nov 6, 2025, 9:46:00 PM
    Author     : mohammed
--%>

<%@page import="auth.User.Role"%>
<%@page import="java.util.Set"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<% String ctx = request.getContextPath(); %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Home Page</title>
    </head>
    <style>
        html, body {
            margin: 0;
            padding: 0;
            height: 100%;
            overflow: hidden; /* prevent any scrolling */
            background: linear-gradient(135deg, #5C62D6, #3C8CE7);
            font-family: "Segoe UI", Arial, sans-serif;
            color: white;
        }

        /* Full screen wrapper */
        .wrapper {
            height: 100vh;
            width: 100%;
            display: flex;
            flex-direction: column;
            justify-content: center;   /* center vertically */
            align-items: center;       /* center horizontally */
            text-align: center;
            padding: 20px;
            box-sizing: border-box;
        }

        h1 {
            font-size: 2.5rem;
            margin-bottom: 10px;
            font-weight: 700;
        }

        p {
            font-size: 1.1rem;
            opacity: 0.9;
            margin-bottom: 25px;
        }

        .btn-vote {
            padding: 16px 40px;
            font-size: 1.2rem;
            border-radius: 50px;
            border: none;
            background: white;
            color: #4A4DE0;
            font-weight: bold;
            cursor: pointer;
            transition: 0.2s ease-in-out;
            margin-bottom: 35px;
        }

        .btn-vote:hover {
            transform: scale(1.05);
            opacity: 0.9;
        }

        .info-box {
            background: rgba(255,255,255,0.13);
            backdrop-filter: blur(6px);
            padding: 18px 25px;
            border-radius: 14px;
            width: 70%;
            max-width: 600px;
            margin-bottom: 30px;
        }

        .creators {
            font-size: 0.95rem;
            opacity: 0.85;
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
        <div class="wrapper">

            <h1>Welcome to the Student Council Elections</h1>
            <p>Your voice matters — participate and shape your future.</p>

            <form method="get" action="<%= ctx%>/app/voting">
                <button class="btn-vote">Vote Now</button>
            </form>

            <div class="info-box">
                <p>
                    ✔ Vote when the election officially opens.<br>
                    ✔ All votes are counted securely and transparently.<br>
                    ✔ Live countdown and election status available.
                </p>
            </div>

            <div class="creators">
                Created by Mohammed & Team – 2025
            </div>

        </div>


    </body>
</html>
