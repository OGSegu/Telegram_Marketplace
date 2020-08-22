package database;

import java.sql.*;

public class SQL {
    private static final String user = "postgres";
    private static final String password = "7018022";
    private static final String url = "jdbc:postgresql://localhost:5432/telegram_market";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }


    public static boolean userExists(long userId) throws SQLException {
        try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE userid = ?;")) {
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        }
    }

    public static void registerUser(Long userId, String login) throws SQLException {
        try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement("INSERT INTO users (id, userid, login, balance, role) VALUES (DEFAULT, ?, ?, 0.00, ?);")) {
            statement.setLong(1, userId);
            statement.setString(2, login);
            statement.setString(3, "user");
            int result = statement.executeUpdate();
            if (result > 0) {
                System.out.println(login + " registered");
            } else {
                System.out.println("Wrong");
            }
        }
    }

    public static void loginUser(Long userId, String login) throws SQLException {
        try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement("UPDATE users SET login = ? WHERE userid = ?;")) {
            statement.setString(1, login);
            statement.setLong(2, userId);
            int result = statement.executeUpdate();
            if (result > 0) {
                System.out.println(login + " logged in");
            } else {
                System.out.println("ERROR");
            }
        }
    }

    public static double getBalance(long userId) throws SQLException {
        try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement("SELECT balance FROM users WHERE userid = ?;")) {
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();
            double result = 0.00;
            while (resultSet.next()) {
                result = resultSet.getDouble(1);
            }
            return result;
        }
    }

    public static void setBalance(long userId, double amount) throws SQLException {
        try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement("UPDATE users SET balance = ? WHERE userid = ?;")) {
            statement.setDouble(1, amount);
            statement.setLong(2, userId);
            if (statement.executeUpdate() > 0) {
                System.out.println("Successfully added balance to " + userId);
            } else {
                System.out.println("Error");
            }
        }
    }

    public static void addBalance(long userId, double amount) throws SQLException {
        double currentBalance = getBalance(userId);
        try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement("UPDATE users SET balance = ? WHERE userid = ?;")) {
            statement.setDouble(1, currentBalance + amount);
            statement.setLong(2, userId);
            if (statement.executeUpdate() > 0) {
                System.out.println("Successfully added balance to " + userId);
            } else {
                System.out.println("Error");
            }
        }
    }
}
