package Main;

import deposit.Deposit;
import order.OrderProcess;
import commands.CommandsHandler;
import database.SQL;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;

import static commands.CommandsHandler.genCode;


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

        try {
            if (message.equals(CommandsHandler.home_msg)) {
                execute(new SendMessage()
                        .setChatId(userID)
                        .setText(Messages.main_menu_msg)
                        .setReplyMarkup(Interface.createMainMenu()));
                return;
            }
            if (message.equals(CommandsHandler.balance_msg)) {
                execute(new SendMessage()
                        .setChatId(userID)
                        .setText(CommandsHandler.getBalance(userID).getText())
                        .setReplyMarkup(Interface.createBalanceMenu()));
                return;
            }
            if (message.equals(CommandsHandler.my_order_msg)) {
                try {
                    execute(new SendMessage()
                            .setChatId(userID)
                            .setText(SQL.getOrders(userID)));
                    return;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (message.equals(CommandsHandler.order_msg)) {
                execute(new SendMessage()
                        .setChatId(userID)
                        .setText(Messages.order_menu_msg));
                return;
            }
            if (message.equals(CommandsHandler.support_msg)) {
                execute(new SendMessage()
                        .setChatId(userID)
                        .setText(Messages.support_msg));
                return;
            }
            if (message.equals(CommandsHandler.deposit_msg)) {
                Deposit deposit = new Deposit(userID);
                execute(deposit.genDepositMsg());
            }
            if (message.equals("/start")) {
                String login = update.getMessage().getFrom().getFirstName();
                CommandsHandler.handleUser(userID, login);
                execute(new SendMessage()
                        .setChatId(userID)
                        .setText(Messages.main_menu_msg)
                        .setReplyMarkup(Interface.createMainMenu()));
                return;
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
            if (message.contains("/dig_promo")) {
                String[] args = message.split(" ");
                if (args.length != 2) {
                    execute(new SendMessage().setChatId(userID).setText("Error! Example: /dig_promo code"));
                    return;
                }
                if (args[1].length() != 16) {
                    execute(new SendMessage().setChatId(userID).setText("Error! Code is a 16-digit number"));
                    return;
                }
                String code = args[1];
                Object[] result = DigiParse.checkCode(code);
                if (result.length == 0) {
                    execute(new SendMessage().setChatId(userID).setText("Error! Wrong code"));
                    return;
                }
                if (SQL.digPromoExists(code)) {
                    execute(new SendMessage().setChatId(userID).setText("Error! Promo was already used"));
                    return;
                } else {
                    SQL.addDigPromo(code);
                }
                String channelName = (String) result[0];
                int amount = (int) result[1];
                NumberFormat nf = NumberFormat.getInstance();
                double price = nf.parse(result[2].toString()).doubleValue();
                SQL.addBalance(userID, price);
                OrderProcess orderProcess = new OrderProcess(this, userID, channelName, amount, price);
                orderProcess.createCustomOrder();
            }
            if (message.contains("/promo")) {
                String[] args = message.split(" ");
                if (args.length != 2) {
                    execute(new SendMessage().setChatId(userID).setText("Error! Example: /promo code"));
                    return;
                }
                if (args[1].length() != 16) {
                    execute(new SendMessage().setChatId(userID).setText("Error! Code is a 16-digit number"));
                    return;
                }
                String code = args[1];
                if (!SQL.promoExists(code)) {
                    execute(new SendMessage().setChatId(userID).setText("Error! Wrong code"));
                    return;
                }
                if (SQL.isPromoUsed(code, userID)) {
                    execute(new SendMessage().setChatId(userID).setText("Error! Promocode was already used"));
                    return;
                }
                double amount = SQL.usePromo(code);
                if (amount == -1) {
                    execute(new SendMessage().setChatId(userID).setText("Error! Promocode expired"));
                    return;
                }
                SQL.addUserUsedPromo(code, userID);
                SQL.addBalance(userID, amount);
                execute(new SendMessage().setChatId(userID).setText(amount + "$ was successfully added to your balance"));
                return;
            }

            if (message.contains("/addpromo")) {
                if (userID != 380962008) execute(new SendMessage().setChatId(userID).setText("You're not an admin"));
                String[] parsedCmd = message.split(" ");
                if (parsedCmd.length != 3) {
                    execute(new SendMessage().setChatId(userID).setText("Error! Example: /addpromo amount usage"));
                    return;
                }
                String code = genCode();
                double amount = -1;
                int usage = Integer.parseInt(parsedCmd[2]);
                try {
                    NumberFormat nf = NumberFormat.getInstance();
                    amount = nf.parse(parsedCmd[1]).doubleValue();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                execute(new SendMessage().setChatId(userID).setText(code + " | Amount: " + amount + "$"));
                SQL.addPromo(code, amount, usage);
                return;
            }

        } catch (TelegramApiException | SQLException | ParseException e) {
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
