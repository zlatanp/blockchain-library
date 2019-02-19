package com.ftn.udd.controller;

import com.ftn.udd.enumeration.UserType;
import com.ftn.udd.model.User;
import com.ftn.udd.repository.AreaCodeRepository;
import com.ftn.udd.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AreaCodeRepository areaCodeRepository;

    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json")
    public String login(@RequestParam("email") String email, @RequestParam("password") String password){

        if(email.isEmpty() || password.isEmpty())
            return "nill";

        User u = userRepository.findByEmail(email);
        if(u == null) {
            return "nouser";
        }else{
            if(!u.getPassword().equals(password)){
                return "badpass";
            }else{
                if(u.getUserType().equals(UserType.STUDENT))
                    return "student";
                if(u.getUserType().equals(UserType.PROFESSOR))
                    return "professor";
                if(u.getUserType().equals(UserType.ADMIN))
                    return "admin";
            }
        }
        return "nill";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = "application/json")
    public String register(@RequestParam("email") String email, @RequestParam("pass") String pass, @RequestParam("passRepeat") String passRepeat, @RequestParam("firstName") String firstname, @RequestParam("lastName") String lastName, @RequestParam("city") String city, @RequestParam("country") String country, @RequestParam("type") String type){

        if(email.isEmpty() || pass.isEmpty() || passRepeat.isEmpty() || firstname.isEmpty() || lastName.isEmpty() || city.isEmpty() || country.isEmpty())
            return "nill";

        if(!pass.equals(passRepeat))
            return "passwordmatch";

        User u = userRepository.findByEmail(email);
        if(u != null) {
            return "used";
        }else {
            if (type.equals("Student")) {
                User user = new User(email, pass, firstname, lastName, city, country, UserType.STUDENT);
                userRepository.save(user);
            }else if (type.equals("Professor")) {
                User user = new User(email, pass, firstname, lastName, city, country, UserType.PROFESSOR);
                userRepository.save(user);
            }
            return "ok";
        }
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET, produces = "application/json")
    public List<User> getAll(){

        return userRepository.findAll();
    }


}
