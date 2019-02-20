package com.ftn.blockchain.repository;

import com.ftn.blockchain.model.Block;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BlockRepository extends MongoRepository<Block, String> {
}
