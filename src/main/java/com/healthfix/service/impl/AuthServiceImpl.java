package com.healthfix.service.impl;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.healthfix.dto.UserRegistrationRequest;
import com.healthfix.entity.User;
import com.healthfix.enums.UserStatus;
import com.healthfix.exception.CustomMessagePresentException;
import com.healthfix.exception.EmailNotVerifiedException;
import com.healthfix.repository.UserRepository;
import com.healthfix.response.LoginResponse;
import com.healthfix.response.UserRegistrationResponse;
import com.healthfix.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

/**
 * Service implementation for user registration.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public UserRegistrationResponse registerUser(UserRegistrationRequest request) {
        try {
            FirebaseToken decodedToken = verifyToken(request.getIdToken());
            User savedUser = createOrUpdateUser(decodedToken, request);

            logger.info("User successfully saved with ID: {}", savedUser.getId());

            return UserRegistrationResponse.builder()
                    .id(savedUser.getId())
                    .firstName(savedUser.getFirstName())
                    .lastName(savedUser.getLastName())
                    .email(savedUser.getEmail())
                    .isEmailVerified(savedUser.getIsEmailVerified())
                    .firebaseUserId(savedUser.getFirebaseUserId())
                    .build();
        } catch (Exception e) {
            logger.error("Error occurred during user registration: {}", e.getMessage(), e);
            throw new CustomMessagePresentException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String resendVerificationEmail(String email) {
        logger.info("Attempting to resend verification email for: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User with email {} not found", email);
                    return new CustomMessagePresentException("User with email not found");
                });

        try {
            String verificationLink = firebaseAuth.generateEmailVerificationLink(email);
            logger.info("Generated verification link for {}: {}", email, verificationLink);
            return verificationLink;
        } catch (Exception ex) {
            logger.error("Error sending verification email for {}: {}", email, ex.getMessage(), ex);
            throw new CustomMessagePresentException(ex.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LoginResponse login(String idToken) {
        try {
            logger.info("Starting social login process with ID token: {}", idToken);

            FirebaseToken decodedToken = verifyToken(idToken);
            boolean isEmailVerified = decodedToken.isEmailVerified();
            if (!isEmailVerified)
                throw new EmailNotVerifiedException("Please verify your email.");

            User user = createOrUpdateUser(decodedToken, null);
            logger.info("Social login process completed successfully.");

            return buildResponse(user);
        } catch (FirebaseAuthException e) {
            logger.error("Firebase authentication failed: {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private FirebaseToken verifyToken(String idToken) throws FirebaseAuthException {
        FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);
        logger.info("Successfully verified ID token: {}", idToken);
        return decodedToken;
    }

    private User createOrUpdateUser(FirebaseToken decodedToken, UserRegistrationRequest request) {
        String email = decodedToken.getEmail();
        boolean isEmailVerified = decodedToken.isEmailVerified();
        String firebaseUserId = decodedToken.getUid();

        if (email == null || email.isEmpty()) {
            logger.error("Email is missing in the ID token.");
            throw new IllegalArgumentException("Email is required but not provided by the provider.");
        }

        String normalizedEmail = email.toLowerCase(Locale.ROOT);

        Optional<User> existingUser = userRepository.findByFirebaseUserId(firebaseUserId);

        User user = existingUser.orElseGet(User::new);
        user.setFirebaseUserId(firebaseUserId);
        user.setEmail(email);
        user.setIsEmailVerified(isEmailVerified);
        user.setUserStatus(isEmailVerified ? UserStatus.Active : UserStatus.Inactive);

        // Split the name from decodedToken.getName()
        if (request == null && decodedToken.getName() != null && !decodedToken.getName().isEmpty()) {
            String fullName = decodedToken.getName().trim();
            String[] nameParts = fullName.split("\\s+"); // Split by whitespace
            if (nameParts.length > 1) {
                user.setFirstName(String.join(" ", Arrays.copyOf(nameParts, nameParts.length - 1))); // All except the last
                user.setLastName(nameParts[nameParts.length - 1]); // Last part as last name
            } else {
                user.setFirstName(fullName); // Only one part, set as first name
                user.setLastName(""); // Empty last name
            }
        }

        // Override with request values if available
        if (request != null) {
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
        }

        return userRepository.save(user);
    }

    private LoginResponse buildResponse(User user) {
        return LoginResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .isEmailVerified(user.getIsEmailVerified())
                .firebaseUserId(user.getFirebaseUserId())
                .build();
    }
}
