package com.newSystem.ProductManagementSystemImplemented.dto.authenticationDTO;

import lombok.Data;

@Data
public class SignUpDTO {

    private String fname;
    private String lname;
    private String email;
    private String password;
    private String phoneNumber;
}
