#!/bin/bash

echo "🏢 Setting up Facilities, Offices, and Coffee Machines..."
echo "========================================================"

# Check if backend is running
echo "1. Checking backend status..."
if ! curl -s http://localhost:8080/api/health > /dev/null; then
    echo "❌ Backend is not running. Please start it first:"
    echo "   cd backend && mvn spring-boot:run"
    exit 1
fi
echo "✅ Backend is running"

# Create test user if not exists
echo ""
echo "2. Creating/checking test user..."
SIGNUP_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "email": "admin@coffee.com", 
    "password": "password123",
    "role": "ROLE_ADMIN"
  }')

if echo "$SIGNUP_RESPONSE" | grep -q "already exists"; then
    echo "ℹ️  Admin user already exists"
elif echo "$SIGNUP_RESPONSE" | grep -q "admin"; then
    echo "✅ Admin user created"
else
    echo "ℹ️  Using existing admin user"
fi

# Login and get token
echo ""
echo "3. Logging in..."
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "password123"
  }')

if ! echo "$LOGIN_RESPONSE" | grep -q "jwt"; then
    echo "❌ Login failed: $LOGIN_RESPONSE"
    exit 1
fi

TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"jwt":"[^"]*"' | cut -d'"' -f4)
echo "✅ Login successful"

# Create Facilities
echo ""
echo "4. Creating facilities..."

# Create Pune facility
echo "   Creating Pune facility..."
PUNE_RESPONSE=$(curl -s -X POST http://localhost:8080/api/facilities \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Pune",
    "location": "Pune, Maharashtra, India"
  }')

if echo "$PUNE_RESPONSE" | grep -q "Pune"; then
    PUNE_ID=$(echo "$PUNE_RESPONSE" | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
    echo "   ✅ Pune facility created (ID: $PUNE_ID)"
elif echo "$PUNE_RESPONSE" | grep -q "already exists"; then
    echo "   ℹ️  Pune facility already exists, getting ID..."
    PUNE_ID=$(curl -s -X GET http://localhost:8080/api/facilities \
      -H "Authorization: Bearer $TOKEN" | grep -o '"id":"[^"]*","name":"Pune"' | cut -d'"' -f4)
    echo "   ✅ Pune facility found (ID: $PUNE_ID)"
else
    echo "   ❌ Failed to create Pune facility: $PUNE_RESPONSE"
fi

# Create Mumbai facility
echo "   Creating Mumbai facility..."
MUMBAI_RESPONSE=$(curl -s -X POST http://localhost:8080/api/facilities \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Mumbai",
    "location": "Mumbai, Maharashtra, India"
  }')

if echo "$MUMBAI_RESPONSE" | grep -q "Mumbai"; then
    MUMBAI_ID=$(echo "$MUMBAI_RESPONSE" | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
    echo "   ✅ Mumbai facility created (ID: $MUMBAI_ID)"
elif echo "$MUMBAI_RESPONSE" | grep -q "already exists"; then
    echo "   ℹ️  Mumbai facility already exists, getting ID..."
    MUMBAI_ID=$(curl -s -X GET http://localhost:8080/api/facilities \
      -H "Authorization: Bearer $TOKEN" | grep -o '"id":"[^"]*","name":"Mumbai"' | cut -d'"' -f4)
    echo "   ✅ Mumbai facility found (ID: $MUMBAI_ID)"
else
    echo "   ❌ Failed to create Mumbai facility: $MUMBAI_RESPONSE"
fi

# Create Offices in Pune
echo ""
echo "5. Creating offices in Pune..."

# Pune Office 1
echo "   Creating Pune Office 1..."
PUNE_OFFICE1_RESPONSE=$(curl -s -X POST http://localhost:8080/api/facilities \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Pune Office 1",
    "location": "Pune, Maharashtra, India - Ground Floor"
  }')

if echo "$PUNE_OFFICE1_RESPONSE" | grep -q "Pune Office 1"; then
    PUNE_OFFICE1_ID=$(echo "$PUNE_OFFICE1_RESPONSE" | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
    echo "   ✅ Pune Office 1 created (ID: $PUNE_OFFICE1_ID)"
else
    echo "   ❌ Failed to create Pune Office 1: $PUNE_OFFICE1_RESPONSE"
fi

# Pune Office 2
echo "   Creating Pune Office 2..."
PUNE_OFFICE2_RESPONSE=$(curl -s -X POST http://localhost:8080/api/facilities \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Pune Office 2",
    "location": "Pune, Maharashtra, India - First Floor"
  }')

if echo "$PUNE_OFFICE2_RESPONSE" | grep -q "Pune Office 2"; then
    PUNE_OFFICE2_ID=$(echo "$PUNE_OFFICE2_RESPONSE" | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
    echo "   ✅ Pune Office 2 created (ID: $PUNE_OFFICE2_ID)"
else
    echo "   ❌ Failed to create Pune Office 2: $PUNE_OFFICE2_RESPONSE"
fi

# Create Offices in Mumbai
echo ""
echo "6. Creating offices in Mumbai..."

# Mumbai Office 1
echo "   Creating Mumbai Office 1..."
MUMBAI_OFFICE1_RESPONSE=$(curl -s -X POST http://localhost:8080/api/facilities \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Mumbai Office 1",
    "location": "Mumbai, Maharashtra, India - Ground Floor"
  }')

if echo "$MUMBAI_OFFICE1_RESPONSE" | grep -q "Mumbai Office 1"; then
    MUMBAI_OFFICE1_ID=$(echo "$MUMBAI_OFFICE1_RESPONSE" | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
    echo "   ✅ Mumbai Office 1 created (ID: $MUMBAI_OFFICE1_ID)"
else
    echo "   ❌ Failed to create Mumbai Office 1: $MUMBAI_OFFICE1_RESPONSE"
fi

# Mumbai Office 2
echo "   Creating Mumbai Office 2..."
MUMBAI_OFFICE2_RESPONSE=$(curl -s -X POST http://localhost:8080/api/facilities \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Mumbai Office 2",
    "location": "Mumbai, Maharashtra, India - Second Floor"
  }')

if echo "$MUMBAI_OFFICE2_RESPONSE" | grep -q "Mumbai Office 2"; then
    MUMBAI_OFFICE2_ID=$(echo "$MUMBAI_OFFICE2_RESPONSE" | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
    echo "   ✅ Mumbai Office 2 created (ID: $MUMBAI_OFFICE2_ID)"
else
    echo "   ❌ Failed to create Mumbai Office 2: $MUMBAI_OFFICE2_RESPONSE"
fi

# Create Coffee Machines
echo ""
echo "7. Creating coffee machines..."

# Function to create machines for an office
create_machines_for_office() {
    local office_id=$1
    local office_name=$2
    local facility_name=$3
    
    echo "   Creating machines for $facility_name - $office_name..."
    
    # Machine 1
    MACHINE1_RESPONSE=$(curl -s -X POST http://localhost:8080/api/machines \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer $TOKEN" \
      -d "{
        \"name\": \"$office_name Machine 1\",
        \"model\": \"CoffeeMaster Pro\",
        \"serialNumber\": \"CM-001\",
        \"location\": \"$office_name\",
        \"facilityId\": $office_id,
        \"status\": \"ACTIVE\"
      }")
    
    if echo "$MACHINE1_RESPONSE" | grep -q "Machine 1"; then
        echo "     ✅ Machine 1 created"
    else
        echo "     ❌ Failed to create Machine 1: $MACHINE1_RESPONSE"
    fi
    
    # Machine 2
    MACHINE2_RESPONSE=$(curl -s -X POST http://localhost:8080/api/machines \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer $TOKEN" \
      -d "{
        \"name\": \"$office_name Machine 2\",
        \"model\": \"CoffeeMaster Elite\",
        \"serialNumber\": \"CM-002\",
        \"location\": \"$office_name\",
        \"facilityId\": $office_id,
        \"status\": \"ACTIVE\"
      }")
    
    if echo "$MACHINE2_RESPONSE" | grep -q "Machine 2"; then
        echo "     ✅ Machine 2 created"
    else
        echo "     ❌ Failed to create Machine 2: $MACHINE2_RESPONSE"
    fi
}

# Create machines for each office
create_machines_for_office $PUNE_OFFICE1_ID "Pune Office 1" "Pune"
create_machines_for_office $PUNE_OFFICE2_ID "Pune Office 2" "Pune"
create_machines_for_office $MUMBAI_OFFICE1_ID "Mumbai Office 1" "Mumbai"
create_machines_for_office $MUMBAI_OFFICE2_ID "Mumbai Office 2" "Mumbai"

# Summary
echo ""
echo "========================================================"
echo "✅ SETUP COMPLETED!"
echo ""
echo "📊 Summary:"
echo "   🏢 Facilities: 2 (Pune, Mumbai)"
echo "   🏢 Offices: 4 (2 in each facility)"
echo "   ☕ Coffee Machines: 8 (2 in each office)"
echo ""
echo "🔍 To verify, check:"
echo "   - Frontend: http://localhost:3000/facilities"
echo "   - Frontend: http://localhost:3000/machines"
echo "   - Database: mysql -u root -e 'USE coffeeappdb; SELECT * FROM facility; SELECT * FROM coffee_machine;'"
echo "========================================================"