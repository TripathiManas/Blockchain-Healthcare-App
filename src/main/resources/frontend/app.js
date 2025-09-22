// ===================================================================================
//  1. FIREBASE INITIALIZATION
// ===================================================================================
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
  apiKey: "AIzaSyABcQ7BaJ80unZK5C0zdGgbABB0dcQVQ-4",
  authDomain: "blockchainassignment-b0f6c.firebaseapp.com",
  projectId: "blockchainassignment-b0f6c",
  storageBucket: "blockchainassignment-b0f6c.firebasestorage.app",
  messagingSenderId: "449646217738",
  appId: "1:449646217738:web:9995ced48835d2a01d2df3",
  measurementId: "G-41ZCCZLYMY"
};
// -----------------------------------------------------------------

// Import functions from the Firebase SDK
import { initializeApp } from "https://www.gstatic.com/firebasejs/9.15.0/firebase-app.js";
import { 
    getAuth, 
    createUserWithEmailAndPassword, 
    signInWithEmailAndPassword, 
    signOut, 
    onAuthStateChanged,
    updateProfile 
} from "https://www.gstatic.com/firebasejs/9.15.0/firebase-auth.js";

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const auth = getAuth();

// ===================================================================================
//  2. DOM ELEMENT SELECTORS
// ===================================================================================
const loginPage = document.getElementById('login-page');
const registerPage = document.getElementById('register-page');
const dashboardPage = document.getElementById('dashboard-page');

const loginForm = document.getElementById('login-form');
const registerForm = document.getElementById('register-form');

const showRegisterLink = document.getElementById('show-register');
const showLoginLink = document.getElementById('show-login');
const logoutButton = document.getElementById('logout-button');

const userInfo = document.getElementById('user-info');

// Feature sections
const doctorFeatures = document.getElementById('doctor-features');
const adminFeatures = document.getElementById('admin-features');
const viewHistoryFeature = document.getElementById('view-history-feature');

// Forms & Outputs
const recordForm = document.getElementById('record-form');
const viewHistoryForm = document.getElementById('view-history-form');
const fetchLogsButton = document.getElementById('fetch-logs-button');
const historyOutput = document.getElementById('history-output');
const logsOutput = document.getElementById('logs-output');

// ===================================================================================
//  3. PAGE NAVIGATION LOGIC
// ===================================================================================
function showPage(pageId) {
    document.querySelectorAll('.page').forEach(page => page.classList.remove('active'));
    document.getElementById(pageId).classList.add('active');
}

showRegisterLink.addEventListener('click', (e) => { e.preventDefault(); showPage('register-page'); });
showLoginLink.addEventListener('click', (e) => { e.preventDefault(); showPage('login-page'); });

// ===================================================================================
//  4. AUTHENTICATION LOGIC (Firebase)
// ===================================================================================
registerForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const name = document.getElementById('register-name').value;
    const email = document.getElementById('register-email').value;
    const password = document.getElementById('register-password').value;
    const role = document.getElementById('register-role').value;

    try {
        const userCredential = await createUserWithEmailAndPassword(auth, email, password);
        // We store the role in the displayName for simplicity, separated by a unique marker.
        await updateProfile(userCredential.user, { displayName: `${name}::${role}` });

        alert('Registration successful! Please login.');
        registerForm.reset();
        showPage('login-page');

    } catch (error) {
        alert('Error registering: ' + error.message);
    }
});

loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;

    try {
        await signInWithEmailAndPassword(auth, email, password);
        // onAuthStateChanged will handle showing the dashboard automatically.
    } catch (error) {
        alert('Error logging in: ' + error.message);
    }
});

logoutButton.addEventListener('click', async () => {
    await signOut(auth);
    // onAuthStateChanged will handle showing the login page automatically.
});

// ===================================================================================
//  5. SESSION HANDLING & DYNAMIC DASHBOARD UI
// ===================================================================================
onAuthStateChanged(auth, (user) => {
    if (user) {
        // User is signed in. Let's parse their role from the displayName.
        const [fullName, userRole] = (user.displayName || '::').split('::');
        
        userInfo.textContent = `Welcome, ${fullName} (${userRole})`;
        
        // Hide all optional sections by default
        doctorFeatures.style.display = 'none';
        adminFeatures.style.display = 'none';
        viewHistoryFeature.style.display = 'none';

        // Show sections based on role
        if (userRole === 'Doctor') {
            doctorFeatures.style.display = 'block';
        }
        if (userRole === 'Patient') {
            viewHistoryFeature.style.display = 'block';
        }
        if (userRole === 'Administrator') {
            viewHistoryFeature.style.display = 'block';
            adminFeatures.style.display = 'block';
        }

      showPage('dashboard-page');
    } else {
        // User is signed out.
        showPage('login-page');
    }
});

// ===================================================================================
//  6. BACKEND INTERACTION
// ===================================================================================
const API_BASE_URL = 'http://localhost:4567';

async function addRecordToBlockchain(recordData) {
    try {
        const response = await fetch(`${API_BASE_URL}/add_record`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(recordData)
        });
        return await response.json();
    } catch (error) {
        console.error("Error adding record:", error);
        return { success: false, message: "Could not connect to the backend." };
    }
}

async function getRecordHistoryFromBlockchain(recordId) {
    try {
        const response = await fetch(`${API_BASE_URL}/get_history?recordId=${recordId}`);
        const data = await response.json();
        return { success: true, history: data };
    } catch (error) {
        console.error("Error fetching history:", error);
        return { success: false, history: [] };
    }
}

async function getAccessLogsFromBlockchain() {
    try {
        const response = await fetch(`${API_BASE_URL}/get_logs`);
        const data = await response.json();
        return { success: true, logs: data };
    } catch (error) {
        console.error("Error fetching logs:", error);
        return { success: false, logs: [] };
    }
}

// ===================================================================================
//  7. DASHBOARD FORM SUBMISSIONS
// ===================================================================================
recordForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const recordData = {
        hospitalId: document.getElementById('hospital-id').value,
        doctorId: document.getElementById('doctor-id').value,
        patientId: document.getElementById('patient-id').value,
        insuranceId: document.getElementById('insurance-id').value,
        recordId: document.getElementById('record-id').value,
        recordType: document.getElementById('record-type').value,
        operation: document.getElementById('operation-type').value,
        prescription: document.getElementById('prescription-details').value,
        amount: document.getElementById('amount').value,
        timestamp: new Date().getTime() // Send as long/bigint
    };
    
    const result = await addRecordToBlockchain(recordData);
    if (result.success) {
        alert(result.message);
        recordForm.reset();
    } else {
        alert('Failed to submit record: ' + result.message);
    }
});

viewHistoryForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const recordId = document.getElementById('history-record-id').value;
    const result = await getRecordHistoryFromBlockchain(recordId);

    if (result.success && result.history.length > 0) {
        // Format the history for display
        const formattedHistory = result.history.map(tx => JSON.stringify(tx, null, 2)).join('\n\n---\n\n');
        historyOutput.textContent = formattedHistory;
    } else {
        historyOutput.textContent = 'No history found for this record ID.';
    }
});

fetchLogsButton.addEventListener('click', async () => {
    const result = await getAccessLogsFromBlockchain();
    if (result.success && result.logs.length > 0) {
        logsOutput.textContent = result.logs.join('\n');
    } else {
        logsOutput.textContent = 'No access logs found.';
    }
});

// ===================================================================================