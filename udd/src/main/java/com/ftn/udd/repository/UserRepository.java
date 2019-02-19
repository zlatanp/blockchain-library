package com.ftn.udd.repository;

import com.ftn.udd.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String>{

    //repository.save(new User(...))                                Create
    //repository.findAll()                                          Read
    public User findByEmail(String email);                    //Read specific

    //repository.save(u);                                           Update
    //repository.delete(repository.findByUsername("username1"));    Delete specific
}
