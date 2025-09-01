#!/bin/bash

echo "🔍 Verifying Facility Setup..."
echo "=============================="

# Check if backend is running
if ! curl -s http://localhost:8080/api/health > /dev/null; then
    echo "❌ Backend is not running. Please start it first:"
    echo "   cd backend && mvn spring-boot:run"
    exit 1
fi

# Login and get token
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "password123"
  }')

if ! echo "$LOGIN_RESPONSE" | grep -q "token"; then
    echo "❌ Login failed. Please run setup_facilities.sh first"
    exit 1
fi

TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

echo "✅ Connected to backend"

# Get facilities
echo ""
echo "📋 FACILITIES:"
echo "--------------"
FACILITIES_RESPONSE=$(curl -s -X GET http://localhost:8080/api/facilities \
  -H "Authorization: Bearer $TOKEN")

echo "$FACILITIES_RESPONSE" | grep -o '"name":"[^"]*"' | sed 's/"name":"//g' | sed 's/"//g' | while read facility; do
    echo "   🏢 $facility"
done

# Get machines
echo ""
echo "☕ COFFEE MACHINES:"
echo "------------------"
MACHINES_RESPONSE=$(curl -s -X GET http://localhost:8080/api/machines \
  -H "Authorization: Bearer $TOKEN")

echo "$MACHINES_RESPONSE" | grep -o '"name":"[^"]*"' | sed 's/"name":"//g' | sed 's/"//g' | while read machine; do
    echo "   ☕ $machine"
done

# Count totals
FACILITY_COUNT=$(echo "$FACILITIES_RESPONSE" | grep -o '"name":"[^"]*"' | wc -l)
MACHINE_COUNT=$(echo "$MACHINES_RESPONSE" | grep -o '"name":"[^"]*"' | wc -l)

echo ""
echo "📊 SUMMARY:"
echo "-----------"
echo "   🏢 Facilities: $FACILITY_COUNT"
echo "   ☕ Coffee Machines: $MACHINE_COUNT"

if [ "$FACILITY_COUNT" -eq 2 ] && [ "$MACHINE_COUNT" -eq 8 ]; then
    echo ""
    echo "✅ Perfect! Setup matches requirements:"
    echo "   - 2 Facilities (Pune, Mumbai)"
    echo "   - 8 Coffee Machines (2 per office, 4 offices total)"
else
    echo ""
    echo "⚠️  Setup incomplete. Expected 2 facilities and 8 machines."
fi

echo ""
echo "🌐 Access your data at:"
echo "   - Facilities: http://localhost:3000/facilities"
echo "   - Machines: http://localhost:3000/machines"
echo "=============================="