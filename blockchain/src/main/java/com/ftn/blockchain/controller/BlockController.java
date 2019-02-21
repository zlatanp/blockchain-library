package com.ftn.blockchain.controller;

import com.ftn.blockchain.model.Block;
import com.ftn.blockchain.repository.BlockRepository;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/blockchain")
public class BlockController {

    @Autowired
    private BlockRepository blockRepository;


    @RequestMapping(value = "/init", method = RequestMethod.GET, produces = "application/json")
    public String init() {

        Block block1 = new Block("Hi im the first block", "0");
        blockRepository.save(block1);
        String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(block1);

        return blockchainJson;
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET, produces = "application/json")
    public String getAll() {

        List<Block> blockchain = blockRepository.findAll();
        return new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
    }

    @RequestMapping(value = "/add/{id}", method = RequestMethod.POST, produces = "application/json")
    public String add(@PathVariable("id") String docId) {

        List<Block> blockchain = blockRepository.findAll();
        Block block = new Block(docId, blockchain.get(blockchain.size()-1).hash);
        blockRepository.save(block);
        return "200";
    }

}
