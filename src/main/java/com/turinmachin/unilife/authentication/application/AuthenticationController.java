package com.turinmachin.unilife.authentication.application;

import com.turinmachin.unilife.authentication.domain.AuthenticationService;
import com.turinmachin.unilife.user.domain.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

}
