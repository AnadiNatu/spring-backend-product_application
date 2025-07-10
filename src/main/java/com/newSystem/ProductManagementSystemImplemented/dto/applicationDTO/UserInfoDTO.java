package com.newSystem.ProductManagementSystemImplemented.dto.applicationDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfoDTO {

    private String fname;
    private String lname;
    private String email;
    private String phoneNumber;
    private List<Long> orderIds;

}
