package com.gogli.librarymanagementsystem.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum MembershipStatus {
    ACTIVE(0),
    SUSPENDED(1),
    EXPIRED(2);

    private final int value;

    MembershipStatus(int value) {
        this.value = value;
    }
    
    @JsonCreator
    public static MembershipStatus fromString(String value) {
        return MembershipStatus.valueOf(value.toUpperCase());
    }
}
