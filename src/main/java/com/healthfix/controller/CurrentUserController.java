package com.healthfix.controller;

import com.healthfix.annotation.ValidAvatar;
import com.healthfix.dto.UpdateUserRequest;
import com.healthfix.exception.CustomMessagePresentException;
import com.healthfix.response.CurrentUserResponse;
import com.healthfix.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.healthfix.utils.ResponseBuilder.error;
import static com.healthfix.utils.ResponseBuilder.success;
import static org.springframework.http.ResponseEntity.ok;

/**
 * Controller for managing user interests.
 */
@RestController
@RequestMapping("/api/v1/current-user")
@RequiredArgsConstructor
@Tag(name = "Current user", description = "Current user info")
public class CurrentUserController {

    private static final Logger logger = LoggerFactory.getLogger(CurrentUserController.class);
    private final CurrentUserService currentUserService;

    /**
     * Endpoint to upload and set the avatar for the authenticated user.
     *
     * @param file           The avatar file to upload. Must be a JPEG/PNG file and not exceed 5MB.
     * @param firebaseUserId The Firebase User ID of the authenticated user.
     * @return ResponseEntity  A response indicating the result of the operation.
     * @throws IllegalArgumentException If validation errors occur during the request.
     */
    @PutMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Set user avatar",
            description = "Allows authenticated users to set their avatar. Supported avatar types: " +
                    "0: Standard, 1: Red Eyes, 2: Custom. File must be JPEG/PNG and not exceed 5MB.")
    public ResponseEntity<JSONObject> setAvatar(
            @ValidAvatar
            @RequestParam("file") MultipartFile file,

            @AuthenticationPrincipal String firebaseUserId) {
        logger.info("Processing avatar upload request for user with Firebase ID: {}", firebaseUserId);
        try {
            currentUserService.setAvatar(file, firebaseUserId);
            logger.info("Successfully set avatar for user with Firebase ID: {}", firebaseUserId);
            return ok(success(null, "Avatar uploaded successfully.").getJson());
        } catch (IllegalArgumentException e) {
            logger.warn("Validation error while setting avatar for user with Firebase ID {}: {}", firebaseUserId, e.getMessage());
            return ResponseEntity.badRequest().body(error(null, e.getMessage()).getJson());
        } catch (IOException e) {
            logger.error("IO error while processing avatar for user with Firebase ID {}: {}", firebaseUserId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(error(null, e.getMessage()).getJson());
        }
    }

    /**
     * Fetch details of the currently authenticated user.
     *
     * @param firebaseUserId the identifier of the current user derived from the authorization token.
     * @return ResponseEntity containing the current user details in a structured JSON format.
     */
    @GetMapping
    @Operation(summary = "Get Current User Data", description = "Retrieve details of the currently authenticated user.")
    public ResponseEntity<JSONObject> getCurrentUser(@AuthenticationPrincipal @NotNull String firebaseUserId) {
        try {
            logger.info("Fetching current user data for Firebase User ID: {}", firebaseUserId);
            CurrentUserResponse userResponse = currentUserService.getCurrentUser(firebaseUserId);
            return ok(success(userResponse, null).getJson());
        } catch (CustomMessagePresentException e) {
            logger.error("Error fetching current user data for Firebase User ID: {}: {}", firebaseUserId, e.getMessage());
            return ResponseEntity.badRequest().body(error(null, e.getMessage()).getJson());
        }
    }

    /**
     * Update the currently authenticated user's details.
     *
     * @param firebaseUserId the identifier of the current user derived from the authorization token.
     * @param request        the request payload containing the updated user details.
     * @return ResponseEntity indicating the status of the operation.
     */
    @PutMapping
    @Operation(summary = "Update Current User Data", description = "Update the first name and last name of the authenticated user.")
    public ResponseEntity<JSONObject> updateCurrentUser(
            @AuthenticationPrincipal String firebaseUserId,
            @Valid @RequestBody UpdateUserRequest request) {
        try {
            logger.info("Updating user data for Firebase User ID: {}", firebaseUserId);
            currentUserService.updateCurrentUser(firebaseUserId, request);
            return ok(success(null, "User details updated successfully").getJson());
        } catch (CustomMessagePresentException e) {
            logger.error("Validation error for Firebase User ID: {}: {}", firebaseUserId, e.getMessage());
            return ResponseEntity.badRequest().body(error(null, e.getMessage()).getJson());
        }
    }
}