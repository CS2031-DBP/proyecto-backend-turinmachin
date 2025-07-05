package com.turinmachin.unilife.user.application;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
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
import com.turinmachin.unilife.user.dto.UpdateUserDto;
import com.turinmachin.unilife.user.dto.UpdateUserPasswordDto;
import com.turinmachin.unilife.user.dto.UpdateUserProfilePictureDto;
import com.turinmachin.unilife.user.dto.UpdateUserRoleDto;
import com.turinmachin.unilife.user.dto.UserResponseDto;
import com.turinmachin.unilife.user.exception.UserNotFoundException;
import com.turinmachin.unilife.user.infrastructure.UserSpecifications;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final ModelMapper modelMapper;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public List<UserResponseDto> getAllUsers(
            @RequestParam(required = false) UUID universityId,
            @RequestParam(required = false) UUID degreeId) {
        Specification<User> spec = Specification
                .where(UserSpecifications.hasUniversityId(universityId))
                .and(UserSpecifications.hasDegreeId(degreeId));

        List<User> users = userService.getAllUsers(spec);
        return users.stream().map(user -> modelMapper.map(user, UserResponseDto.class)).toList();
    }

    @GetMapping("/@self")
    @PreAuthorize("hasRole('ROLE_USER')")
    public UserResponseDto getSelfUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return modelMapper.map(user, UserResponseDto.class);
    }

    @GetMapping("/{id}")
    public UserResponseDto getUserById(@PathVariable UUID id) {
        User user = userService.getUserById(id).orElseThrow(UserNotFoundException::new);
        return modelMapper.map(user, UserResponseDto.class);
    }

    @GetMapping("/username/{username}")
    public UserResponseDto getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username).orElseThrow(UserNotFoundException::new);
        return modelMapper.map(user, UserResponseDto.class);
    }

    @PutMapping("/@self")
    @PreAuthorize("hasRole('ROLE_USER')")
    public UserResponseDto updateUser(@Valid @RequestBody UpdateUserDto dto, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        user = userService.updateUser(user, dto);
        return modelMapper.map(user, UserResponseDto.class);
    }

    @PatchMapping("/@self/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_USER')")
    public void updateUserPassword(@Valid @RequestBody UpdateUserPasswordDto dto,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        userService.updateUserPassword(user, dto);
    }

    @PatchMapping("/@self/picture")
    @PreAuthorize("hasRole('ROLE_USER')")
    public UserResponseDto updateUserProfilePicture(@Valid @ModelAttribute UpdateUserProfilePictureDto dto,
            Authentication authentication) throws IOException {
        User user = (User) authentication.getPrincipal();
        user = userService.updateUserProfilePicture(user, dto.getPicture());
        return modelMapper.map(user, UserResponseDto.class);
    }

    @DeleteMapping("/@self/picture")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_USER')")
    public void deleteUserProfilePicture(Authentication authentication) throws IOException {
        User user = (User) authentication.getPrincipal();
        userService.deleteUserProfilePicture(user);
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public UserResponseDto updateUserRole(@PathVariable UUID id, @Valid @RequestBody UpdateUserRoleDto dto) {
        User user = userService.getUserById(id).orElseThrow(UserNotFoundException::new);
        user = userService.updateUserRole(user, dto.getRole());
        return modelMapper.map(user, UserResponseDto.class);
    }

    @DeleteMapping("/@self")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_USER')")
    public void deleteSelfUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        userService.deleteUser(user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public void deleteUser(@PathVariable UUID id) {
        User user = userService.getUserById(id).orElseThrow(UserNotFoundException::new);
        userService.deleteUser(user);
    }

}
