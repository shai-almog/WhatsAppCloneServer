package com.codename1.whatsapp.server.websocket;

import com.codename1.whatsapp.server.api.NotificationService;
import com.codename1.whatsapp.server.api.UserService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class AppSocket extends TextWebSocketHandler {
    private static Logger log = 
            Logger.getLogger(AppSocket.class.getName());

    private static final Map<String, List<WebSocketSession>> clients = 
            new HashMap<>();
    
    @Autowired
    private UserService users;
    
    private static boolean sendToToken(String token, String json) {
        List<WebSocketSession> ws = clients.get(token);
        WebSocketSession currentSocket = null;
        try {
            if(ws != null) {
                TextMessage t = new TextMessage(json);
                List<WebSocketSession> removeQueue = null;
                for(WebSocketSession w : ws) {
                    currentSocket = w;
                    if(currentSocket.isOpen()) {
                        w.sendMessage(t);
                        return true;
                    } else {
                        if(removeQueue == null) {
                            removeQueue = new ArrayList<>();
                        }
                        removeQueue.add(w);
                    }
                }
                if(removeQueue != null) {
                    for(WebSocketSession w : removeQueue) {
                        ws.remove(w);
                    }
                }
            } else {
                log.warning("No WS connections for token: " + token);
            }
        } catch(IOException err) {
            log.log(Level.SEVERE, "Exception during sending message", err);
            if(currentSocket != null && ws != null) {
                ws.remove(currentSocket);
                if(ws.isEmpty()) {
                    clients.remove(token);
                }
            }
        }
        return false;
    }
    
    public static void sendUserTyping(String token, String id, 
            boolean b)  {
        sendToToken(token, "{\"typing\":\"" + b + 
                "\",\"authorId\":\"" + id + "\"}");
    }

    public static boolean sendMessage(String token, String json)  {
        return sendToToken(token, json);
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session,
            TextMessage message) throws Exception {
        JsonParser parser = JsonParserFactory.getJsonParser();
        Map<String, Object> m = parser.parseMap(message.getPayload());
        String type = (String)m.get("t");
        if(type != null) {
            if(type.equals("init")) {
                String token = (String)m.get("tok");
                Number time = (Number)m.get("time");
                List<WebSocketSession> l = clients.get(token);
                if(l == null) {
                    l = new ArrayList<>();
                    clients.put(token, l);
                }
                l.add(session);
                return;
            }
        } else {
            String typing = (String)m.get("typing");
            String sentTo = (String)m.get("sentTo");
            if(typing != null) {
                String authorId = (String)m.get("authorId");
                users.userTyping(authorId, sentTo, true);
            } else {
                String id = (String)m.get("id");
                if(id == null) {
                    users.sendMessage(sentTo, m);
                } else {
                    users.sendJSONTo(sentTo, message.getPayload());
                }
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession wss, 
            Throwable thrwbl) throws Exception {
        log.log(Level.SEVERE, "Error during transport", thrwbl);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession wss, CloseStatus cs)
            throws Exception {
        for(String s : clients.keySet()) {
            List<WebSocketSession> wl = clients.get(s);
            for(WebSocketSession w : wl) {
                if(w == wss) {
                    wl.remove(w);
                    if(wl.isEmpty()) {
                        clients.remove(s);
                    }
                    return;
                }
            }
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
