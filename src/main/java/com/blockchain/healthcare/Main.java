package com.blockchain.healthcare;

import static spark.Spark.*;
import com.google.gson.Gson;
import spark.Spark;

public class Main {
    public static void main(String[] args) {
        // --- 1. Initialize the Admin Node ---
        Admin_Node adminNode = new Admin_Node(0, 4568);
        System.out.println("Blockchain Admin Node initialized.");

        // --- 2. Setup the SparkJava Web Server ---
        port(4567);

        // Serve static files from the 'frontend' directory on the classpath
        staticFiles.location("/frontend");
        
        // --- 3. Configure CORS ---
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        });

        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        System.out.println("API server listening on http://localhost:4567");

        // --- 4. Define API Endpoints ---

        post("/add_record", (request, response) -> {
            response.type("application/json");
            try {
                Gson gson = new Gson();
                Transaction tx = gson.fromJson(request.body(), Transaction.class);
                adminNode.proposeTransaction(tx);
                return "{\"success\": true, \"message\": \"Transaction proposed successfully!\"}";
            } catch (Exception e) {
                response.status(500);
                return "{\"success\": false, \"message\": \"Error: " + e.getMessage() + "\"}";
            }
        });

        get("/get_history", (request, response) -> {
            response.type("application/json");
            String recordId = request.queryParams("recordId");
            if (recordId == null || recordId.isEmpty()) {
                response.status(400);
                return "{\"success\": false, \"message\": \"'recordId' is required.\"}";
            }
            var history = adminNode.getRecordHistory(recordId);
            return new Gson().toJson(history);
        });

        get("/get_logs", (request, response) -> {
            response.type("application/json");
            var logs = adminNode.getAccessLogs();
            return new Gson().toJson(logs);
        });

        // Gracefully stop the Spark server when the application exits
        Runtime.getRuntime().addShutdownHook(new Thread(Spark::stop));
    }
}
