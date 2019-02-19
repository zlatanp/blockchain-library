package com.ftn.udd.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@NoArgsConstructor
public class AreaCode {

    @Id
    private String code;
    private String name;


    public AreaCode(String code, String name){
        this.code = code;
        this.name = name;
    }

}
