package com.turinmachin.unilife.user.application;

import java.io.IOException;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.turinmachin.unilife.user.domain.User;
import com.turinmachin.unilife.user.domain.UserService;
import com.turinmachin.unilife.user.dto.SelfUserResponseDto;
import com.turinmachin.unilife.user.dto.UpdateUserDto;
import com.turinmachin.unilife.user.dto.UpdateUserPasswordDto;
import com.turinmachin.unilife.user.dto.UpdateUserProfilePictureDto;
import com.turinmachin.unilife.user.dto.UpdateUserRoleDto;
import com.turinmachin.unilife.user.dto.UserResponseDto;
import com.turinmachin.unilife.user.exception.UserNotFoundException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public Page<UserResponseDto> getAllUsers(@RequestParam(required = false) final String query,
            final Pageable pageable,
            final Authentication authentication) {
        final User authUser = (User) authentication.getPrincipal();
        Page<User> users;

        if (query == null || query.isEmpty()) {
            users = userService.getUsersExcluding(authUser.getId(), pageable);
        } else {
            users = userService.searchUsersExcluding(query, authUser.getId(), pageable);
        }
        return users.map(user -> modelMapper.map(user, UserResponseDto.class));
    }

    @GetMapping("/@self")
    @PreAuthorize("hasRole('ROLE_USER')")
    public SelfUserResponseDto getSelfUser(final Authentication authentication) {
        final User user = (User) authentication.getPrincipal();
        return modelMapper.map(user, SelfUserResponseDto.class);
    }

    @GetMapping("/{id}")
    public UserResponseDto getUserById(@PathVariable final UUID id) {
        final User user = userService.getUserById(id).orElseThrow(UserNotFoundException::new);
        return modelMapper.map(user, UserResponseDto.class);
    }

    @GetMapping("/username/{username}")
    public UserResponseDto getUserByUsername(@PathVariable final String username) {
        final User user = userService.getUserByUsername(username).orElseThrow(UserNotFoundException::new);
        return modelMapper.map(user, UserResponseDto.class);
    }

    @PutMapping("/@self")
    @PreAuthorize("hasRole('ROLE_USER')")
    public SelfUserResponseDto updateUser(@Valid @RequestBody final UpdateUserDto dto,
            final Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        user = userService.updateUser(user, dto);
        return modelMapper.map(user, SelfUserResponseDto.class);
    }

    @PatchMapping("/@self/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_USER')")
    public void updateUserPassword(@Valid @RequestBody final UpdateUserPasswordDto dto,
            final Authentication authentication) {
        final User user = (User) authentication.getPrincipal();
        userService.updateUserPassword(user, dto);
    }

    @PatchMapping("/@self/picture")
    @PreAuthorize("hasRole('ROLE_USER')")
    public SelfUserResponseDto updateUserProfilePicture(@Valid @ModelAttribute final UpdateUserProfilePictureDto dto,
            final Authentication authentication) throws IOException {
        User user = (User) authentication.getPrincipal();
        user = userService.updateUserProfilePicture(user, dto.getPicture());
        return modelMapper.map(user, SelfUserResponseDto.class);
    }

    @DeleteMapping("/@self/picture")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_USER')")
    public void deleteUserProfilePicture(final Authentication authentication) throws IOException {
        final User user = (User) authentication.getPrincipal();
        userService.deleteUserProfilePicture(user);
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public UserResponseDto updateUserRole(@PathVariable final UUID id,
            @Valid @RequestBody final UpdateUserRoleDto dto) {
        User user = userService.getUserById(id).orElseThrow(UserNotFoundException::new);
        user = userService.updateUserRole(user, dto.getRole());
        return modelMapper.map(user, UserResponseDto.class);
    }

    @DeleteMapping("/@self")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_USER')")
    public void deleteSelfUser(final Authentication authentication) {
        final User user = (User) authentication.getPrincipal();
        userService.deleteUser(user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public void deleteUser(@PathVariable final UUID id) {
        final User user = userService.getUserById(id).orElseThrow(UserNotFoundException::new);
        userService.deleteUser(user);
    }

}
