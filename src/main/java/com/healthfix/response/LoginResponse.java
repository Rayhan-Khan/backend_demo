package com.healthfix.response;

import lombok.Builder;
import lombok.Data;

/**
 * DTO for login response.
 */
@Data
@Builder
public class LoginResponse {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean isEmailVerified;
    private String firebaseUserId;
}
