// Test script to check database operations
const axios = require('axios');

const API_BASE_URL = 'http://localhost:8080/api';

async function testDatabaseOperations() {
    try {
        console.log('🔍 Testing Database Operations...\n');

        // Test 1: Check if backend is running
        console.log('1. Testing backend health...');
        try {
            const healthResponse = await axios.get(`${API_BASE_URL}/health`);
            console.log('✅ Backend is running');
        } catch (error) {
            console.log('❌ Backend is not running. Start it with: cd backend && mvn spring-boot:run');
            return;
        }

        // Test 2: Create a test user
        console.log('\n2. Creating test user...');
        try {
            const signupResponse = await axios.post(`${API_BASE_URL}/auth/signup`, {
                username: 'testuser',
                email: 'test@example.com',
                password: 'password123',
                role: 'ROLE_ADMIN'
            });
            console.log('✅ Test user created:', signupResponse.data);
        } catch (error) {
            if (error.response?.status === 400 && error.response?.data?.includes('already exists')) {
                console.log('ℹ️  Test user already exists');
            } else {
                console.log('❌ Error creating user:', error.response?.data || error.message);
            }
        }

        // Test 3: Login with test user
        console.log('\n3. Testing login...');
        try {
            const loginResponse = await axios.post(`${API_BASE_URL}/auth/login`, {
                username: 'testuser',
                password: 'password123'
            });
            console.log('✅ Login successful, token received');
            const token = loginResponse.data.token;

            // Test 4: Get facilities (should be empty initially)
            console.log('\n4. Checking facilities...');
            try {
                const facilitiesResponse = await axios.get(`${API_BASE_URL}/facilities`, {
                    headers: { Authorization: `Bearer ${token}` }
                });
                console.log('✅ Facilities retrieved:', facilitiesResponse.data.length, 'facilities');
            } catch (error) {
                console.log('❌ Error getting facilities:', error.response?.data || error.message);
            }

            // Test 5: Get machines
            console.log('\n5. Checking machines...');
            try {
                const machinesResponse = await axios.get(`${API_BASE_URL}/machines`, {
                    headers: { Authorization: `Bearer ${token}` }
                });
                console.log('✅ Machines retrieved:', machinesResponse.data.length, 'machines');
            } catch (error) {
                console.log('❌ Error getting machines:', error.response?.data || error.message);
            }

        } catch (error) {
            console.log('❌ Login failed:', error.response?.data || error.message);
        }

    } catch (error) {
        console.log('❌ General error:', error.message);
    }
}

// Run the test
testDatabaseOperations();