package com.newSystem.ProductManagementSystemImplemented.dto.authenticationDTO;

import lombok.Data;

@Data
public class AuthenticationRequest {

    private String email;
    private String password;
}
