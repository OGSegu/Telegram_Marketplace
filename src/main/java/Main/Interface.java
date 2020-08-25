package Main;

import com.vdurmont.emoji.EmojiParser;
import commands.CommandsHandler;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class Interface {

    public static ReplyKeyboardMarkup createMainMenu() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        List<KeyboardRow> keyboard = new ArrayList<>(); // Первая строка
        keyboardFirstRow.add(CommandsHandler.balance_msg);
        keyboardFirstRow.add(CommandsHandler.my_order_msg);
        keyboardSecondRow.add(CommandsHandler.order_msg);
        keyboardSecondRow.add(CommandsHandler.support_msg);
        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    public static ReplyKeyboardMarkup createBalanceMenu() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        List<KeyboardRow> keyboard = new ArrayList<>(); // Первая строка
        keyboardFirstRow.add(CommandsHandler.deposit_msg);
        keyboardFirstRow.add(CommandsHandler.home_msg);
        keyboard.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }
}
