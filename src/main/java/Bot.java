import commands.CommandsHandler;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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
            if (message.contains("/order")) {
                String[] args = message.split(" ");
                if (args.length != 3) {
                    execute(new SendMessage().setChatId(userID).setText("Error! Example: /order channel amount"));
                    return;
                }
                String channel = args[1];
                int amount = Integer.parseInt(args[2]);
                OrderProcess orderProcess = new OrderProcess(this, userID, channel, amount);
                orderProcess.createOrder();
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
