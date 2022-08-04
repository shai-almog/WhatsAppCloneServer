package com.codename1.whatsapp.server.entities;

import com.codename1.whatsapp.server.dao.UserDAO;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

@Entity
@Indexed
public class User {

    @Id
    private String id;

    @Field
    private String name;
    
    @Field
    private String tagline;
    
    @Column(unique=true)
    private String phone;

    private String verificationCode;

    @Temporal(TemporalType.DATE)
    private Date creationDate;

    @ManyToOne
    private Media avatar;

    @Column(unique=true)
    private String authtoken;
    
    private String pushKey;
    
    private boolean verified;
    

    public User() {
        id = UUID.randomUUID().toString();
        creationDate = new Date();
    }

    public UserDAO getDAO() {
        return new UserDAO(id, name, tagline, phone, creationDate, 
                avatar == null ? null : avatar.getId());
    }

    public UserDAO getLoginDAO() {
        UserDAO d = getDAO();
        d.setToken(authtoken);
        return d;
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
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone the phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }


    /**
     * @return the creationDate
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * @param creationDate the creationDate to set
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * @return the avatar
     */
    public Media getAvatar() {
        return avatar;
    }

    /**
     * @param avatar the avatar to set
     */
    public void setAvatar(Media avatar) {
        this.avatar = avatar;
    }

    /**
     * @return the authtoken
     */
    public String getAuthtoken() {
        return authtoken;
    }

    /**
     * @param authtoken the authtoken to set
     */
    public void setAuthtoken(String authtoken) {
        this.authtoken = authtoken;
    }

    /**
     * @return the verificationCode
     */
    public String getVerificationCode() {
        return verificationCode;
    }

    /**
     * @param verificationCode the verificationCode to set
     */
    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    /**
     * @return the pushKey
     */
    public String getPushKey() {
        return pushKey;
    }

    /**
     * @param pushKey the pushKey to set
     */
    public void setPushKey(String pushKey) {
        this.pushKey = pushKey;
    }

    /**
     * @return the tagline
     */
    public String getTagline() {
        return tagline;
    }

    /**
     * @param tagline the tagline to set
     */
    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    /**
     * @return the verified
     */
    public boolean isVerified() {
        return verified;
    }

    /**
     * @param verified the verified to set
     */
    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
