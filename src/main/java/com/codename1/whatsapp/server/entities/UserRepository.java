package com.codename1.whatsapp.server.entities;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository  extends CrudRepository<User, String> { 
    public List<User> findByPhone(String phone);
    public List<User> findByAuthtoken(String authtoken);
    public List<User> findByPushKey(String pushKey);
}
