package com.blockchain.healthcare;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.hash.Hashing;
import com.google.gson.Gson;

public class Block {
    private long timestamp;
    private List<Transaction> transactions;
    private String previousHash;
    private String merkleRoot;
    private int nonce;
    private String hash;

    // A no-argument constructor is required for deserialization from the database.
    public Block() {
        this.transactions = new ArrayList<>();
    }

    /**
     * Constructor for creating a new block.
     * @param transactions The list of transactions to include in the block.
     * @param previousHash The hash of the previous block in the chain.
     * @param timestamp The creation time of the block.
     */
    public Block(List<Transaction> transactions, String previousHash, long timestamp) {
        this.timestamp = timestamp;
        this.transactions = transactions != null ? transactions : new ArrayList<>();
        this.previousHash = previousHash;
        this.nonce = 0;
        this.merkleRoot = calculateMerkleRoot(); // Calculate Merkle root before hash
        this.hash = calculateBlockHash(); // Calculate initial hash
    }

    /**
     * Calculates the SHA-256 hash of the block's essential header data.
     * @return The calculated hash as a String.
     */
    public String calculateBlockHash() {
        String dataToHash = timestamp + previousHash + merkleRoot + nonce;
        return Hashing.sha256().hashString(dataToHash, StandardCharsets.UTF_8).toString();
    }

    /**
     * Calculates the Merkle Root from the list of transactions.
     * The Merkle Root is a secure way to verify the integrity of all transactions.
     * @return The Merkle Root hash as a String.
     */
    public String calculateMerkleRoot() {
        if (transactions == null || transactions.isEmpty()) {
            return Hashing.sha256().hashString("", StandardCharsets.UTF_8).toString();
        }
        // Get the hash of each transaction
        List<String> tree = transactions.stream().map(Transaction::getHashValue).collect(Collectors.toList());
        
        // Repeatedly hash pairs of nodes until only one root hash remains
        while (tree.size() > 1) {
            List<String> newTree = new ArrayList<>();
            for (int i = 0; i < tree.size(); i += 2) {
                String left = tree.get(i);
                // Handle cases with an odd number of transactions by duplicating the last one
                String right = (i + 1 < tree.size()) ? tree.get(i + 1) : left;
                String combinedHash = Hashing.sha256().hashString(left + right, StandardCharsets.UTF_8).toString();
                newTree.add(combinedHash);
            }
            tree = newTree;
        }
        return tree.get(0);
    }

    /**
     * A simple Proof-of-Work algorithm.
     * It increments the nonce until the block's hash starts with a certain number of zeros.
     * @param difficulty The number of leading zeros required in the hash.
     */
    public void mineBlock(int difficulty) {
        this.merkleRoot = calculateMerkleRoot(); // Recalculate in case transactions changed
        String target = new String(new char[difficulty]).replace('\0', '0');
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateBlockHash();
        }
        System.out.println("Block Mined!!! : " + hash);
    }

    // --- Getters and Setters (JavaBean convention) for JSON and Database mapping ---
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public List<Transaction> getTransactions() { return transactions; }
    public void setTransactions(List<Transaction> transactions) { this.transactions = transactions; }
    public String getPreviousHash() { return previousHash; }
    public void setPreviousHash(String previousHash) { this.previousHash = previousHash; }
    public String getMerkleRoot() { return merkleRoot; }
    public void setMerkleRoot(String merkleRoot) { this.merkleRoot = merkleRoot; }
    public int getNonce() { return nonce; }
    public void setNonce(int nonce) { this.nonce = nonce; }
    public String getHash() { return hash; }
    public void setHash(String hash) { this.hash = hash; }

    /**
     * This is the specific method the Node class was missing in the previous version.
     */
    public String getBlockHash() {
        return this.hash;
    }

    /**
     * Converts the Block object to its JSON string representation.
     * @return A JSON string of the block.
     */
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}

