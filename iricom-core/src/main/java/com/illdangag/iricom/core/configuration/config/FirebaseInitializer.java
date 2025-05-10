package com.illdangag.iricom.core.configuration.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Slf4j
@Configuration
public class FirebaseInitializer {
    private final ApplicationContext applicationContext;

    @Autowired
    public FirebaseInitializer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public FirebaseApp getFirebaseApp() throws IOException {
        try {
            FirebaseApp firebaseApp = FirebaseApp.getInstance(FirebaseApp.DEFAULT_APP_NAME);
            if (firebaseApp != null) {
                log.debug("already create firebase app");
                return firebaseApp;
            }
        } catch (Exception exception) {
            log.debug("create firebase app");
        }

        ClassPathResource resource = new ClassPathResource("firebase-adminsdk.json");
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
                .build();
        return FirebaseApp.initializeApp(options);
    }

    @Bean
    public FirebaseAuth getFirebaseAuth() throws IOException {
        FirebaseInitializer firebaseInitializer = applicationContext.getBean(FirebaseInitializer.class);
        FirebaseApp firebaseApp = firebaseInitializer.getFirebaseApp();
        return FirebaseAuth.getInstance(firebaseApp);
    }
}
