package com.itsuda.perfume.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
public class FirebaseConfig {

    private final String FCM_CONFIG_PATH = "/firebase/scently-db31e-firebase-adminsdk-fbsvc-045b7c468d.json";

    @PostConstruct
    public void firebaseApp() throws IOException {
        FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                .setCredentials(getGoogleCredentials()).build();
        FirebaseApp.initializeApp(firebaseOptions);
    }

    private GoogleCredentials getGoogleCredentials() throws IOException {
        return GoogleCredentials.fromStream(new ClassPathResource(FCM_CONFIG_PATH).getInputStream());
    }
}
