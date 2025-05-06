package com.eucl.rw.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationDTO {
    private String name;
    private String email;
    private String phone;

    private String nationalId;
    private String password;

}
