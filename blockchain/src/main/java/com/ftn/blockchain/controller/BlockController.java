package com.ftn.blockchain.controller;

import com.ftn.blockchain.model.Block;
import com.ftn.blockchain.repository.BlockRepository;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/blockchain")
public class BlockController {

    @Autowired
    private BlockRepository blockRepository;

    public static ArrayList<Block> blockchain = new ArrayList<Block>();

    @RequestMapping(value = "/test", method = RequestMethod.GET, produces = "application/json")
    public String test() {

        //add our blocks to the blockchain ArrayList:
        Block block1 = new Block("Hi im the first block", "0");
        blockchain.add(block1);
        blockRepository.save(block1);
        Block block2 = new Block("Yo im the second block", blockchain.get(blockchain.size() - 1).hash);
        blockchain.add(block2);
        blockRepository.save(block2);
        Block block3 = new Block("Hey im the third block", blockchain.get(blockchain.size() - 1).hash);
        blockchain.add(block3);
        blockRepository.save(block3);

        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        System.out.println(blockchainJson);

        return blockchainJson;
    }
}
