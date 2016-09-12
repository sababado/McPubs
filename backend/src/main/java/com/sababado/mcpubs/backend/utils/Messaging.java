package com.sababado.mcpubs.backend.utils;

import com.google.appengine.api.utils.SystemProperty;
import com.google.appengine.repackaged.com.google.gson.Gson;
import com.sababado.mcpubs.backend.models.SubscribeResponse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

/**
 * Created by robert on 9/10/16.
 */
public class Messaging {
    private static final Logger log = Logger.getLogger(Messaging.class.getName());
    private static final String SUBSCRIBE_URL = "https://iid.googleapis.com/iid/v1:batchAdd";
    private static final String UNSUBSCRIBE_URL = "https://iid.googleapis.com/iid/v1:batchRemove";
    private static final String SERVER_KEY = "AIzaSyAywwSJGV07RpNoS6tzHt08pB7cbdrJP_o";
    private static final Gson gson = new Gson();

    public static boolean subscribeToTopic(String deviceToken, String topic) {
        return sendPost(SUBSCRIBE_URL, deviceToken, topic);
    }

    public static boolean unsubscribeFromTopic(String deviceToken, String topic) {
        return sendPost(UNSUBSCRIBE_URL, deviceToken, topic);
    }

    // HTTP POST request
    private static boolean sendPost(String url, String deviceToken, String topic) {
//        log.setLevel(Level.ALL);
        if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
            try {
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                //add request header
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Authorization", "key=" + SERVER_KEY);
                con.setDoOutput(true);

                String data = "{" +
                        "\"to\": \"/topics/" + topic + "\"," +
                        "\"registration_tokens\": [\"" + deviceToken + "\"]" +
                        "}";

                log.info("Sending 'POST' request to URL : " + url);
                log.info("Post parameters : " + data);
                byte[] out = data.getBytes(StandardCharsets.UTF_8);
                int length = out.length;

                con.setFixedLengthStreamingMode(length);
                con.connect();
                try (OutputStream os = con.getOutputStream()) {
                    os.write(out);
                }

                // Send post request

                int responseCode = con.getResponseCode();
                log.info("Response Code : " + responseCode);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                //print result
                String responseString = response.toString().trim();
                log.info("Response: " + responseString);

                SubscribeResponse subscribeResponse = gson.fromJson(responseString, SubscribeResponse.class);
                for (SubscribeResponse.ErrorResponse resp : subscribeResponse.results) {
                    if (!StringUtils.isEmptyOrWhitespace(resp.error)) {
                        // TODO handle an error
                        log.warning("Failed to un/subscribe to " + topic);
                    }
                }
            } catch (Exception e) {
                // TODO handle an exception un/subscribing to a topic.
                log.severe("Failed to un/subscribe to " + topic + "\n" + e.getMessage());
                return false;
            }
        }
        return true;
    }
}
