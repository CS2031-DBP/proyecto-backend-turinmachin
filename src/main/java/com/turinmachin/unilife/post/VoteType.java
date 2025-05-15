package com.turinmachin.unilife.post;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum VoteType {

    UPVOTE((short) 1),
    DOWNVOTE((short) -1);

    private final Short value;

    public static VoteType fromValue(short value) {
        return Arrays.stream(values())
                .filter(v -> v.value == value)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid vote value: " + value));
    }

}
