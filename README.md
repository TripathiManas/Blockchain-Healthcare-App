Blockchain Healthcare Record Management System
1. Introduction
This project is a secure, tamper-proof patient record management system built using a custom blockchain implementation in Java. It addresses challenges like medical record authenticity, unauthorized edits, and data tampering by leveraging a distributed ledger. The system features a web-based user interface for interaction and a persistent backend that stores the blockchain data in Google's Firebase Firestore.

2. Features Implemented
This project successfully implements all the required features as per the assignment specification:

User Registration: A dynamic web interface allows new Patients, Doctors, and Administrators to register for the system. User roles determine the features they can access.

Role-Based Access Control: The dashboard dynamically changes based on the logged-in user's role:

Doctors can add or update patient records.

Patients can view the complete history of their own records.

Administrators can view the history of any record and access a consolidated log of all system activities.

Record Adding/Updating: Doctors can submit new medical records (Diagnoses, Prescriptions, Test Results) through a dedicated form. Each submission becomes a transaction on the blockchain.

Record History Viewing: Authorized users can input a Record ID to view its complete, immutable history, showcasing every transaction related to that record.

Access Logs: The system maintains a running log of every attempted access and modification to each record, which is visible only to administrators.

Merkle Tree: Each block securely hashes all of its transactions into a single Merkle Root, ensuring the integrity and tamper-resistance of the data within the block.

Consensus Algorithm (Proof of Work): A simple Proof-of-Work (PoW) consensus mechanism is implemented. Before a new block can be added to the chain, a "miner" (in this case, the Admin Node) must solve a computational puzzle, ensuring security and agreement across the network.

3. Technology Stack
This project is a full-stack application utilizing the following technologies:

Backend:

Language: Java (JDK 11)

Build Tool: Apache Maven (handles dependencies and compilation)

Web Server: SparkJava (lightweight framework for the API)

Database: Google Firebase Firestore (for persistent storage of the blockchain)

Authentication: Firebase Admin SDK (for secure backend operations)

Libraries: Google Gson (for JSON data handling), Google Guava (for hashing).

Frontend:

Structure: HTML5

Styling: CSS3

Logic & Interactivity: JavaScript (ES6 Modules)

Authentication: Firebase Web SDK (for user registration, login, and session management).

4. Setup and Installation Instructions
To run this project, you will need Java, Maven, and a free Firebase account.

Step 1: Prerequisites
Java Development Kit (JDK) 11 or higher: Download Link

Apache Maven: Installation Guide

Git: Download Link

Step 2: Clone the Repository
Clone this project to your local machine:

git clone <your-private-github-repo-url>
cd <repository-folder-name>

Step 3: Firebase Setup
This project uses Firebase for user authentication and to store the blockchain data.

Create a Firebase Project: Go to the Firebase Console and create a new project.

Enable Services:

Authentication: Go to the "Authentication" section, click "Get started," and enable the Email/Password provider.

Firestore: Go to the "Firestore Database" section, click "Create database," and start in test mode.

Get Frontend Credentials:

Go to Project Settings (gear icon) -> Your apps.

Create a new Web App (</>).

Firebase will provide a firebaseConfig object. Copy this object.

Configure app.js:

Open the file src/main/resources/frontend/app.js.

Paste the copied firebaseConfig object into the placeholder at the top of the file.

Get Backend Credentials:

Go to Project Settings -> Service accounts.

Click "Generate new private key". A .json file will be downloaded.

Rename this file to serviceAccountKey.json.

Move this file into the root directory of the project (the same level as pom.xml).

Step 4: Run the Application
Open a terminal and navigate to the root directory of the project.

Compile and Download Dependencies: Run the following Maven command. It will download all necessary libraries and compile the Java code.

mvn clean install

Run the Server: After the build is successful, start the Java backend server:

mvn exec:java

Access the Application: Open your web browser and go to the following URL:
http://localhost:4567

5. Usage Guide
Register a User: Use the registration form to create an account as a Patient, Doctor, or Administrator.

Login: Log in with your newly created credentials.

Dashboard: You will be redirected to a dashboard with features corresponding to your role.

Add a Record (Doctor): Fill out the "Add/Update Patient Record" form and click "Submit to Blockchain." A new block will be mined and saved to the database.

View History (Patient/Administrator): Enter a Record ID into the "View Record History" form and click "Fetch History" to see all transactions for that record.

View Logs (Administrator): Click the "Fetch All Access Logs" button to see a log of all history and log requests made during the current server session.

6. Group Information
Group Number: [Your Group Number]

Group Members:

Manas Tripathi - 2023A7PS0129H


