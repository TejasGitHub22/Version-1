#!/bin/bash

echo "🔍 Checking Simulator Data in Database..."
echo "========================================"

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

# Check machines and their real-time data
echo ""
echo "☕ COFFEE MACHINES & REAL-TIME DATA:"
echo "------------------------------------"
MACHINES_RESPONSE=$(curl -s -X GET http://localhost:8080/api/machines \
  -H "Authorization: Bearer $TOKEN")

# Parse and display machine data
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

# Check usage history
echo "📊 USAGE HISTORY:"
echo "-----------------"
USAGE_RESPONSE=$(curl -s -X GET http://localhost:8080/api/usage-history \
  -H "Authorization: Bearer $TOKEN")

if echo "$USAGE_RESPONSE" | grep -q "\[\]"; then
    echo "   ℹ️  No usage history found yet"
else
    echo "$USAGE_RESPONSE" | grep -o '{"id":[^}]*}' | while read usage; do
        USAGE_ID=$(echo "$usage" | grep -o '"id":[0-9]*' | cut -d':' -f2)
        MACHINE_ID=$(echo "$usage" | grep -o '"machineId":[0-9]*' | cut -d':' -f2)
        BREW_TYPE=$(echo "$usage" | grep -o '"brewType":"[^"]*"' | cut -d'"' -f4)
        TIMESTAMP=$(echo "$usage" | grep -o '"timestamp":"[^"]*"' | cut -d'"' -f4)
        
        echo "   📝 Usage ID: $USAGE_ID | Machine: $MACHINE_ID | Brew: $BREW_TYPE | Time: $TIMESTAMP"
    done
fi

# Check if simulator is running
echo ""
echo "🤖 SIMULATOR STATUS:"
echo "--------------------"
if curl -s http://localhost:8081/actuator/health > /dev/null 2>&1; then
    echo "   ✅ Simulator is running on port 8081"
    
    # Check simulator analytics
    echo ""
    echo "📈 SIMULATOR ANALYTICS:"
    echo "----------------------"
    
    # Brew types
    BREW_TYPES=$(curl -s http://localhost:8081/api/analytics/usage/brew-types 2>/dev/null)
    if [ $? -eq 0 ]; then
        echo "   ☕ Brew Types: $BREW_TYPES"
    else
        echo "   ❌ Could not fetch brew types"
    fi
    
    # Resource averages
    RESOURCES=$(curl -s http://localhost:8081/api/analytics/resources/averages 2>/dev/null)
    if [ $? -eq 0 ]; then
        echo "   📊 Resource Averages: $RESOURCES"
    else
        echo "   ❌ Could not fetch resource averages"
    fi
    
    # Recent activity
    ACTIVITY=$(curl -s http://localhost:8081/api/analytics/recent-activity 2>/dev/null)
    if [ $? -eq 0 ]; then
        ACTIVITY_COUNT=$(echo "$ACTIVITY" | grep -o '{"machineId":[^}]*}' | wc -l)
        echo "   🕒 Recent Activity: $ACTIVITY_COUNT records"
    else
        echo "   ❌ Could not fetch recent activity"
    fi
    
else
    echo "   ❌ Simulator is not running on port 8081"
    echo "   💡 Start it with: cd simulator && mvn spring-boot:run"
fi

# Database check
echo ""
echo "🗄️  DATABASE CHECK:"
echo "-------------------"
if mysql -u root -e "USE coffeeappdb; SELECT COUNT(*) as machine_count FROM coffee_machine; SELECT COUNT(*) as usage_count FROM usage_history;" 2>/dev/null; then
    echo "   ✅ Database accessible"
else
    echo "   ❌ Could not access database directly"
    echo "   💡 Try: mysql -u root -pTejas2004 -e 'USE coffeeappdb; SHOW TABLES;'"
fi

echo ""
echo "========================================"
echo "✅ Simulator data check completed!"
echo ""
echo "💡 Tips:"
echo "   - If no data: Start simulator with 'cd simulator && mvn spring-boot:run'"
echo "   - Check MQTT connection in backend logs"
echo "   - Verify machine IDs match between simulator and database"
echo "========================================"