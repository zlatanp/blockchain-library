package com.ftn.udd.controller;

import org.springframework.web.bind.annotation.RequestMapping;

public class HomeController {

    @RequestMapping("/")
    public String startMethod(){
        return "index.html";
    }
}
