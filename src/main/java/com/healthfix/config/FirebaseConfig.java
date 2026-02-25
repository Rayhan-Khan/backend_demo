package com.healthfix.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Configuration class for initializing and setting up the Firebase Admin SDK in a Spring Boot application.
 * This class handles the initialization of Firebase credentials and provides beans for Google Cloud Storage
 * and credentials management.
 */
@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${firebase.config.path}")
    private String firebaseConfigPath;

    @Value("${BUCKET_NAME}")
    private String bucketName;

    private GoogleCredentials googleCredentials;

    /**
     * Initializes the Firebase Admin SDK after the bean is constructed.
     * This method loads the service account credentials from the specified file path,
     * configures Firebase options, and initializes the Firebase application if it hasn't been initialized yet.
     *
     * @throws IllegalStateException if there is an error reading the service account file or initializing Firebase
     */
    @PostConstruct
    public void initializeFirebase() {
        try (InputStream serviceAccount = new FileInputStream(firebaseConfigPath)) {
            this.googleCredentials = GoogleCredentials.fromStream(serviceAccount);

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(googleCredentials)
                        .setStorageBucket(bucketName)
                        .build();

                FirebaseApp.initializeApp(options);
                log.info("Firebase Initialized.");
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to initialize Firebase", e);
        }
    }

    /**
     * Provides a Spring-managed bean for GoogleCredentials.
     * This bean is available after the {@link #initializeFirebase()} method has executed.
     *
     * @return the initialized {@link GoogleCredentials} instance
     * @throws IllegalStateException if the credentials have not been initialized yet
     */
    @Bean
    public GoogleCredentials googleCredentials() {
        if (googleCredentials == null) {
            throw new IllegalStateException("GoogleCredentials has not been initialized yet.");
        }
        return googleCredentials;
    }

    /**
     * Provides a Spring-managed bean for Google Cloud Storage client.
     * This bean uses the initialized {@link GoogleCredentials} to configure and access the storage service.
     *
     * @return a configured {@link Storage} instance for interacting with Google Cloud Storage
     */
    @Bean
    public Storage firebaseStorage() {
        return StorageOptions.newBuilder()
                .setCredentials(googleCredentials())
                .build()
                .getService();
    }
}
