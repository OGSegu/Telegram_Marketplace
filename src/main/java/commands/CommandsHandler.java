package commands;

import database.SQL;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.sql.SQLException;

public class CommandsHandler {

    public static SendMessage getBalance(Long userID) {
        double balance = 0;
        try {
            balance = SQL.getBalance(userID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new SendMessage()
                .setChatId(userID)
                .setText(String.format("Your balance: %.2f$", balance));
    }

    public static void handleUser(Long userId, String login) {
        try {
            if (SQL.userExists(userId)) {
                SQL.loginUser(userId, login);
            } else {
                SQL.registerUser(userId, login);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
