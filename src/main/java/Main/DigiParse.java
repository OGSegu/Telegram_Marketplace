package Main;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DigiParse {
    private static final String ID_SELLER = "409342";
    private static final String PASSWORD = "7018022";

    public static Object[] checkCode(String code) {
        Object[] result = new Object[3];
        String md5 = DigestUtils.md5Hex(String.format("%s:%s:%s", ID_SELLER, code, PASSWORD));
        String requestString = String.format("{" +
                "\"id_seller\":\"%s\"," +
                "\"unique_code\":\"%s\"," +
                "\"sign\":\"%s\"" +
                "}", ID_SELLER, code, md5);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestString))
                .uri(URI.create("https://www.oplata.info/xml/check_unique_code.asp?"))
                .setHeader("Content-Type", "\"text/json\" | \"text/xml\" ")
                .build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        if (response == null) return result;
        JSONObject jsonObject = new JSONObject(response.body());
        try {
            result[2] = jsonObject.getString("amount");
            result[1] = jsonObject.getInt("cnt_goods");
            JSONArray array = jsonObject.getJSONArray("options");
            result[0] = parseChannel(array.getJSONObject(0).getString("user_data"));
        } catch (JSONException e) {
            return result;
        }
        return result;
    }

    public static String parseChannel(String channel) {
        String result = channel;
        if (channel.contains("twitch.tv")) {
            Pattern pattern = Pattern.compile("(?<=twitch.tv/).+");
            Matcher matcher = pattern.matcher(channel);
            while (matcher.find()) {
                result = matcher.group();
            }
        }
        return result;
    }
}
