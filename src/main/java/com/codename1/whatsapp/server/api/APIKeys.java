package com.codename1.whatsapp.server.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.springframework.stereotype.Service;

@Service
public class APIKeys {
    private Properties apiKeys;

    public Properties getApiKeys() {
        if (apiKeys == null) {
            apiKeys = new Properties();
            try (FileInputStream fis = new FileInputStream(
                    System.getProperty("user.home") + File.separator
                    + "whatsapp_clone_keys.properties")) {
                apiKeys.load(fis);
            } catch (IOException err) {
                throw new RuntimeException(err);
            }
        }
        return apiKeys;
    }
    
    public String get(String k) {
        return getApiKeys().getProperty(k);
    }

    public String get(String k, String def) {
        return getApiKeys().getProperty(k, def);
    }
}
