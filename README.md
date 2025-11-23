## Project login
When project is working with included database

there is users included inside database can be used

Email and password:
* "m.hammad@ajman.ac.ae", "1111"
* "202311566@ajmanuni.ac.ae", "1111"
* "202310776@ajmanuni.ac.ae", "1111"
* More can be found in AccountManagerServiceProvider.java


## Java 21 is required

## Database required: Mysql
* THE JDBC is already included in the project
  
* there is sql file that can be imported to for a demo on your local database.

* change sql url, username, password inside the class MysqlProvider.java for your needs
```java
public class MysqlProvider {

    public static final String URL =""; <----
    public static final String USERNAME =""; <----
    public static final String PASSWORD ="";  <----

    static {
    
    try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC driver not found", e);
        }
    
    }

    
     public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(MysqlProvider.URL, MysqlProvider.USERNAME, MysqlProvider.PASSWORD);
    }
    
}
```
