import java.sql.*;

public class DBConnector {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/footballapp";
    private static final String USER = "postgres";
    private static final String PASSWORD = "olehz17";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }
}

