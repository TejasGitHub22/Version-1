#!/bin/bash

echo "🔄 Testing Data Consistency Across All Pages..."
echo "=============================================="

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

# Test 1: Get all machines and their statuses
echo ""
echo "📊 TEST 1: Machine Status Consistency"
echo "------------------------------------"
MACHINES_RESPONSE=$(curl -s -X GET http://localhost:8080/api/machines \
  -H "Authorization: Bearer $TOKEN")

echo "Machine Status Summary:"
echo "$MACHINES_RESPONSE" | grep -o '{"id":[^}]*}' | while read machine; do
    MACHINE_ID=$(echo "$machine" | grep -o '"id":[0-9]*' | cut -d':' -f2)
    MACHINE_NAME=$(echo "$machine" | grep -o '"name":"[^"]*"' | cut -d'"' -f4)
    STATUS=$(echo "$machine" | grep -o '"status":"[^"]*"' | cut -d'"' -f4)
    TEMP=$(echo "$machine" | grep -o '"temperature":[0-9.]*' | cut -d':' -f2)
    WATER=$(echo "$machine" | grep -o '"waterLevel":[0-9.]*' | cut -d':' -f2)
    MILK=$(echo "$machine" | grep -o '"milkLevel":[0-9.]*' | cut -d':' -f2)
    BEANS=$(echo "$machine" | grep -o '"beansLevel":[0-9.]*' | cut -d':' -f2)
    SUGAR=$(echo "$machine" | grep -o '"sugarLevel":[0-9.]*' | cut -d':' -f2)
    LAST_UPDATE=$(echo "$machine" | grep -o '"lastUpdate":"[^"]*"' | cut -d'"' -f4)
    
    echo "   ☕ $MACHINE_NAME (ID: $MACHINE_ID)"
    echo "      Status: $STATUS | Temp: ${TEMP}°C"
    echo "      Water: ${WATER}% | Milk: ${MILK}% | Beans: ${BEANS}% | Sugar: ${SUGAR}%"
    echo "      Last Update: $LAST_UPDATE"
    echo ""
done

# Test 2: Test machine status update
echo "🔄 TEST 2: Machine Status Update Test"
echo "------------------------------------"
FIRST_MACHINE_ID=$(echo "$MACHINES_RESPONSE" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)

if [ -n "$FIRST_MACHINE_ID" ]; then
    echo "Testing status update for Machine ID: $FIRST_MACHINE_ID"
    
    # Get current status
    CURRENT_STATUS=$(echo "$MACHINES_RESPONSE" | grep -o '"id":'$FIRST_MACHINE_ID'[^}]*"status":"[^"]*"' | grep -o '"status":"[^"]*"' | cut -d'"' -f4)
    echo "Current status: $CURRENT_STATUS"
    
    # Toggle status
    NEW_STATUS="OFF"
    if [ "$CURRENT_STATUS" = "OFF" ]; then
        NEW_STATUS="ON"
    fi
    
    echo "Updating status to: $NEW_STATUS"
    
    # Update status
    UPDATE_RESPONSE=$(curl -s -X PUT http://localhost:8080/api/machines/$FIRST_MACHINE_ID/status \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer $TOKEN" \
      -d "{\"status\": \"$NEW_STATUS\"}")
    
    if echo "$UPDATE_RESPONSE" | grep -q "$NEW_STATUS"; then
        echo "✅ Status update successful"
        
        # Verify the change
        sleep 2
        UPDATED_MACHINES=$(curl -s -X GET http://localhost:8080/api/machines \
          -H "Authorization: Bearer $TOKEN")
        
        UPDATED_STATUS=$(echo "$UPDATED_MACHINES" | grep -o '"id":'$FIRST_MACHINE_ID'[^}]*"status":"[^"]*"' | grep -o '"status":"[^"]*"' | cut -d'"' -f4)
        
        if [ "$UPDATED_STATUS" = "$NEW_STATUS" ]; then
            echo "✅ Status change verified across API"
        else
            echo "❌ Status change not reflected in API"
        fi
    else
        echo "❌ Status update failed: $UPDATE_RESPONSE"
    fi
else
    echo "❌ No machines found to test"
fi

# Test 3: Usage count consistency
echo ""
echo "📈 TEST 3: Usage Count Consistency"
echo "---------------------------------"
USAGE_RESPONSE=$(curl -s -X GET http://localhost:8080/api/usage-history \
  -H "Authorization: Bearer $TOKEN")

TOTAL_USAGE=$(echo "$USAGE_RESPONSE" | grep -o '{"id":[^}]*}' | wc -l)
echo "Total usage records: $TOTAL_USAGE"

# Count by machine
echo "Usage by machine:"
echo "$USAGE_RESPONSE" | grep -o '"machineId":[0-9]*' | sort | uniq -c | while read count machine_id; do
    MACHINE_ID=$(echo "$machine_id" | cut -d':' -f2)
    echo "   Machine $MACHINE_ID: $count brews"
done

# Test 4: Check if simulator data is flowing
echo ""
echo "🤖 TEST 4: Simulator Data Flow"
echo "-----------------------------"
if curl -s http://localhost:8081/actuator/health > /dev/null 2>&1; then
    echo "✅ Simulator is running"
    
    # Check if MQTT data is being received
    echo "Checking for recent machine updates..."
    RECENT_UPDATES=$(echo "$MACHINES_RESPONSE" | grep -o '"lastUpdate":"[^"]*"' | head -5)
    if [ -n "$RECENT_UPDATES" ]; then
        echo "Recent machine updates found:"
        echo "$RECENT_UPDATES" | while read update; do
            echo "   $update"
        done
    else
        echo "⚠️  No recent updates found"
    fi
else
    echo "❌ Simulator is not running"
    echo "💡 Start it with: ./start_simulator.sh"
fi

# Test 5: Database consistency check
echo ""
echo "🗄️  TEST 5: Database Consistency"
echo "-------------------------------"
if mysql -u root -e "USE coffeeappdb; SELECT COUNT(*) as machine_count FROM coffee_machine; SELECT COUNT(*) as usage_count FROM usage_history;" 2>/dev/null; then
    echo "✅ Database accessible"
    
    # Check for data consistency
    DB_MACHINE_COUNT=$(mysql -u root -e "USE coffeeappdb; SELECT COUNT(*) FROM coffee_machine;" 2>/dev/null | tail -1)
    API_MACHINE_COUNT=$(echo "$MACHINES_RESPONSE" | grep -o '"id":[0-9]*' | wc -l)
    
    if [ "$DB_MACHINE_COUNT" = "$API_MACHINE_COUNT" ]; then
        echo "✅ Machine count consistent: DB=$DB_MACHINE_COUNT, API=$API_MACHINE_COUNT"
    else
        echo "❌ Machine count mismatch: DB=$DB_MACHINE_COUNT, API=$API_MACHINE_COUNT"
    fi
    
    DB_USAGE_COUNT=$(mysql -u root -e "USE coffeeappdb; SELECT COUNT(*) FROM usage_history;" 2>/dev/null | tail -1)
    if [ "$DB_USAGE_COUNT" = "$TOTAL_USAGE" ]; then
        echo "✅ Usage count consistent: DB=$DB_USAGE_COUNT, API=$TOTAL_USAGE"
    else
        echo "❌ Usage count mismatch: DB=$DB_USAGE_COUNT, API=$TOTAL_USAGE"
    fi
else
    echo "❌ Could not access database directly"
fi

echo ""
echo "=============================================="
echo "✅ Data consistency test completed!"
echo ""
echo "💡 Tips for maintaining consistency:"
echo "   - All pages now use centralized DataContext"
echo "   - Data refreshes every 10 seconds automatically"
echo "   - Status updates trigger full data refresh"
echo "   - Usage counts are calculated from real database data"
echo "=============================================="