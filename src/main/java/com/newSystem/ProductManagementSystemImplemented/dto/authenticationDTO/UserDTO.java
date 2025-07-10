package com.newSystem.ProductManagementSystemImplemented.dto.authenticationDTO;


import com.newSystem.ProductManagementSystemImplemented.enums.UserRoles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String fname;
    private String lname;
    private String email;
    private String username;
    private String password;
    private String phoneNumber;
    private UserRoles userRole;

}
