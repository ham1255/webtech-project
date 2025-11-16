<%-- 
    Document   : app
    Created on : Nov 6, 2025, 9:46:00 PM
    Author     : mohammed
--%>

<%@page import="auth.User.Role"%>
<%@page import="java.util.Set"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="election.Election" %>
<%@ page import="java.time.ZoneId" %>
<% String ctx = request.getContextPath();

    List<Election> elections = (List<Election>) request.getAttribute("elections");
    List<Election> activeElections = new ArrayList<>();

    if (elections != null) {
        for (Election e : elections) {
            if (!e.isClosed()) {
                activeElections.add(e);
            }
        }
    }
%>

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
            max-width: 650px;
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

        /* ===== Button ===== */

        .btn-enter {
            margin-top: 20px;
            padding: 14px 35px;
            font-size: 1.2rem;
            font-weight: bold;

            border: none;
            border-radius: 999px;

            background: var(--btn-bg);
            color: var(--btn-text);

            cursor: pointer;
            transition: opacity var(--transition-fast), transform var(--transition-fast);
        }

        .btn-enter:hover {
            opacity: 0.85;
            transform: translateY(-1px);
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

            <h1>Available Elections</h1>

            <%
                if (elections == null || elections.isEmpty()) {
            %>
            <div class="no-elections">There are no elections at the moment.</div>

            <%
            } else {
                for (Election e : elections) {

                    long preStart = e.getPreStartsAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    long start = e.getStartsAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    long end = e.getEndsAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    String electionId = e.getElectionId();
            %>

            <div class="election-box"
                 data-pre="<%=preStart%>"
                 data-start="<%=start%>"
                 data-end="<%=end%>"
                 data-election="<%=electionId%>">

                <div class="name"><%= e.getName()%></div>
                <div class="status">loading...</div>
                <div class="votes">loading...</div>
                <div class="timer">Calculating time…</div>

                <form class="vote-form" method="get" action="<%= ctx%>/app/vote">
                    <input type="hidden" name="electionId" value="<%= e.getElectionId()%>">
                    <button class="btn-enter">Vote now!</button>
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
                    let pre = parseInt(box.dataset.pre);
                    let start = parseInt(box.dataset.start);
                    let end = parseInt(box.dataset.end);
                    let election = box.dataset.election;
                    var electionVotes = 0;
                    let votes = box.querySelector(".votes");
                    let timer = box.querySelector(".timer");
                    let status = box.querySelector(".status");
                    const voteForm = box.querySelector(".vote-form");

                    fetch(`<%=ctx%>/app/voting?appMode=election-json-votes&electionId=` + election)
                            .then(res => res.json())
                            .then(data => {
                                electionVotes = data.votes;
                            });


                    if (voteForm) {
                        if (now > start && now < end) {
                            voteForm.style.display = "block";
                        } else {
                            voteForm.style.display = "none";
                        }
                    }


                    if (now < pre) {
                        timer.textContent = "Candidates Registration begins in: " + format(pre - now);
                        status.textContent = "";
                        votes.textContent = "";
                    } else if (now >= pre && now < start) {
                        status.textContent = "Candidates Registeration period has started.";
                        timer.textContent = "Voting starts in: " + format(start - now);
                        votes.textContent = "";
                    } else if (now >= start && now < end) {
                        timer.textContent = "Election ends in: " + format(end - now);
                        status.textContent = "";
                        votes.textContent = "Votes (LIVE): " + electionVotes;
                    } else {
                        timer.textContent = "Election finished.";
                        status.textContent = "";
                        votes.textContent = "Votes: " + electionVotes;
                    }
                });
            }

            updateTimers();
            setInterval(updateTimers, 1000);
        </script>


    </body>
</html>
