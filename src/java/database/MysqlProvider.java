/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

import java.sql.*;

/**
 *
 * @author mohammed
 */
public class MysqlProvider {

    public static final String URL ="jdbc:mysql://192.168.0.8:3306/votingsystem?useSSL=false&requireSSL=false&verifyServerCertificate=false";
    public static final String USERNAME ="admin";
    public static final String PASSWORD ="admin";

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
