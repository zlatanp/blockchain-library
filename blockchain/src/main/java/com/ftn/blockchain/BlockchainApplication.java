package com.ftn.blockchain;

import com.ftn.blockchain.model.Block;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;

@SpringBootApplication
public class BlockchainApplication {

	public static ArrayList<Block> blockchain = new ArrayList<Block>();


	public static void main(String[] args) {
		SpringApplication.run(BlockchainApplication.class, args);
	}

}
