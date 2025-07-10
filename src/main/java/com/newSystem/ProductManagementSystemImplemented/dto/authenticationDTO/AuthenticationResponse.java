package com.newSystem.ProductManagementSystemImplemented.dto.authenticationDTO;

import com.newSystem.ProductManagementSystemImplemented.enums.UserRoles;
import lombok.Data;

@Data
public class AuthenticationResponse {

    private Long userId;
    private String jwt;
    private String fullName;
    private UserRoles userRoles;
}

