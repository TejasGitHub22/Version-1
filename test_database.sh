#!/bin/bash

echo "🔍 Testing Database Operations..."
echo "================================"

# Test 1: Check if backend is running
echo "1. Testing backend health..."
if curl -s http://localhost:8080/api/health > /dev/null; then
    echo "✅ Backend is running"
else
    echo "❌ Backend is not running. Start it with: cd backend && mvn spring-boot:run"
    exit 1
fi

# Test 2: Create a test user
echo ""
echo "2. Creating test user..."
SIGNUP_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com", 
    "password": "password123",
    "role": "ROLE_ADMIN"
  }')

if echo "$SIGNUP_RESPONSE" | grep -q "already exists"; then
    echo "ℹ️  Test user already exists"
elif echo "$SIGNUP_RESPONSE" | grep -q "testuser"; then
    echo "✅ Test user created successfully"
else
    echo "❌ Error creating user: $SIGNUP_RESPONSE"
fi

# Test 3: Login with test user
echo ""
echo "3. Testing login..."
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }')

if echo "$LOGIN_RESPONSE" | grep -q "token"; then
    echo "✅ Login successful, token received"
    
    # Extract token
    TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
    
    # Test 4: Get facilities
    echo ""
    echo "4. Checking facilities..."
    FACILITIES_RESPONSE=$(curl -s -X GET http://localhost:8080/api/facilities \
      -H "Authorization: Bearer $TOKEN")
    
    if echo "$FACILITIES_RESPONSE" | grep -q "\[\]"; then
        echo "✅ Facilities endpoint working (empty list)"
    elif echo "$FACILITIES_RESPONSE" | grep -q "facility"; then
        echo "✅ Facilities retrieved successfully"
    else
        echo "❌ Error getting facilities: $FACILITIES_RESPONSE"
    fi
    
    # Test 5: Get machines
    echo ""
    echo "5. Checking machines..."
    MACHINES_RESPONSE=$(curl -s -X GET http://localhost:8080/api/machines \
      -H "Authorization: Bearer $TOKEN")
    
    if echo "$MACHINES_RESPONSE" | grep -q "\[\]"; then
        echo "✅ Machines endpoint working (empty list)"
    elif echo "$MACHINES_RESPONSE" | grep -q "machine"; then
        echo "✅ Machines retrieved successfully"
    else
        echo "❌ Error getting machines: $MACHINES_RESPONSE"
    fi
    
else
    echo "❌ Login failed: $LOGIN_RESPONSE"
fi

echo ""
echo "================================"
echo "✅ Database test completed!"