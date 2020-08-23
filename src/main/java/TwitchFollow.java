import twitch.FollowSender;
import twitch.TwitchUser;

import java.io.File;

public class TwitchFollow implements Runnable {

    private final String channel;
    private final int amount;


    public TwitchFollow(String channel, int amount) {
        this.channel = channel;
        this.amount = amount;
    }

    @Override
    public void run() {
        FollowSender followSender = new FollowSender(new File("10k_tokens.txt"), channel, amount);
        followSender.start();
    }
}
