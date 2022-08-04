package com.codename1.whatsapp.server.api;

import com.codename1.whatsapp.server.dao.MessageDAO;
import com.codename1.whatsapp.server.dao.UserDAO;
import com.codename1.whatsapp.server.entities.ChatMessage;
import com.codename1.whatsapp.server.entities.ChatMessageRepository;
import com.codename1.whatsapp.server.entities.ChatGroup;
import com.codename1.whatsapp.server.entities.Media;
import com.codename1.whatsapp.server.entities.MediaRepository;
import com.codename1.whatsapp.server.entities.User;
import com.codename1.whatsapp.server.entities.UserRepository;
import com.codename1.whatsapp.server.websocket.AppSocket;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.codename1.whatsapp.server.entities.ChatGroupRepository;


@Service
public class UserService {
    @Autowired
    private UserRepository users;

    @Autowired
    private ChatGroupRepository groups;

    @Autowired
    private ChatMessageRepository messages;

    @Autowired
    private APIKeys keys;
    
    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private MediaRepository medias;
    
    @Autowired
    private NotificationService notifications;

    private void sendActivationSMS(String number, String text) {
        Twilio.init(keys.get("twilio.sid"), keys.get("twilio.auth"));

        Message message = Message
                .creator(new PhoneNumber(number),
                        new PhoneNumber(keys.get("twilio.phone")),
                        text)
                .create();
        message.getSid();
    }

    public UserDAO login(String phone, String auth) 
            throws LoginException {
        List<User> userList;
        userList = users.findByPhone(phone);
        if (userList != null && userList.size() == 1) {
            User u = userList.get(0);
            if (encoder.matches(auth, u.getAuthtoken())) {
                return u.getLoginDAO();
            }
            throw new LoginException("Authentication error!");
        }
        throw new LoginException("User not found!");
    }

    private String createVerificationCode(int length) {
        StringBuilder k = new StringBuilder();
        Random r = new Random();
        for (int iter = 0; iter < length; iter++) {
            k.append(r.nextInt(10));
        }
        return k.toString();
    }


    public UserDAO signup(UserDAO user) throws SignupException {
        List<User> ul = users.findByPhone(user.getPhone());
        if (ul != null && ul.size() > 0) {
            throw new SignupException(
                    "The phone number is already registered!");
        }

        User u = new User();
        setProps(user, u);
        u.setAuthtoken(UUID.randomUUID().toString());
        u.setVerificationCode(createVerificationCode(4));

        users.save(u);

        sendActivationSMS(user.getPhone(), "Activation key: " + u.
                getVerificationCode());

        return u.getLoginDAO();
    }

    public boolean verifyPhone(String userId, String code) {
        User u = users.findById(userId).get();
        if (u.getVerificationCode().equals(code)) {
            u.setVerificationCode(null);
            u.setVerified(true);
            users.save(u);
            return true;
        }
        return false;
    }

    private void setProps(UserDAO user, User u) {
        u.setName(user.getName());
        u.setTagline(user.getTagline());
    }

    public void update(String auth, UserDAO user) {
        User u = users.findById(user.getId()).get();
        if (encoder.matches(auth, u.getAuthtoken())) {
            setProps(user, u);
            users.save(u);
        }
    }

    public byte[] getAvatar(String userId) {
        User u = users.findById(userId).get();
        if (u.getAvatar() != null) {
            return u.getAvatar().getData();
        }
        return null;
    }

    public void setAvatar(String auth, String userId, String mediaId) {
        Media m = medias.findById(mediaId).get();
        User u = users.findById(userId).get();
        if (encoder.matches(auth, u.getAuthtoken())) {
            u.setAvatar(m);
            users.save(u);
        }
    }
    
    public void userTyping(String userId, String toUser, boolean t) {
        Optional<User> to = users.findById(toUser);
        if(to.isPresent()) {
            AppSocket.sendUserTyping(to.get().getAuthtoken(), userId, t);
        } else {
            ChatGroup g = groups.findById(toUser).get();
            for(User u : g.getMembers()) {
                AppSocket.sendUserTyping(u.getAuthtoken(), userId, t);
            }
        }
    }
    
    
    public MessageDAO sendMessage(MessageDAO m) {
        ChatMessage cm = new ChatMessage();
        cm.setAuthor(users.findById(m.getAuthorId()).get());
        cm.setBody(m.getBody());
        cm.setMessageTime(new Date());
        Optional<User> to = users.findById(m.getSentTo());
        if(to.isPresent()) {
            cm.setSentTo(to.get());
            String json = createMessageImpl(m.getSentTo(), cm);
            User usr = to.get();
            sendMessageImpl(usr.getAuthtoken(), usr.getPushKey(), json, 
                    m.getBody());
        } else {
            ChatGroup g = groups.findById(m.getSentTo()).get();
            cm.setSentToGroup(g);
            String json = createMessageImpl(m.getSentTo(), cm);
            for(User u : g.getMembers()) {
                sendMessageImpl(u.getAuthtoken(), u.getPushKey(), json, 
                        m.getBody());
            }
        }
        return cm.getDAO();
    }    
    
    private void sendMessageImpl(String authToken, String pushKey, 
            String json, String message) {
        if(!AppSocket.sendMessage(authToken, json)) {
            notifications.sendPushNotification(pushKey, message, 1);
        }
    }
    
    public void sendMessage(String toUser, 
            Map<String, Object> parsedJSON) {
        ChatMessage cm = new ChatMessage();
        cm.setAuthor(users.findById((String)parsedJSON.get("authorId")).
                get());
        cm.setBody((String)parsedJSON.get("body"));
        cm.setMessageTime(new Date());
        Optional<User> to = users.findById(toUser);
        if(to.isPresent()) {
            cm.setSentTo(to.get());
            String json = createMessageImpl(toUser, cm);
            User usr = to.get();
            sendMessageImpl(usr.getAuthtoken(), usr.getPushKey(), 
                    json, cm.getBody());
        } else {
            ChatGroup g = groups.findById(toUser).get();
            cm.setSentToGroup(g);
            String json = createMessageImpl(toUser, cm);
            for(User u : g.getMembers()) {
                sendMessageImpl(u.getAuthtoken(), u.getPushKey(), 
                        json, cm.getBody());
            }
        }
    }    
    
    private String createMessageImpl(String toUser, ChatMessage cm) {
        cm = messages.save(cm);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String dao = objectMapper.writeValueAsString(cm.getDAO());
            return dao;
        } catch(JsonProcessingException err) {
            throw new RuntimeException(err);
        }
        
    }
    
    public void sendJSONTo(String toUser, String json) {
        Optional<User> to = users.findById(toUser);
        if(to.isPresent()) {
            AppSocket.sendMessage(to.get().getAuthtoken(), json);
        } else {
            ChatGroup g = groups.findById(toUser).get();
            for(User u : g.getMembers()) {
                AppSocket.sendMessage(u.getAuthtoken(), json);
            }
        }
    }
    
    public UserDAO findRegisteredUser(String phone) {
        return fromList(users.findByPhone(phone));
    }
    
    private UserDAO fromList(List<User> ul) {
        if(ul.isEmpty()) {
           return null; 
        }
        User u = ul.get(0);
        if(!u.isVerified()) {
           return null; 
        }
        return u.getDAO();
    }
    
    public UserDAO findRegisteredUserById(String id) {
        return users.findById(id).get().getDAO();
    }
    
    public void ackMessage(String id) {
        ChatMessage m = messages.findById(id).get();
        m.setAck(true);
        messages.save(m);
    }
    
    public void sendUnAckedMessages(String toUser) {
        List<ChatMessage> mess = messages.findByUnAcked(toUser);
        for(ChatMessage m : mess) {
            sendMessage(m.getDAO());
        }
    }

    public void updatePushKey(String auth, String id, String key) {
        User u = users.findById(auth).get();
        if (encoder.matches(auth, u.getAuthtoken())) {
            u.setPushKey(key);
            users.save(u);
        }        
    }
}
