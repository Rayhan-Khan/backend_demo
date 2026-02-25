package com.healthfix.service;

import com.healthfix.dto.UpdateUserRequest;
import com.healthfix.response.CurrentUserResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CurrentUserService {

    /**
     * Sets the avatar for the specified user.
     *
     * @param file           the file containing the avatar image.
     * @param firebaseUserId the unique identifier of the user in Firebase.
     * @throws IOException if an error occurs while processing the avatar file.
     */
    void setAvatar(MultipartFile file, String firebaseUserId) throws IOException;

    /**
     * Fetch details of the currently authenticated user.
     *
     * @param firebaseUserId the identifier of the current user derived from the authorization token.
     * @return CurrentUserResponse containing the user details.
     */
    CurrentUserResponse getCurrentUser(String firebaseUserId);

    /**
     * Update the currently authenticated user's details.
     *
     * @param firebaseUserId the identifier of the current user derived from the authorization token.
     * @param request        the request payload containing the updated user details.
     */
    void updateCurrentUser(String firebaseUserId, UpdateUserRequest request);
}
