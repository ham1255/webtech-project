<%-- 
    Document   : app
    Created on : Nov 6, 2025, 9:46:00 PM
    Author     : mohammed
--%>

<%@page import="polling.election.StudentCouncilElectionChair"%>
<%@page import="polling.election.Candidate"%>
<%@page import="java.util.Map"%>
<%@page import="auth.User.Role"%>
<%@page import="java.util.Set"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="polling.election.Election" %>
<%@ page import="java.time.ZoneId" %>
<% String ctx = request.getContextPath(); %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Voting Page</title>
    </head>
    <title>Vote Now</title>

    <style>
        :root {
            /* Colors */
            --bg-gradient-start: #3C8CE7;
            --bg-gradient-end:   #5C62D6;
            --text-main:         #FFFFFF;
            --card-bg:           rgba(255, 255, 255, 0.16);
            --btn-bg:            #FFFFFF;
            --btn-text:          #3C3FE7;

            /* Sizing & layout */
            --page-padding: 20px;
            --card-radius: 18px;
            --transition-fast: 0.2s ease;
        }

        /* ===== Global Layout ===== */

        html,
        body {
            margin: 0;
            padding: 0;
            height: 100%;
            overflow: hidden; /* full-page, no scroll */
            font-family: "Segoe UI", Arial, sans-serif;
            color: var(--text-main);
            background: linear-gradient(135deg, var(--bg-gradient-start), var(--bg-gradient-end));
        }

        .container {
            height: 100vh;
            display: flex;
            flex-direction: column;
            justify-content: flex-start;   /* move upward */
            align-items: center;
            padding-top: 60px;             /* adjust amount */
        }


        /* ===== Header ===== */

        h1 {
            font-size: 2.4rem;
            margin: 0 0 20px 0;
        }

        /* ===== Empty State ===== */

        .no-elections {
            font-size: 1.3rem;
            opacity: 0.9;
            margin-top: 20px;
        }

        /* ===== Election Card ===== */

        .election-box {
            width: 80%;
            max-width: 1250px;
            margin-bottom: 20px;
            padding: 25px;
            box-sizing: border-box;
            display: flex;
            flex-direction: column;
            justify-content: center; /* center content vertically */
            align-items: center;
            background: var(--card-bg);
            backdrop-filter: blur(6px);
            border-radius: var(--card-radius);
        }

        .election-box .name {
            font-size: 1.7rem;
            font-weight: 600;
            margin-bottom: 8px;
        }

        .election-box .status {
            font-size: 1.05rem;
            opacity: 0.9;
            margin-bottom: 8px;
        }

        .election-box .timer {
            font-size: 1rem;
            opacity: 0.95;
        }

        .page-btn {
            background: linear-gradient(135deg, #5C62D6, #43A5BE);
            color: white;
            padding: 8px 18px;
            border-radius: 999px;
            text-decoration: none;
            font-weight: 600;
            transition: all 0.2s ease;

            border: none;              /* remove dark border */
            outline: none;             /* remove focus outline */
            box-shadow: none;
        }

        .page-btn:hover {
            background: rgba(255,255,255,0.25);
            transform: scale(1.05);
        }


        .chair-group {
            margin-top: 12px;
        }

        .chair-option {
            display: flex;
            align-items: center;
            padding: 8px 10px;
            background: #0f172a;
            border: 1px solid #4b5563;
            border-radius: 8px;
            margin-bottom: 8px;
            cursor: pointer;
            transition: 0.15s;
        }

        .chair-option:hover {
            border-color: #22c55e;
            background: #1e293b;
        }

        .chair-option input {
            margin-right: 10px;
        }

        .chair-label-title {
            color: #e5e7eb;
            font-weight: 600;
        }

        .chair-label-sub {
            font-size: 12px;
            color: #9ca3af;
        }


    </style>
    <body>
        <jsp:include page="/app/navbar.jsp"/>
        <%
            // Get attributes sent by servlet
            String name = (String) request.getAttribute("name");
            String id = (String) request.getAttribute("id");
            Set<Role> roles = (Set<Role>) request.getAttribute("roles");
            String appMode = (String) request.getAttribute("appMode");
        %>


        <% if (appMode.equals("election-select")) { %>

        <div class="container">

            <%
                List<Election> elections = (List<Election>) request.getAttribute("elections");

                if (elections == null || elections.isEmpty()) {
            %>
            <div class="no-elections">There are no elections at the moment.</div>

            <%
            } else {
                for (Election e : elections) {

                    long registerationStarts = e.registerationStartsAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    long start = e.getStartsAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    long end = e.getEndsAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    String electionId = e.getElectionId();
            %>

            <div class="election-box"
                 data-registerationstart="<%=registerationStarts%>"
                 data-start="<%=start%>"
                 data-end="<%=end%>"
                 data-election="<%=electionId%>">

                <div class="name"><%= e.getName()%></div>
                <div class="status">loading...</div>
                <div class="timer">Calculating time…</div>
                <div>

                </div>
                <form class="register-form" method="get" action="<%= ctx%>/app/candidate">
                    <input type="hidden" name="appMode" value="election-candidate-register">
                    <input type="hidden" name="electionId" value="<%= e.getElectionId()%>"><br>
                    <button class="page-btn">Register Now!</button>
                </form>



            </div>

            <%
                    }
                }
            %>

        </div>

        <script>
            function format(ms) {
                if (ms <= 0)
                    return "0s";
                let s = Math.floor(ms / 1000);
                let d = Math.floor(s / 86400);
                s %= 86400;
                let h = Math.floor(s / 3600);
                s %= 3600;
                let m = Math.floor(s / 60);
                s %= 60;

                let out = [];
                if (d > 0)
                    out.push(d + "d");
                if (h > 0)
                    out.push(h + "h");
                if (m > 0)
                    out.push(m + "m");
                out.push(s + "s");
                return out.join(" ");
            }

            function updateTimers() {
                let now = Date.now();

                document.querySelectorAll(".election-box").forEach(box => {
                    let registerationStart = parseInt(box.dataset.registerationstart);
                    let start = parseInt(box.dataset.start);
                    let end = parseInt(box.dataset.end);
                    let election = box.dataset.election;
                    const registerForm = box.querySelector(".register-form");
                    let timer = box.querySelector(".timer");
                    let status = box.querySelector(".status");

                    if (registerForm) {
                        if (now >= registerationStart && now < start) {
                            registerForm.style.display = "block";
                        } else {
                            registerForm.style.display = "none";
                        }
                    }


                    if (now < registerationStart) {
                        timer.textContent = "Candidates Registration begins in: " + format(registerationStart - now);
                    } else if (now >= registerationStart && now < start) {
                        status.textContent = "Candidates Registeration period has started.";
                        timer.textContent = "Voting starts in: " + format(start - now);
                    } else if (now >= start && now < end) {
                        timer.textContent = "Registeration closed, election began.";
                        status.textContent = "";
                    } else {
                        timer.textContent = "Election finished.";
                    }
                });
            }

            updateTimers();
            setInterval(updateTimers, 1000);
        </script>

        <% } else if (appMode.equals("election-candidate-register")) {%>
        <a style= "margin-top:10px; display:inline-block;" href="<%= ctx%>/app/candidate" class="page-btn">Go back</a>


        <%

            Election election = (Election) request.getAttribute("election");

        %>
        <div class="container">
            <div class="election-box"> 

                <form method="post" action="<%=ctx%>/app/candidate" class="vote-form">
                    <input type="hidden" name="electionId" value="<%= election.getElectionId()%>">
                    <input type="hidden" name="operation" value="register">


                    <div class="chair-group">
                        <p>Select Your Student Council Chair:</p>

                        <% for (StudentCouncilElectionChair chair : StudentCouncilElectionChair.values()) {%>
                        <label class="chair-option">
                            <input type="radio" name="chair" value="<%= chair.toString()%>" required>
                            <div>
                                <div class="chair-label-title"><%= chair.getDisplayName()%></div>
                                <div class="chair-label-sub">Max seats: <%= chair.getMaxSeats()%></div>
                            </div>
                        </label>
                        <% } %>
                    </div>




                    <br><br><br>
                    <input class="page-btn" type="submit" value="Register now!">
                </form>
            </div>
        </div>
        <%}%>


    </body>
</html>
