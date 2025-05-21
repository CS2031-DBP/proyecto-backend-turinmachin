package com.turinmachin.unilife.post.domain;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VoteType {
    UPVOTE((short) 1),
    DOWNVOTE((short) -1);

    private final short value;

    public static VoteType fromValue(short value) {
        return Arrays.stream(values())
                .filter(v -> v.value == value)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid vote value: " + value));
    }
}
