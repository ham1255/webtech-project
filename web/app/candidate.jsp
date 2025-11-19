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

        .results-container {
            margin-top: 12px;
            display: flex;
            flex-wrap: wrap;
            gap: 12px;
            align-items: stretch;          /* make all blocks in a row same height */
        }

        .chair-block {
            flex: 1 1 260px;               /* ⬅️ key: same base width for all boxes */
            box-sizing: border-box;

            background: #5C62D6;
            border: 1px solid rgba(255,255,255,0.25);  /* softer border, not dark */
            border-radius: 12px;
            padding: 12px;
            color: #e5e7eb;

            display: flex;
            flex-direction: column;        /* lets content stack nicely */
            justify-content: flex-start;
        }
        .chair-title {
            font-weight: 600;
            margin-bottom: 6px;
            font-size: 15px;
        }
        .chair-list {
            margin: 0;
            padding-left: 18px;
        }
        .chair-list li {
            margin-bottom: 3px;
        }

        .vote-item {
            display: flex;
            justify-content: space-between;   /* pushes name left and votes right */
            align-items: center;
            padding: 4px 0;
        }

        .vote-name {
            flex: 1;                           /* long names won't push votes */
            max-width: 70%;                    /* prevent too long names from eating space */
            overflow: hidden;
            text-overflow: ellipsis;           /* (…) if too long */
            white-space: nowrap;
        }

        .vote-count {
            width: 90px;                       /* fixed width keeps all aligned */
            text-align: right;
            flex-shrink: 0;
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
            <div class="no-elections">There are no elections & polls at the moment.</div>

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
                <div class="votes">loading...</div>
                <div class="timer">Calculating time…</div>
                <div>

                </div>
                <form class="vote-form" method="get" action="<%= ctx%>/app/voting">
                    <input type="hidden" name="appMode" value="election-vote">
                    <input type="hidden" name="electionId" value="<%= e.getElectionId()%>"><br>
                    <button class="page-btn">Vote now!</button>
                </form>
                 <br>
                 <div id="showLive" class="name"></div>   
                 <div id="results-container" class="results-container"></div>


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
                    var electionVotes = 0;
                    let votes = box.querySelector(".votes");
                    let timer = box.querySelector(".timer");
                    let status = box.querySelector(".status");
                    const voteForm = box.querySelector(".vote-form");
                    let showLive = document.getElementById("showLive");

                    fetch(`<%=ctx%>/app/voting?appMode=election-json-votes&electionId=` + election)
                            .then(res => res.json())
                            .then(data => {
                                electionVotes = data.total_votes;
                                if (now >= start && now < end) {
                                    votes.textContent = "Total Votes (LIVE): " + electionVotes;
                                }
                                var names = data.names; // your names object from the response

                                const resultsContainer = box.querySelector("#results-container");
                                resultsContainer.innerHTML = ""; // clear old results
                                const votesPerChair = data.votes || {};
                                for (const chairName in votesPerChair) {

                                    const chairBlock = document.createElement("div");
                                    chairBlock.className = "chair-block";

                                    const title = document.createElement("div");
                                    title.className = "chair-title";
                                    title.textContent = chairName + " Results";
                                    chairBlock.appendChild(title);

                                    const list = document.createElement("ol");
                                    list.className = "chair-list";
                                    const entries = Object.entries(votesPerChair[chairName])
                                            .sort((a, b) => b[1] - a[1])
                                            .slice(0, 10);



                                    entries.forEach(function (entry) {
                                        var candidateId = entry[0];
                                        var count = entry[1];

                                        // Read names from your response
                                        var name = names[candidateId];

                                        // If name exists → "Name (ID)"
                                        // If not → just ID
                                        var display = name ? (name + " (" + candidateId + ")") : candidateId;

                                        // Create <li class="vote-item">
                                        var li = document.createElement("li");
                                        li.className = "vote-item";

                                        // Create <span class="vote-name">
                                        var nameSpan = document.createElement("span");
                                        nameSpan.className = "vote-name";
                                        nameSpan.textContent = display;

                                        // Create <span class="vote-count">
                                        var countSpan = document.createElement("span");
                                        countSpan.className = "vote-count";
                                        countSpan.textContent = count + " vote" + (count !== 1 ? "s" : "");

                                        // Add children
                                        li.appendChild(nameSpan);
                                        li.appendChild(countSpan);

                                        // Add to list
                                        list.appendChild(li);
                                    });

                                    chairBlock.appendChild(list);
                                    resultsContainer.appendChild(chairBlock);


                                }



                            });

                    if (voteForm) {
                        if (now > start && now < end) {
                            voteForm.style.display = "block";
                        } else {
                            voteForm.style.display = "none";
                        }
                    }


                    if (now < registerationStart) {
                        timer.textContent = "Candidates Registration begins in: " + format(registerationStart - now);
                        status.textContent = "";
                        votes.textContent = "";
                        showLive.textContent = "";
                    } else if (now >= registerationStart && now < start) {
                        status.textContent = "Candidates Registeration period has started.";
                        timer.textContent = "Voting starts in: " + format(start - now);
                        votes.textContent = "";
                        showLive.textContent = "";
                    } else if (now >= start && now < end) {
                        timer.textContent = "Election ends in: " + format(end - now);
                        status.textContent = "";
                        showLive.textContent = "Results (LIVE)";

                    } else {
                        timer.textContent = "Election finished.";
                        status.textContent = now + "<" + registerationStart;
                        votes.textContent = "";
                        showLive.textContent = "Results (LIVE)";
                    }
                });
            }

            updateTimers();
            setInterval(updateTimers, 1000);
        </script>

        <% } else {%>
        <a style= "margin-top:10px; display:inline-block;" href="<%= ctx%>/app/voting" class="page-btn">Go back</a>


        <%

            Election election = (Election) request.getAttribute("election");
            Map<StudentCouncilElectionChair, List<Candidate>> candidatesByChair
                    = (Map<StudentCouncilElectionChair, List<Candidate>>) request.getAttribute("candidatesByChair");


        %>
        <style>
            .select-box {
                width: 260px;
                padding: 12px;
                font-size: 16px;
                border-radius: 10px;
                border: 1px solid #cfd3d4;
                background: #fff;
                color: #000;
                outline: none;
                transition: 0.2s;
            }

            .select-box:focus {
                border-color: #43A5BE;
                box-shadow: 0 0 5px rgba(67,165,190,0.8);
            }
        </style>
        <div class="container">
            <div class="election-box"> 

                <form method="post" action="<%=ctx%>/app/voting" class="vote-form">
                    <input type="hidden" name="electionId" value="<%= election.getElectionId()%>">
                    <input type="hidden" name="operation" value="vote">
                    <% for (Map.Entry<StudentCouncilElectionChair, List<Candidate>> entry : candidatesByChair.entrySet()) {
                            StudentCouncilElectionChair chair = entry.getKey();
                            List<Candidate> candidates = entry.getValue();
                    %>

                    <label><strong><%= chair.getDisplayName()%> Candidates:</strong></label><br><br>

                    <select name="<%= chair%>" class="select-box">
                        <% for (Candidate c : candidates) {%>
                        <option value="<%= c.getUserID()%>"> <%= c.getUserID()%>: <%= c.getName()%></option>
                        <% } %>
                    </select><br><br>
                    <% } %>

                    <input class="page-btn" type="submit" value="Vote now!">
                </form>
            </div>
        </div>
        <%}%>


    </body>
</html>
