package com.codename1.whatsapp.server.dao;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

public class MessageDAO {
    private String id;
    private String authorId;
    private String sentTo;
    
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS")
    private Date time;
    private String body;
    private String[] attachments;

    public MessageDAO() {
    }

    public MessageDAO(String id, String authorId, String sentTo,
            Date time, String body, String[] attachments) {
        this.id = id;
        this.authorId = authorId;
        this.sentTo = sentTo;
        this.time = time;
        this.body = body;
        this.attachments = attachments;
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
     * @return the authorId
     */
    public String getAuthorId() {
        return authorId;
    }

    /**
     * @param authorId the authorId to set
     */
    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    /**
     * @return the sentTo
     */
    public String getSentTo() {
        return sentTo;
    }

    /**
     * @param sentTo the sentTo to set
     */
    public void setSentTo(String sentTo) {
        this.sentTo = sentTo;
    }

    /**
     * @return the time
     */
    public Date getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(Date time) {
        this.time = time;
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
    public String[] getAttachments() {
        return attachments;
    }

    /**
     * @param attachments the attachments to set
     */
    public void setAttachments(String[] attachments) {
        this.attachments = attachments;
    }
    
}
