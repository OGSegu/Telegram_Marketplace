package database;

import Order.Status;

import java.sql.*;

public class SQL {
    private static final String user = "postgres";
    private static final String password = "7018022";
    private static final String url = "jdbc:postgresql://localhost:5432/postgres";

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
        try (Connection connection = connect(); PreparedStatement statement = connection
                .prepareStatement("INSERT INTO users (id, userid, login, balance, role) VALUES (DEFAULT, ?, ?, 0.00, ?);")) {
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

    public static String getLogin(long userId) throws SQLException {
        try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement("SELECT login FROM users WHERE userid = ?;")) {
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();
            String result = "";
            while (resultSet.next()) {
                result = resultSet.getString(1);
            }
            return result;
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
                System.out.println("Successfully set balance to " + userId);
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

    public static int addOrder(long userId, String channel, int amount, double price) throws SQLException {
        try (Connection connection = connect(); PreparedStatement statement = connection
                .prepareStatement("INSERT INTO orders (id, userid, login, channel, amount, price, status, createdat) VALUES (DEFAULT, ?, ?, ?, ?, ?, 'Queue', DEFAULT) RETURNING id")) {
            statement.setLong(1, userId);
            statement.setString(2, getLogin(userId));
            statement.setString(3, channel);
            statement.setInt(4, amount);
            statement.setDouble(5, price);
            ResultSet resultSet = statement.executeQuery();
            int id = -1;
            while (resultSet.next()) {
                id = resultSet.getInt(1);
            }
            if (id == -1) {
                System.out.println("Failed");
            }
            return id;
        }
    }

    public static boolean takeMoney(long userId, double price) throws SQLException {
        try (Connection connection = connect(); PreparedStatement statement = connection
                .prepareStatement("UPDATE users SET balance = ? WHERE userId = ?")) {
            double balance = getBalance(userId);
            if (balance < price) return false;
            statement.setDouble(1, balance - price);
            statement.setLong(2, userId);
            return statement.executeUpdate() > 0;
        }
    }

    public static boolean changeOrderStatus(int id, Status state) throws SQLException {
        try (Connection connection = connect(); PreparedStatement statement = connection
                .prepareStatement("UPDATE orders SET status = ? WHERE id = ?")) {
            String status = state.getDescription();
            statement.setString(1, status);
            statement.setInt(2, id);
            return statement.executeUpdate() > 0;
        }
    }

    public static String getOrders(long userID) throws SQLException {
        StringBuilder sb = new StringBuilder();
        try (Connection connection = connect(); PreparedStatement statement = connection
                .prepareStatement("SELECT channel, amount, status FROM orders WHERE userID = ? ORDER BY id DESC")) {
            statement.setLong(1, userID);
            ResultSet resultSet = statement.executeQuery();
            int counter = 0;
            sb.append("Channel | Amount | Status\n");
            while (resultSet.next()) {
                sb.append(resultSet.getString(1)).append(" | ");
                sb.append(resultSet.getInt(2)).append(" | ");
                sb.append(resultSet.getString(3)).append("\n");
                counter++;
                if (counter >= 3) break;
            }
        }
        return sb.toString();
    }

    public static boolean promoExists(String code) throws SQLException {
        try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM promos WHERE code = ?;")) {
            statement.setString(1, code);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        }
    }

    public static void addPromo(String code) throws SQLException {
        try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement("INSERT INTO promos (code) VALUES (?);")) {
            statement.setString(1, code);
            statement.executeUpdate();
        }
    }
}
