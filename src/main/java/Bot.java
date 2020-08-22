import commands.CommandsHandler;
import database.SQL;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;

public class Bot extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived(Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            parseMessage(update);
        }
    }

    private void parseMessage(Update update) {
        String message = update.getMessage().getText();
        Long userID = update.getMessage().getChatId();
        SendMessage sendMessage = null;
        try {
            if (message.equals("/start")) {
                String login = update.getMessage().getFrom().getFirstName();
                CommandsHandler.handleUser(userID, login);
                sendMessage = new SendMessage().setChatId(userID).setText(Messages.welcome_msg);
            }
            if (message.equals("/balance")) {
                execute(CommandsHandler.getBalance(userID));
            }


            if (sendMessage != null) {
                execute(sendMessage);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public String getBotUsername() {
        return "realsegu_smm";
    }

    public String getBotToken() {
        return "1126523911:AAFmu8VBXm12g4NDd89REuU-CAeoEFmB7xo";
    }
}
