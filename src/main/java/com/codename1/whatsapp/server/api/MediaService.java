package com.codename1.whatsapp.server.api;

import com.codename1.whatsapp.server.entities.Media;
import com.codename1.whatsapp.server.entities.MediaRepository;
import com.codename1.whatsapp.server.entities.User;
import com.codename1.whatsapp.server.entities.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MediaService {

    @Autowired
    private MediaRepository media;

    @Autowired
    private UserRepository user;

    public String storeMedia(String authtoken, byte[] data, 
            String mimeType, String role, String visibility, 
            String filename) {
        User u = user.findByAuthtoken(authtoken).get(0);
        Media m = new Media();
        m.setData(data);
        m.setOwner(u);
        m.setDate(System.currentTimeMillis());
        m.setFilename(filename);
        m.setMimeType(mimeType);
        m.setRole(role);
        //m.setVisibility(visibility);
        media.save(m);
        return m.getId();
    }

   /* public MediaDAO getPublicMedia(String id) throws PermissionException {
        Media m = media.findById(id).get();
        if(VisibilityConstants.isPublic(m.getVisibility())) {
            return m.getDAO();
        }
        throw new PermissionException(
                "Media item belongs to a user that isn't a friend");
    }

    public MediaDAO getMedia(String authToken, String id) throws
            PermissionException {
        Media m = media.findById(id).get();
        if(VisibilityConstants.isPublic(m.getVisibility())) {
            return m.getDAO();
        }
        if(!m.getOwner().getAuthtoken().equals(authToken)) {
            if(!m.getOwner().isFriendByToken(authToken)) {
                throw new PermissionException(
                        "Media item belongs to a user that isn't a friend");
            }
        }
        return m.getDAO();
    }*/
}
