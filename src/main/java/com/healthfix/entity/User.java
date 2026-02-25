package com.healthfix.entity;

import com.healthfix.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serial;

@Getter
@Setter
@Entity
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@Table(name = "users")
public class User extends BaseEntityWithUpdate {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "first_name", length = 100, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 100, nullable = false)
    private String lastName;

    @Column(name = "email", length = 256, nullable = false, unique = true)
    @Email
    @Size(max = 256)
    private String email;

    @Column(name = "is_email_verified")
    private Boolean isEmailVerified = false;

    @Column(name = "firebase_user_id", length = 100, nullable = false)
    private String firebaseUserId;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", length = 20)
    private UserStatus userStatus;
}
