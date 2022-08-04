package com.codename1.whatsapp.server.api;

import com.codename1.whatsapp.server.entities.User;
import com.codename1.whatsapp.server.entities.UserRepository;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private static final String PUSHURL = 
            "https://push.codenameone.com/push/push";
    private final static Logger logger = LoggerFactory.
            getLogger(NotificationService.class);

    @Autowired
    private APIKeys keys;
    
    @Autowired
    private UserRepository users;

    private String sendPushImpl(String deviceId, 
            String messageBody, int type) throws IOException {
        HttpURLConnection connection = (HttpURLConnection)
                new URL(PUSHURL).openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", 
                "application/x-www-form-urlencoded;charset=UTF-8");
        String cert;
        String pass;
        boolean production = keys.get("push.itunes_production", 
                "false").equals("true");
        if(production) {
            cert = keys.get("push.itunes_prodcert");
            pass = keys.get("push.itunes_prodpass");
        } else {
            cert = keys.get("push.itunes_devcert");
            pass = keys.get("push.itunes_devpass");
        }
        String query = "token=" + keys.get("push.token") +
            "&device=" + URLEncoder.encode(deviceId, "UTF-8") +
            "&type=" + type + "&auth=" + 
             URLEncoder.encode(keys.get("push.gcm_key"), "UTF-8") +
            "&certPassword=" + URLEncoder.encode(pass, "UTF-8") +
            "&cert=" + URLEncoder.encode(cert, "UTF-8") +
            "&body=" + URLEncoder.encode(messageBody, "UTF-8") +
            "&production=" + production;
        try (OutputStream output = connection.getOutputStream()) {
            output.write(query.getBytes("UTF-8"));
        }
        int c = connection.getResponseCode();
        if(c == 200) {
            try (InputStream i = connection.getInputStream()) {
                return new String(readInputStream(i));
            }
        }
        logger.error("Error response code from push server: " + c);
        return null;
    }
    
    @Async
    public void sendPushNotification(String deviceId, 
            String messageBody, int type) {
        try {
            String json = sendPushImpl(deviceId, messageBody, type);
            if(json != null) {
                JsonParser parser = JsonParserFactory.getJsonParser();
                if(json.startsWith("[")) {                
                    List<Object> lst = parser.parseList(json);
                    for(Object o : lst) {
                        if(o instanceof Map) {
                            Map entry = (Map)o;
                            String status = (String)entry.get("status");
                            if(status != null) {
                                if(status.equals("error") || 
                                        status.equals("inactive")) {
                                    removePushKey((String)
                                            entry.get("id"));
                                }
                            }
                        }
                    }
                } else {
                    Map<String, Object> m = parser.parseMap(json);
                    if(m.containsKey("error")) {
                        logger.error("Error message from server: " + 
                                m.get("error"));
                    }
                }
            } 
        } catch(IOException err) {
            logger.error("Error during connection to push server", err);
        }
    }

    private  static byte[] readInputStream(InputStream i) 
            throws IOException {
        try(ByteArrayOutputStream b = new ByteArrayOutputStream()) {
            copy(i, b);
            return b.toByteArray();
        }
    }

    private static void copy(InputStream i, OutputStream o) 
            throws IOException {
        byte[] buffer = new byte[8192];
        int size = i.read(buffer);
        while(size > -1) {
            o.write(buffer, 0, size);
            size = i.read(buffer);
        }
     }

    public void removePushKey(String key) {
        List<User> userList = users.findByPushKey(key);
        if(!userList.isEmpty()) {
            User u = userList.get(0);
            u.setPushKey(null);
            users.save(u);
        }
    }
}
