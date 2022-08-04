package com.codename1.whatsapp.server.entities;

import java.util.Date;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

@Entity
@Indexed
public class ChatGroup {

    @Id
    private String id;

    @Field
    private String name;
    
    @Field
    private String tagline;
    
    @Temporal(TemporalType.DATE)
    private Date creationDate;

    @ManyToOne
    private Media avatar;

    @ManyToOne
    private User createdBy;
    
    @OneToMany
    @OrderBy("name ASC")
    private Set<User> admins;
    
    @OneToMany
    @OrderBy("name ASC")
    private Set<User> members;
    

    public ChatGroup() {
        id = UUID.randomUUID().toString();
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
     * @return the createdBy
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(
            User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * @return the admins
     */
    public Set<User> getAdmins() {
        return admins;
    }

    /**
     * @param admins the admins to set
     */
    public void setAdmins(
            Set<User> admins) {
        this.admins = admins;
    }

    /**
     * @return the members
     */
    public Set<User> getMembers() {
        return members;
    }

    /**
     * @param members the members to set
     */
    public void setMembers(
            Set<User> members) {
        this.members = members;
    }
}
