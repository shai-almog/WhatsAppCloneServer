package com.codename1.whatsapp.server.entities;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ChatMessageRepository extends 
        CrudRepository<ChatMessage, String> { 
    @Query("select n from ChatMessage n where n.ack = false and "
            + "n.sentTo.id = ?1 order by messageTime asc")    
    public List<ChatMessage> findByUnAcked(String sentTo);
}
