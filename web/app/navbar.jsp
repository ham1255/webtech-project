<%@ page contentType="text/html; charset=UTF-8" %> 
<% String ctx = request.getContextPath();%>
<style> :root {
    --c1:#5C62D6; /* main blue-violet */
    --c2:#43A5BE; /* teal accent */
}
body {
    margin:0;
    font-family: "Segoe UI", Arial, sans-serif;
    height: 100vh;
    background: linear-gradient(135deg, #000000, #43A5BE);
    color: white; /* optional for contrast */
}
.nav {
    
    position: sticky;
    top: 10px;             
    left: 10px;          
    right: 10px;           
    z-index: 999;          
    
    background: linear-gradient(135deg, var(--c1), var(--c2));
    border-radius:50px;
    margin:10px;
    padding:10px 20px;
    display:flex;
    align-items:center;
    justify-content:space-between;
    color:white;
    box-shadow:0 4px 15px rgba(0,0,0,.15);
}
.nav-left, .nav-right {
    display:flex;
    align-items:center;
    gap:10px;
}
.nav a, .nav button {
    background:rgba(255,255,255,.15);
    border:none;
    color:white;
    text-decoration:none;
    padding:10px 18px;
    border-radius:999px;
    cursor:pointer;
    font-weight:600;
    transition:all .2s ease;
}
.nav a:hover, .nav button:hover {
    background:rgba(255,255,255,.25);
    transform:scale(1.05);
}
.brand {
    font-size:18px;
    font-weight:700;
    letter-spacing:0.3px;
}
.kebab {
    width:42px;
    height:42px;
    background:rgba(255,255,255,.15);
    border:none;
    border-radius:50%;
    display:flex;
    align-items:center;
    justify-content:center;
    font-size:22px;
    cursor:pointer;
    transition:.2s ease;
}
.kebab:hover {
    background:rgba(255,255,255,.25);
    transform:rotate(90deg);
}
.menu {
    position:absolute;
    right:20px;
    top:70px;
    background:#fff;
    color:#111;
    border-radius:20px;
    box-shadow:0 6px 20px rgba(0,0,0,.2);
    display:none;
    overflow:hidden;
    min-width:180px;
    z-index:10;
}
.menu.open {
    display:block;
}
.menu a {
    display:block;
    padding:12px 16px;
    color:#111;
    text-decoration:none;
    font-weight:600;
    transition:.15s;
}
.menu a:hover {
    background:rgba(92,98,214,.1);
} </style>

<nav class="nav">
    <div class="nav-left">
        <span class="brand">Student E-Voting portal</span>
        <a href="<%= ctx%>/app">Home</a> </div> <div class="nav-right">
        <form action="<%= ctx%>/auth/logout" method="post" style="margin:0;">
            <button type="submit">Logout</button> </form> 
        <button class="kebab" id="menuToggle" title="More"> ⋮ 
        </button> </div> <div class="menu" id="moreMenu"> 
            <a href="<%= ctx%>/profile">Profile</a>
            <a href="<%= ctx%>/help">help & Support</a> 
        </div> 
</nav> 

<script> const toggle = document.getElementById("menuToggle");
    const menu = document.getElementById("moreMenu");
    document.addEventListener("click", (e) => { 
        if (toggle.contains(e.target)){ 
            menu.classList.toggle("open"); 
        } else if (!menu.contains(e.target)){ 
            menu.classList.remove("open"); 
        } 
    });
</script>