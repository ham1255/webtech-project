<%-- 
    Document   : app
    Created on : Nov 6, 2025, 9:46:00 PM
    Author     : mohammed
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>403</title>
    </head>
    <style>
        .access-denied {
            background: rgba(255,255,255,0.15);
            border-radius: 16px;
            padding: 30px;
            margin: 50px auto;
            max-width: 600px;
            color: #fff;
            text-align: center;
            box-shadow: 0 4px 12px rgba(0,0,0,0.3);
            backdrop-filter: blur(5px);
        }

        .access-denied h2 {
            font-size: 28px;
            margin-bottom: 10px;
        }

        .access-denied p {
            margin-bottom: 25px;
            font-size: 16px;
        }


    </style>
    <body>
        <jsp:include page="/app/navbar.jsp"/>
        <div class="access-denied">
            <h2>Access Denied</h2>
            <p>You don’t have permission to access this page.</p>
        </div>
    </body>
</html>
