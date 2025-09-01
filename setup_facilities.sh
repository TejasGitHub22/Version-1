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

if ! echo "$LOGIN_RESPONSE" | grep -q "token"; then
    echo "❌ Login failed: $LOGIN_RESPONSE"
    exit 1
fi

TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
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
    "address": "Pune, Maharashtra, India",
    "contactPerson": "Pune Manager",
    "contactEmail": "pune@coffee.com",
    "contactPhone": "+91-20-12345678"
  }')

if echo "$PUNE_RESPONSE" | grep -q "Pune"; then
    PUNE_ID=$(echo "$PUNE_RESPONSE" | grep -o '"id":[0-9]*' | cut -d':' -f2)
    echo "   ✅ Pune facility created (ID: $PUNE_ID)"
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
    "address": "Mumbai, Maharashtra, India",
    "contactPerson": "Mumbai Manager",
    "contactEmail": "mumbai@coffee.com",
    "contactPhone": "+91-22-87654321"
  }')

if echo "$MUMBAI_RESPONSE" | grep -q "Mumbai"; then
    MUMBAI_ID=$(echo "$MUMBAI_RESPONSE" | grep -o '"id":[0-9]*' | cut -d':' -f2)
    echo "   ✅ Mumbai facility created (ID: $MUMBAI_ID)"
else
    echo "   ❌ Failed to create Mumbai facility: $MUMBAI_RESPONSE"
fi

# Create Offices in Pune
echo ""
echo "5. Creating offices in Pune..."

# Pune Office 1
echo "   Creating Pune Office 1..."
PUNE_OFFICE1_RESPONSE=$(curl -s -X POST http://localhost:8080/api/facilities/$PUNE_ID/offices \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Pune Office 1",
    "floor": "Ground Floor",
    "capacity": 50
  }')

if echo "$PUNE_OFFICE1_RESPONSE" | grep -q "Pune Office 1"; then
    PUNE_OFFICE1_ID=$(echo "$PUNE_OFFICE1_RESPONSE" | grep -o '"id":[0-9]*' | cut -d':' -f2)
    echo "   ✅ Pune Office 1 created (ID: $PUNE_OFFICE1_ID)"
else
    echo "   ❌ Failed to create Pune Office 1: $PUNE_OFFICE1_RESPONSE"
fi

# Pune Office 2
echo "   Creating Pune Office 2..."
PUNE_OFFICE2_RESPONSE=$(curl -s -X POST http://localhost:8080/api/facilities/$PUNE_ID/offices \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Pune Office 2",
    "floor": "First Floor",
    "capacity": 75
  }')

if echo "$PUNE_OFFICE2_RESPONSE" | grep -q "Pune Office 2"; then
    PUNE_OFFICE2_ID=$(echo "$PUNE_OFFICE2_RESPONSE" | grep -o '"id":[0-9]*' | cut -d':' -f2)
    echo "   ✅ Pune Office 2 created (ID: $PUNE_OFFICE2_ID)"
else
    echo "   ❌ Failed to create Pune Office 2: $PUNE_OFFICE2_RESPONSE"
fi

# Create Offices in Mumbai
echo ""
echo "6. Creating offices in Mumbai..."

# Mumbai Office 1
echo "   Creating Mumbai Office 1..."
MUMBAI_OFFICE1_RESPONSE=$(curl -s -X POST http://localhost:8080/api/facilities/$MUMBAI_ID/offices \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Mumbai Office 1",
    "floor": "Ground Floor",
    "capacity": 100
  }')

if echo "$MUMBAI_OFFICE1_RESPONSE" | grep -q "Mumbai Office 1"; then
    MUMBAI_OFFICE1_ID=$(echo "$MUMBAI_OFFICE1_RESPONSE" | grep -o '"id":[0-9]*' | cut -d':' -f2)
    echo "   ✅ Mumbai Office 1 created (ID: $MUMBAI_OFFICE1_ID)"
else
    echo "   ❌ Failed to create Mumbai Office 1: $MUMBAI_OFFICE1_RESPONSE"
fi

# Mumbai Office 2
echo "   Creating Mumbai Office 2..."
MUMBAI_OFFICE2_RESPONSE=$(curl -s -X POST http://localhost:8080/api/facilities/$MUMBAI_ID/offices \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Mumbai Office 2",
    "floor": "Second Floor",
    "capacity": 80
  }')

if echo "$MUMBAI_OFFICE2_RESPONSE" | grep -q "Mumbai Office 2"; then
    MUMBAI_OFFICE2_ID=$(echo "$MUMBAI_OFFICE2_RESPONSE" | grep -o '"id":[0-9]*' | cut -d':' -f2)
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
        \"name\": \"$facility_name $office_name Machine 1\",
        \"model\": \"CoffeeMaster Pro\",
        \"serialNumber\": \"CM-${facility_name:0:3}-${office_name:0:3}-001\",
        \"location\": \"$office_name\",
        \"facilityId\": $facility_id,
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
        \"name\": \"$facility_name $office_name Machine 2\",
        \"model\": \"CoffeeMaster Elite\",
        \"serialNumber\": \"CM-${facility_name:0:3}-${office_name:0:3}-002\",
        \"location\": \"$office_name\",
        \"facilityId\": $facility_id,
        \"status\": \"ACTIVE\"
      }")
    
    if echo "$MACHINE2_RESPONSE" | grep -q "Machine 2"; then
        echo "     ✅ Machine 2 created"
    else
        echo "     ❌ Failed to create Machine 2: $MACHINE2_RESPONSE"
    fi
}

# Create machines for each office
create_machines_for_office $PUNE_ID "Pune Office 1" "Pune"
create_machines_for_office $PUNE_ID "Pune Office 2" "Pune"
create_machines_for_office $MUMBAI_ID "Mumbai Office 1" "Mumbai"
create_machines_for_office $MUMBAI_ID "Mumbai Office 2" "Mumbai"

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