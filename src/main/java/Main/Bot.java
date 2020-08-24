package Main;

import Order.OrderProcess;
import commands.CommandsHandler;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


public class Bot extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived(Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            parseMessage(update);
        }
        if (update.hasCallbackQuery()) {
            parseCallBack(update);
        }
    }

    private void parseCallBack(Update update) {
        final String callback = update.getCallbackQuery().getData();
        long userId = update.getCallbackQuery().getMessage().getChatId();
        if (callback.contains("start_service")) {
            OrderProcess.parseCallbackAnswer(this, callback);
        }
        if (callback.equals("cancel_service")) {
            EditMessageText editMessageText = new EditMessageText()
                    .setChatId(userId)
                    .setMessageId(update.getCallbackQuery().getMessage().getMessageId())
                    .setText("Order canceled");
            try {
                execute(editMessageText);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void parseMessage(Update update) {
        String message = update.getMessage().getText();
        long userID = update.getMessage().getChatId();
        SendMessage sendMessage = null;
        try {
            if (message.equals("Home")) {
                sendMessage = new SendMessage()
                        .setChatId(userID)
                        .setText(Messages.main_menu_msg)
                        .setReplyMarkup(Interface.createMainMenu());
            }
            if (message.equals("Balance")) {
                sendMessage = new SendMessage()
                        .setChatId(userID)
                        .setText(CommandsHandler.getBalance(userID).getText())
                        .setReplyMarkup(Interface.createBalanceMenu());
            }
            if (message.equals("Order")) {
                sendMessage = new SendMessage()
                        .setChatId(userID)
                        .setText(Messages.order_menu_msg);
            }
            if (message.equals("/start")) {
                String login = update.getMessage().getFrom().getFirstName();
                CommandsHandler.handleUser(userID, login);
                sendMessage = new SendMessage()
                        .setChatId(userID)
                        .setText(Messages.main_menu_msg)
                        .setReplyMarkup(Interface.createMainMenu());
            }
            if (sendMessage != null) {
                execute(sendMessage);
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
