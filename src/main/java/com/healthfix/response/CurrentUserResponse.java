package com.healthfix.response;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for returning current user details.
 */
@Getter
@Setter
public class CurrentUserResponse {

    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean isEmailVerified;
    private String status;
    private String firebaseUserId;
    private String joinDate;
    private String avatar;
}