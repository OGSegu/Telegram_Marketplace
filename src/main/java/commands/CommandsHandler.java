package commands;

import com.vdurmont.emoji.EmojiParser;
import database.SQL;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.sql.SQLException;
import java.util.Random;

public class CommandsHandler {

    public static final String balance_msg = EmojiParser.parseToUnicode(":dollar: Balance");
    public static final String my_order_msg = EmojiParser.parseToUnicode(":clipboard: My orders");
    public static final String order_msg = EmojiParser.parseToUnicode(":package: Order");
    public static final String support_msg = EmojiParser.parseToUnicode(":telephone_receiver: Support");
    public static final String deposit_msg = EmojiParser.parseToUnicode(":credit_card: Deposit");
    public static final String home_msg = EmojiParser.parseToUnicode(":back: Home");


    public static String genCode() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 16; i++) {
            sb.append((char) (random.nextInt(25) + 65));
        }
        return sb.toString();
    }

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

    public static void generatePayment(Long userId) {

    }
}
