package com.codename1.whatsapp.server.webservice;

import com.codename1.whatsapp.server.api.LoginException;
import com.codename1.whatsapp.server.api.SignupException;
import com.codename1.whatsapp.server.api.UserService;
import com.codename1.whatsapp.server.dao.ErrorDAO;
import com.codename1.whatsapp.server.dao.MessageDAO;
import com.codename1.whatsapp.server.dao.UserDAO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/user")
@RestController
public class UserWebService {
    @Autowired
    private UserService users;
    
    @ExceptionHandler(LoginException.class)
    @ResponseStatus(value=HttpStatus.FORBIDDEN)
    public @ResponseBody
    ErrorDAO handleLoginException(LoginException e) {
        return new ErrorDAO(e.getMessage(), 0);
    }
    
    @ExceptionHandler(SignupException.class)
    @ResponseStatus(value=HttpStatus.FORBIDDEN)
    public @ResponseBody
    ErrorDAO handleSignupException(SignupException e) {
        return new ErrorDAO(e.getMessage(), 0);
    }

    @RequestMapping(method=RequestMethod.POST, value="/login")
    public @ResponseBody
    UserDAO login(@RequestHeader String auth, @RequestBody UserDAO u) 
            throws LoginException {
        return users.login(u.getPhone(), auth);
    }

    @RequestMapping(method=RequestMethod.POST, value="/signup")
    public @ResponseBody
    UserDAO signup(@RequestBody UserDAO user)
            throws SignupException {
        return users.signup(user);
    }

    @RequestMapping(method=RequestMethod.GET, value="/verify")
    public @ResponseBody
    String verifyPhone(
            @RequestParam String userId,
            @RequestParam String code) {
        if(users.verifyPhone(userId, code)) {
            return "OK";
        }
        return "ERROR";
    }

    @RequestMapping(method=RequestMethod.POST, value="/update")
    public String update(@RequestHeader String auth, 
            @RequestBody UserDAO user) {
        users.update(auth, user);
        return "OK";
    }

    @RequestMapping(value="/avatar/{id:.+}", method=RequestMethod.GET)
    public ResponseEntity<byte[]> getAvatar(
            @PathVariable("id") String id) {
        byte[] av = users.getAvatar(id);
        if(av != null) {
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).
                    body(av);
        }
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(method=RequestMethod.GET, value="/set-avatar")
    public String setAvatar(
            @RequestHeader String auth,
            @RequestParam String userId,
            @RequestParam String mediaId) {
        users.setAvatar(auth, userId, mediaId);
        return "OK";
    }
    
    @RequestMapping(method=RequestMethod.GET, value="/findRegisteredUser")
    public List<UserDAO> findRegisteredUser(String phone) {
        UserDAO d = users.findRegisteredUser(phone);
        if(d == null) {
            return new ArrayList<>();
        }
        return Arrays.asList(d);
    }
    
    @RequestMapping(method=RequestMethod.GET, 
            value="/findRegisteredUserById")
    public List<UserDAO> findRegisteredUserById(String id) {
        UserDAO d = users.findRegisteredUserById(id);
        if(d == null) {
            return new ArrayList<>();
        }
        return Arrays.asList(d);
    }
    
    @RequestMapping(method=RequestMethod.POST, value="/sendMessage")
    public MessageDAO sendMessage(@RequestHeader String auth, 
            @RequestBody MessageDAO m) {
        return users.sendMessage(m);
    }
    
    @RequestMapping(method=RequestMethod.POST, value="/ackMessage")
    public void ackMessage(@RequestHeader String auth, 
            @RequestBody String id) {
        users.ackMessage(id);
    }    

    @RequestMapping(method=RequestMethod.GET, value="/updatePushKey")
    public void updatePushKey(@RequestHeader String auth, String id, 
            String key) {
        users.updatePushKey(auth, id, key);
    }
}
