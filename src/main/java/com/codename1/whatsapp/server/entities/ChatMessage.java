package com.codename1.whatsapp.server.entities;

import com.codename1.whatsapp.server.dao.MessageDAO;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.search.annotations.Indexed;

@Entity
@Indexed
public class ChatMessage {
    @Id
    private String id;

    @ManyToOne
    private User author;
    
    @ManyToOne
    private User sentTo;

    @ManyToOne
    private ChatGroup sentToGroup;

    @Temporal(TemporalType.TIMESTAMP)
    private Date messageTime;

    private String body;
    
    private boolean ack;
    
    @OneToMany
    @OrderBy("date ASC")
    private Set<Media> attachments;
    
    public ChatMessage() {
        id = UUID.randomUUID().toString();
    }

    public MessageDAO getDAO() {
        String[] a;
        if(attachments != null && !attachments.isEmpty()) {
            a = new String[attachments.size()];
            Iterator<Media> i = attachments.iterator();
            for(int iter = 0 ; iter < a.length ; iter++) {
                a[iter] = i.next().getId();
            }
        } else {
            a = new String[0];
        }
        return new MessageDAO(id, author.getId(), 
                sentTo != null ? sentTo.getId() : sentToGroup.getId(),
                messageTime, body, a);
    }
    
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the author
     */
    public User getAuthor() {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(
            User author) {
        this.author = author;
    }

    /**
     * @return the sentTo
     */
    public User getSentTo() {
        return sentTo;
    }

    /**
     * @param sentTo the sentTo to set
     */
    public void setSentTo(
            User sentTo) {
        this.sentTo = sentTo;
    }

    /**
     * @return the sentToGroup
     */
    public ChatGroup getSentToGroup() {
        return sentToGroup;
    }

    /**
     * @param sentToGroup the sentToGroup to set
     */
    public void setSentToGroup(
            ChatGroup sentToGroup) {
        this.sentToGroup = sentToGroup;
    }

    /**
     * @return the messageTime
     */
    public Date getMessageTime() {
        return messageTime;
    }

    /**
     * @param messageTime the messageTime to set
     */
    public void setMessageTime(Date messageTime) {
        this.messageTime = messageTime;
    }

    /**
     * @return the body
     */
    public String getBody() {
        return body;
    }

    /**
     * @param body the body to set
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * @return the attachments
     */
    public Set<Media> getAttachments() {
        return attachments;
    }

    /**
     * @param attachments the attachments to set
     */
    public void setAttachments(
            Set<Media> attachments) {
        this.attachments = attachments;
    }

    /**
     * @return the ack
     */
    public boolean isAck() {
        return ack;
    }

    /**
     * @param ack the ack to set
     */
    public void setAck(boolean ack) {
        this.ack = ack;
    }

}
