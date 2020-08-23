import database.SQL;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import twitch.TwitchUser;
import twitch.Utils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderProcess {

    private final Bot bot;
    private final String channel;
    private final long userId;
    private final int amount;
    private double balance = 0;

    public OrderProcess(Bot bot, long userId, String channel, int amount) {
        this.bot = bot;
        this.userId = userId;
        this.channel = channel;
        this.amount = amount;
    }

    public void createOrder() {
        if (checkEnoughBalance() && validChannel() && validAmount()) {
            try {
                bot.execute(new SendMessage()
                        .setChatId(userId)
                        .setText(String.format("Order\nFollow to: %s\nAmount: %d\nPrice: %.2f$", channel, amount, amount * 0.001))
                        .setReplyMarkup(createYesNoBtn())
                );
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean validChannel() {
        try {
            if (channel.isEmpty()) {
                bot.execute(new SendMessage().setChatId(userId).setText("Write channel"));
                return false;
            }
            if (!Utils.channelExists(channel)) {
                bot.execute(new SendMessage().setChatId(userId).setText("Wrong channel. Try again"));
                return false;
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean validAmount() {
        if (amount < 1000 || amount > 10000) {
            try {
                bot.execute(new SendMessage().setChatId(userId).setText("Wrong amount. 1000 < amount < 10000"));
                return false;
            } catch (TelegramApiException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private boolean checkEnoughBalance() {
        try {
            balance = SQL.getBalance(userId);
            if (balance < 1.00) {
                bot.execute(new SendMessage().setChatId(userId).setText("You need to have at least 1.00$ to start an order\nYour balance: " + balance + "$"));
                return false;
            }
        } catch (SQLException | TelegramApiException e) {
            System.out.println("SQL Error: " + e);
            return false;
        }
        return true;
    }


    private InlineKeyboardMarkup createYesNoBtn() {
        List<InlineKeyboardButton> row1 = new ArrayList<>(); // Первая строка
        row1.add(new InlineKeyboardButton().setText("START").setCallbackData("start_service"));
        row1.add(new InlineKeyboardButton().setText("CANCEL").setCallbackData("cancel_service"));
        List<List<InlineKeyboardButton>> finalRow = new ArrayList<>(); // Финальный двумерный лист
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(); // Markup, который будет добавляться к сообщению
        finalRow.add(row1);
        markup.setKeyboard(finalRow);
        return markup;
    }

}
