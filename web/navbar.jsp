
<style>
  .navbar {
    background: linear-gradient(135deg, #660631, #9a0f52);
    color: white;
    padding: 12px 24px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-family: Arial, sans-serif;
    border-radius: 40px; /* rounded navbar */
    width: 90%;
    margin: 15px auto; /* centered */
    box-shadow: 0 4px 12px rgba(0,0,0,0.2);
  }

  .navbar .welcome {
    font-size: 18px;
    font-weight: 500;
  }

  .dropdown {
    position: relative;
    display: inline-block;
  }

  .dropdown-btn {
    background: none;
    border: none;
    color: white;
    cursor: pointer;
    font-size: 22px;
    padding: 5px 10px;
  }

  .dropdown-content {
    display: none;
    position: absolute;
    right: 0;
    background-color: white;
    min-width: 150px;
    box-shadow: 0 4px 8px rgba(0,0,0,0.1);
    border-radius: 8px;
    z-index: 1;
    overflow: hidden;
  }

  .dropdown-content a,
  .dropdown-content button {
    color: #333;
    padding: 10px 15px;
    text-decoration: none;
    display: block;
    width: 100%;
    background: none;
    border: none;
    text-align: left;
    cursor: pointer;
    font-size: 15px;
  }

  .dropdown-content a:hover,
  .dropdown-content button:hover {
    background-color: #f1f1f1;
  }

  .dropdown:hover .dropdown-content {
    display: block;
  }
</style>

<div class="navbar">
  <div class="welcome">Welcome, Someone</div>

  <div class="dropdown">
    <button class="dropdown-btn">&#8942;</button> <!-- 3 dots -->
    <div class="dropdown-content">
      <a href="#">Profile</a>
      <a href="#">Settings</a>
      <form id="logoutForm" action="logout" method="post">
        <button type="submit">Logout</button>
      </form>
    </div>
  </div>
</div>