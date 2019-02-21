package com.ftn.udd.model;

import com.ftn.udd.enumeration.UserType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String city;
    private String country;
    private UserType userType;
    private String privateKey;
    private String publicKey;
    private String pinCode;

    public User(String email, String password, String firstName, String lastName, String city, String country, UserType userType) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
        this.country = country;
        this.userType = userType;
    }

    public User(String email, String name, String city, String country){
        this.email = email;
        this.firstName = name;
        this.city = city;
        this.country = country;
    }
}
