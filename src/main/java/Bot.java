import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;

public class Bot extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived(Update update) {
        // We check if the update has a message and the message has text
        System.out.println(update.getMessage().getChatId());
        if (update.hasMessage() && update.getMessage().hasText()) {
            if (update.getMessage().getText().equals("/balance")) {
                try {
                    double balance = SQL.getBalance(update.getMessage().getChatId());
                    SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                            .setChatId(update.getMessage().getChatId())
                            .setText(String.valueOf(balance));
                    try {
                        execute(message); // Call method to send the message
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public String getBotUsername() {
        return "realsegu_smm";
    }

    public String getBotToken() {
        return "1126523911:AAFmu8VBXm12g4NDd89REuU-CAeoEFmB7xo";
    }
}
