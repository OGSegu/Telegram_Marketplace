package Main;

import Order.OrderProcess;

public class Messages {
    public final static String welcome_msg = "Welcome to Segu SMM Bot";
    public final static String moreinfo_msg = "In progress";

    public final static String order_menu_msg = String.format("Twitch Follow Bot\nMin Amount - %d\nMax Amount - %d\nTo order use: /order channel amount", OrderProcess.minAmount, OrderProcess.maxAmount);
    public final static String main_menu_msg = "Segu SMM Bot";
}
