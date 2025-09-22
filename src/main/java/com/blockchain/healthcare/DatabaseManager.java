package com.blockchain.healthcare;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
            // This method securely gets credentials from an environment variable or a local file
            InputStream serviceAccountStream = getServiceAccountStream();
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("Successfully connected to Firebase.");
            }
        } catch (IOException e) {
            System.err.println("Failed to initialize Firebase: " + e.getMessage());
            throw new RuntimeException(e);
        }
        this.db = FirestoreClient.getFirestore();
    }

    /**
     * Securely reads Firebase credentials. For deployment, it reads from a secret
     * environment variable named FIREBASE_CREDENTIALS. For local development,
     * it falls back to reading the local 'serviceAccountKey.json' file.
     * @return An InputStream containing the service account credentials.
     */
    private InputStream getServiceAccountStream() throws IOException {
        String firebaseCredentials = System.getenv("FIREBASE_CREDENTIALS");
        if (firebaseCredentials != null && !firebaseCredentials.isEmpty()) {
            System.out.println("Loading Firebase credentials from environment variable.");
            return new ByteArrayInputStream(firebaseCredentials.getBytes(StandardCharsets.UTF_8));
        } else {
            System.out.println("Loading Firebase credentials from local 'serviceAccountKey.json' file.");
            return new FileInputStream("serviceAccountKey.json");
        }
    }
    
    public void saveBlock(Block block) {
        try {
            ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME)
                    .document(block.getBlockHash())
                    .set(block);
            future.get();
            System.out.println("Successfully saved block " + block.getBlockHash() + " to Firestore.");
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error saving block to Firestore: " + e.getMessage());
        }
    }

    public List<Block> loadBlockchain() {
        List<Block> blockchain = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            for (QueryDocumentSnapshot document : documents) {
                blockchain.add(document.toObject(Block.class));
            }
            System.out.println("Successfully loaded " + blockchain.size() + " blocks from Firestore.");
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error loading blockchain from Firestore: " + e.getMessage());
        }
        blockchain.sort((b1, b2) -> Long.compare(b1.getTimestamp(), b2.getTimestamp()));
        return blockchain;
    }
}

