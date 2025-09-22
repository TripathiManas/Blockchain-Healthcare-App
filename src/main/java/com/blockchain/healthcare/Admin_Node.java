package com.blockchain.healthcare;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Admin_Node extends Node {
    private final List<Block> blockchain;
    private final List<String> accessLogs;
    private final DatabaseManager dbManager;

    Admin_Node(int id, int port) {
        super(id, port);
        this.accessLogs = new ArrayList<>();
        this.dbManager = new DatabaseManager();

        // Load the existing blockchain from the database.
        this.blockchain = dbManager.loadBlockchain();

        // If the blockchain is empty (e.g., first time running), create a genesis block.
        if (this.blockchain.isEmpty()) {
            System.out.println("No existing blockchain found in database. Creating genesis block...");
            Block genesisBlock = new Block(new ArrayList<>(), "0", System.currentTimeMillis());
            this.blockchain.add(genesisBlock);
            this.dbManager.saveBlock(genesisBlock);
        }
        
        // The base Node class uses a simple list of hashes. We need to synchronize it
        // with the full blockchain we just loaded.
        this.block_hashes = this.blockchain.stream()
                                           .map(Block::getBlockHash)
                                           .collect(Collectors.toList());

        System.out.println("Admin Node initialized with " + this.blockchain.size() + " blocks.");
    }

    /**
     * Overrides the default proposeTransaction to mine a block immediately. This makes the
     * application feel more responsive for demonstration purposes, ensuring data is saved
     * to the database right away.
     */
    @Override
    void proposeTransaction(Transaction t) {
        System.out.println("Admin Node proposing transaction: " + t.getRecordId());
        // Add the transaction to the queue.
        this.tq.insert_transaction(t);
        // Immediately mine a new block instead of waiting for the queue to be full.
        System.out.println("Transaction proposed. Mining a new block immediately.");
        mineBlock();
    }

    /**
     * Overrides the default mineBlock method to add database persistence.
     */
    @Override
    Block mineBlock() {
        // Use the superclass method to create the block, perform proof-of-work, and broadcast it.
        Block newBlock = super.mineBlock();
        
        // Add the new block to our full, in-memory copy of the chain.
        this.blockchain.add(newBlock);

        // Save the new block to the Firebase database for persistence.
        this.dbManager.saveBlock(newBlock);
        
        System.out.println("Admin Node mined a new block. Total blocks now: " + this.blockchain.size());
        return newBlock;
    }

    /**
     * Scans the in-memory blockchain (loaded from DB) to find all transactions
     * for a specific recordId.
     * @param recordId The ID of the record to search for.
     * @return A list of all transactions related to the recordId.
     */
    public List<Transaction> getRecordHistory(String recordId) {
        String logMessage = "History requested for recordId: " + recordId + " at " + new Date();
        accessLogs.add(logMessage);
        System.out.println(logMessage);

        return blockchain.stream()
                .flatMap(block -> block.getTransactions().stream())
                .filter(tx -> tx.getRecordId().equals(recordId))
                .collect(Collectors.toList());
    }

    /**
     * Returns a copy of the access logs for the current server session.
     * @return A list of all access log strings.
     */
    public List<String> getAccessLogs() {
        String logMessage = "Admin access logs requested at " + new Date();
        accessLogs.add(logMessage);
        System.out.println(logMessage);
        return new ArrayList<>(accessLogs);
    }
}

