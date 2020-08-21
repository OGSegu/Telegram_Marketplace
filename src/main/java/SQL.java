import java.sql.*;

public class SQL {
    private static final String user = "postgres";
    private static final String password = "7018022";
    private static final String url = "jdbc:postgresql://localhost:5432/telegram_market";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }


    public static boolean userExists(long chatID) throws SQLException {
        try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE chatid = ?;")) {
            statement.setLong(1, chatID);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        }
    }
    public static void registerUser(Long chatID, String login) throws SQLException {
        if (!userExists(chatID)) {
            try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement("INSERT INTO users (id, chatID, login, balance) VALUES (DEFAULT, ?, ?, 0);")) {
                statement.setLong(1, chatID);
                statement.setString(2, login);
                int result = statement.executeUpdate();
                if (result > 0) {
                    System.out.println("Success");
                } else {
                    System.out.println("Wrong");
                }
            }
        } else {
            try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement("UPDATE users SET login = ? WHERE chatid = ?;")) {
                statement.setString(1, login);
                statement.setLong(2, chatID);
                int result = statement.executeUpdate();
                if (result > 0) {
                    System.out.println("Success");
                } else {
                    System.out.println("Wrong");
                }
            }
        }
    }
    public static void setBalance(long chatID, double amount) throws SQLException {
        try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement("UPDATE users SET balance = ? WHERE chatid = ?;")) {
            statement.setDouble(1, amount);
            statement.setLong(2, chatID);
            if (statement.executeUpdate() > 0) {
                System.out.println("Successfully added balance");
            } else {
                System.out.println("Error");
            }
        }
    }

    public static double getBalance(long chatID) throws SQLException {
        try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement("SELECT balance FROM users WHERE chatid = ?;")) {
            statement.setLong(1, chatID);
            ResultSet resultSet = statement.executeQuery();
            double result = 0.00;
            while (resultSet.next()) {
                result = resultSet.getDouble(1);
            }
            return result;
        }
    }
}
