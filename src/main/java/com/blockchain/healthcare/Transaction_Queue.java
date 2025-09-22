package com.blockchain.healthcare;

import java.util.ArrayList;
import java.util.List;

public class Transaction_Queue {
    private final List<Transaction> transaction_queue;
    private final int max_size;

    public Transaction_Queue(int max_size) {
        this.transaction_queue = new ArrayList<>();
        this.max_size = max_size;
    }

    public List<Transaction> getTransactions() {
        return new ArrayList<>(transaction_queue);
    }

    public boolean insert_transaction(Transaction t) {
        transaction_queue.add(t);
        return transaction_queue.size() >= max_size;
    }

    void removeTransactions(List<Transaction> toRemove) {
        for (Transaction tx : toRemove) {
            transaction_queue.removeIf(t -> t.getHashValue().equals(tx.getHashValue()));
        }
    }
    
    public void clear() {
        transaction_queue.clear();
    }
}
