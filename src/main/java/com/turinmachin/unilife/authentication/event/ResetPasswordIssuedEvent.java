package com.turinmachin.unilife.authentication.event;

import com.turinmachin.unilife.user.domain.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResetPasswordIssuedEvent {

    private User user;
    private String token;

}
