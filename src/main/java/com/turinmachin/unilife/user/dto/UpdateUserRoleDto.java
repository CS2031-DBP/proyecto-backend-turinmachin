package com.turinmachin.unilife.user.dto;

import com.turinmachin.unilife.user.domain.Role;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserRoleDto {

    @NotNull
    private Role role;

}
