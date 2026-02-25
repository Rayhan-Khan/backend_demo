package com.healthfix.enums;

import lombok.Getter;

@Getter
public enum ReferenceType {

    UsersAvatars(0);

    private final Integer label;

    ReferenceType(Integer label) {
        this.label = label;
    }
}