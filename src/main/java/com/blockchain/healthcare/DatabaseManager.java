package com.blockchain.healthcare;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

public class DatabaseManager {

    private static final String COLLECTION_NAME = "blocks";
    private final Firestore db;

    public DatabaseManager() {
        try {
            // This looks for the key file in the root of your project folder
            FileInputStream serviceAccount = new FileInputStream("serviceAccountKey.json");
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            // This check prevents a crash if the app is re-initialized (common in development)
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("Successfully connected to Firebase.");
            }
        } catch (IOException e) {
            System.err.println("Error reading service account key. Make sure 'serviceAccountKey.json' is in the project root.");
            throw new RuntimeException(e);
        }
        this.db = FirestoreClient.getFirestore();
    }

    /**
     * Saves a single block to the Firestore database.
     * The block's hash is used as the document ID to ensure there are no duplicates.
     * @param block The block object to save.
     */
    public void saveBlock(Block block) {
        try {
            ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                    .document(block.getBlockHash())
                    .set(block);
            future.get(); // Wait for the write operation to complete
            System.out.println("Successfully saved block " + block.getBlockHash() + " to Firestore.");
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error saving block to Firestore: " + e.getMessage());
        }
    }

    /**
     * Loads the entire blockchain from the Firestore database.
     * @return A list of all blocks stored in the database, sorted by timestamp.
     */
    public List<Block> loadBlockchain() {
        List<Block> blockchain = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
            // The getDocuments() method returns a List of QueryDocumentSnapshot
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            for (QueryDocumentSnapshot document : documents) {
                blockchain.add(document.toObject(Block.class));
            }
            System.out.println("Successfully loaded " + blockchain.size() + " blocks from Firestore.");
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error loading blockchain from Firestore: " + e.getMessage());
        }
        
        // Firestore doesn't guarantee order, so we must sort the blocks by their timestamp
        // to ensure the integrity of the chain.
        blockchain.sort((b1, b2) -> Long.compare(b1.getTimestamp(), b2.getTimestamp()));
        return blockchain;
    }
}

