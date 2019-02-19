package com.ftn.udd.repository;

import com.ftn.udd.model.AreaCode;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AreaCodeRepository extends MongoRepository<AreaCode, String> {

    public AreaCode findByName(String name);
    public AreaCode findByCode(String code);

}
