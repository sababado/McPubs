package com.sababado.mcpubs.backend.utils;

import com.google.appengine.api.utils.SystemProperty;
import com.google.appengine.repackaged.com.google.gson.Gson;
import com.sababado.mcpubs.backend.models.PubNotification;
import com.sababado.mcpubs.backend.models.SubscribeResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
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
        return subscriptionPost(SUBSCRIBE_URL, deviceToken, topic);
    }

    public static boolean unsubscribeFromTopic(String deviceToken, String topic) {
        return subscriptionPost(UNSUBSCRIBE_URL, deviceToken, topic);
    }

    private static boolean subscriptionPost(String url, String deviceToken, String topic) {
//        log.setLevel(Level.ALL);
        if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
            try {
                URL obj = new URL(url);
                HttpURLConnection con = prepareRequest(obj);

                String data = "{" +
                        "\"to\": \"/topics/" + topic + "\"," +
                        "\"registration_tokens\": [\"" + deviceToken + "\"]" +
                        "}";

                log.info("Sending 'POST' request to URL : " + url);
                log.info("Post parameters : " + data);
                writeBytes(data, con);

                // Send post request
                String responseString = getResponse(con);
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

    public static boolean sendMessage(PubNotification pubNotification) {
        log.setLevel(Level.ALL);
        if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
            String data = gson.toJson(pubNotification);
            try {
                URL obj = new URL("https://fcm.googleapis.com/fcm/send");
                HttpURLConnection con = prepareRequest(obj);

                log.info("Sending 'POST' request to send notification.");
                log.info("Post parameters : " + data);
                writeBytes(data, con);

                // Send post request
                String responseString = getResponse(con);
                log.info("Response: " + responseString);
            } catch (Exception e) {
                // TODO handle an exception un/subscribing to a topic.
                log.severe("Failed to send a message " + data + "\n" + e.getMessage());
                return false;
            }
        }
        return true;
    }

    private static HttpURLConnection prepareRequest(URL url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        //add request header
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", "key=" + SERVER_KEY);
        con.setDoOutput(true);
        return con;
    }

    private static void writeBytes(String data, HttpURLConnection con) throws IOException {
        byte[] out = data.getBytes(StandardCharsets.UTF_8);
        int length = out.length;

        con.setFixedLengthStreamingMode(length);
        con.connect();
        try (OutputStream os = con.getOutputStream()) {
            os.write(out);
        }
    }

    private static String getResponse(HttpURLConnection con) throws IOException {
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
        return response.toString().trim();
    }
}
