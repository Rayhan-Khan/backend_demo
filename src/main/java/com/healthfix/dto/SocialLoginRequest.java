package com.healthfix.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for social login request.
 */
@Data
public class SocialLoginRequest {
    @NotBlank(message = "ID token is required.")
    private String idToken;
}
