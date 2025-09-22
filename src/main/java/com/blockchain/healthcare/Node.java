package com.blockchain.healthcare;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;

public class Node {
    protected int id;
    protected Transaction_Queue tq;
    protected List<String> block_hashes;
    protected int total_nodes;
    protected List<String[]> peers = new ArrayList<>();
    protected NetworkManager network;

    public Node(int id, int port) {
        this.id = id;
        this.tq = new Transaction_Queue(10); // Mine a block after 10 transactions
        this.block_hashes = new ArrayList<>();
        this.total_nodes = 1;
        this.network = new NetworkManager(port, this);
        network.startServer();
    }

    /**
     * Broadcasts a message to all known peers in the network.
     * @param msg The message to send.
     */
    protected void broadcast(String msg) {
        for (String[] peer : peers) {
            network.sendMessage(peer[0], Integer.parseInt(peer[1]), msg);
        }
    }

    /**
     * Creates a new block, performs a simple proof-of-work, and broadcasts it.
     * @return The newly mined Block.
     */
    Block mineBlock() {
        System.out.println("Node " + id + " is mining a block...");
        String lastHash = block_hashes.isEmpty() ? "0" : block_hashes.get(block_hashes.size() - 1);
        Block newBlock = new Block(tq.getTransactions(), lastHash, new Date().getTime());
        newBlock.mineBlock(2); // Using a simple proof of work with a difficulty of 2.

        // Add the new block's hash to the local list and clear the transaction queue.
        block_hashes.add(newBlock.getBlockHash());
        tq.clear();

        // Broadcast the newly mined block to the entire network.
        broadcast("BLOCK_MINED|" + newBlock.toString());
        return newBlock;
    }

    /**
     * Proposes a new transaction to the network.
     * @param t The transaction to propose.
     */
    void proposeTransaction(Transaction t) {
        // Broadcast the new transaction to all peers.
        broadcast("NEW_TX|" + new Gson().toJson(t));
        
        // This node adds the transaction to its own queue.
        // If the queue becomes full after adding, it triggers the mining of a new block.
        if (tq.insert_transaction(t)) {
            mineBlock();
        }
    }

    /**
     * The central message handler for all incoming network communication.
     * It parses messages and calls the appropriate function.
     * @param rawMessage The raw message string received from another node.
     */
    public void receiveMessage(String rawMessage) {
        System.out.println("Node " + id + " received: " + rawMessage);
        String[] parts = rawMessage.split("\\|", 2);
        String command = parts[0];
        String data = parts.length > 1 ? parts[1] : "";

        switch (command) {
            case "NEW_TX":
                // A new transaction has been received. Deserialize it and add to the queue.
                Transaction tx = new Gson().fromJson(data, Transaction.class);
                if (tq.insert_transaction(tx)) {
                    mineBlock(); // If the queue is now full, start mining.
                }
                break;
            case "BLOCK_MINED":
                // A new block has been mined by another node. Deserialize and add its hash.
                Block b = new Gson().fromJson(data, Block.class);
                if (!block_hashes.contains(b.getBlockHash())) {
                    block_hashes.add(b.getBlockHash());
                    // In a real blockchain, extensive validation of the block would happen here.
                }
                break;
            case "NEW_NODE":
                // A new node has joined the network (simplified for this assignment).
                total_nodes++;
                break;
        }
    }
}

