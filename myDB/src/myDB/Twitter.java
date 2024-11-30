package myDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Twitter {
    private static final String URL = "jdbc:mysql://localhost:3306/dbtwitter";
    private static final String USER = "root";
    private static final String PASSWORD = "12345";

    private static Connection connection;
 // 데이터베이스 연결 반환
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL 드라이버 로드
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                System.err.println("Database driver not found.");
                e.printStackTrace();
            }
        }
        return connection;
    }
}
