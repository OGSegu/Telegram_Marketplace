package Main;

import Order.Status;
import database.SQL;
import twitch.FollowSender;

import java.io.File;
import java.sql.SQLException;

public class TwitchFollow extends Thread {

    private final int id;
    private final String channel;
    private final int amount;


    public TwitchFollow(int id, String channel, int amount) {
        this.id = id;
        this.channel = channel;
        this.amount = amount;
    }

    @Override
    public void run() {
        try {
            SQL.changeOrderStatus(id, Status.IN_PROCESS);
            FollowSender followSender = new FollowSender(Config.token_file, channel, amount, Config.threads);
            followSender.start();
            SQL.changeOrderStatus(id, Status.DONE);
        } catch (SQLException e ) {
            e.printStackTrace();
        }
    }
}
