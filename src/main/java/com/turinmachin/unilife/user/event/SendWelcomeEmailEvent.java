package com.turinmachin.unilife.user.event;

import com.turinmachin.unilife.user.domain.User;
import lombok.Data;

@Data
public class SendWelcomeEmailEvent {

    private final User user;

}
