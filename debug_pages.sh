#!/bin/bash

echo "🔍 Debugging Pages Loading Issues..."
echo "===================================="

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

# Test facilities API
echo ""
echo "🏢 Testing Facilities API..."
echo "----------------------------"
FACILITIES_RESPONSE=$(curl -s -X GET http://localhost:8080/api/facilities \
  -H "Authorization: Bearer $TOKEN")

if echo "$FACILITIES_RESPONSE" | grep -q "\[\]"; then
    echo "✅ Facilities API working (empty list)"
elif echo "$FACILITIES_RESPONSE" | grep -q "facility"; then
    echo "✅ Facilities API working (has data)"
    FACILITY_COUNT=$(echo "$FACILITIES_RESPONSE" | grep -o '"id":[0-9]*' | wc -l)
    echo "   Found $FACILITY_COUNT facilities"
else
    echo "❌ Facilities API error: $FACILITIES_RESPONSE"
fi

# Test machines API
echo ""
echo "☕ Testing Machines API..."
echo "-------------------------"
MACHINES_RESPONSE=$(curl -s -X GET http://localhost:8080/api/machines \
  -H "Authorization: Bearer $TOKEN")

if echo "$MACHINES_RESPONSE" | grep -q "\[\]"; then
    echo "✅ Machines API working (empty list)"
elif echo "$MACHINES_RESPONSE" | grep -q "machine"; then
    echo "✅ Machines API working (has data)"
    MACHINE_COUNT=$(echo "$MACHINES_RESPONSE" | grep -o '"id":[0-9]*' | wc -l)
    echo "   Found $MACHINE_COUNT machines"
else
    echo "❌ Machines API error: $MACHINES_RESPONSE"
fi

# Test usage history API
echo ""
echo "📊 Testing Usage History API..."
echo "-------------------------------"
USAGE_RESPONSE=$(curl -s -X GET http://localhost:8080/api/usage-history \
  -H "Authorization: Bearer $TOKEN")

if echo "$USAGE_RESPONSE" | grep -q "\[\]"; then
    echo "✅ Usage History API working (empty list)"
elif echo "$USAGE_RESPONSE" | grep -q "usage"; then
    echo "✅ Usage History API working (has data)"
    USAGE_COUNT=$(echo "$USAGE_RESPONSE" | grep -o '"id":[0-9]*' | wc -l)
    echo "   Found $USAGE_COUNT usage records"
else
    echo "❌ Usage History API error: $USAGE_RESPONSE"
fi

# Check if frontend is running
echo ""
echo "🌐 Testing Frontend..."
echo "---------------------"
if curl -s http://localhost:3000 > /dev/null; then
    echo "✅ Frontend is running on port 3000"
else
    echo "❌ Frontend is not running. Start it with:"
    echo "   cd frontend && npm start"
fi

echo ""
echo "===================================="
echo "✅ Debug completed!"
echo ""
echo "💡 If APIs are working but pages aren't loading:"
echo "   1. Check browser console for JavaScript errors"
echo "   2. Check if DataContext is properly imported"
echo "   3. Verify all API endpoints are accessible"
echo "===================================="