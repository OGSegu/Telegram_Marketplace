package deposit;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class Deposit {
    private final String paymentLink;
    private final long userId;

    public Deposit(long userId) {
        this.paymentLink = "https://www.digiseller.market/asp2/pay_wm.asp?id_d=2961013&userid=" + userId;
        this.userId = userId;
    }

    public SendMessage genDepositMsg() {
        return new SendMessage().setChatId(userId).setText("Payment link:\n" + paymentLink).setReplyMarkup(createCheckBtn());
    }


    public long getUserId() {
        return userId;
    }

    private InlineKeyboardMarkup createCheckBtn() {
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(new InlineKeyboardButton().setText("Check payment"));
        List<List<InlineKeyboardButton>> finalRow = new ArrayList<>();
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        finalRow.add(row1);
        markup.setKeyboard(finalRow);
        return markup;
    }
}
