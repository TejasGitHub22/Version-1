#!/bin/bash

echo "🤖 Starting Coffee Machine Simulator..."
echo "======================================"

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

# Get machine IDs from database
echo ""
echo "🔍 Getting machine IDs from database..."
MACHINES_RESPONSE=$(curl -s -X GET http://localhost:8080/api/machines \
  -H "Authorization: Bearer $TOKEN")

# Extract machine IDs
MACHINE_IDS=$(echo "$MACHINES_RESPONSE" | grep -o '"id":[0-9]*' | cut -d':' -f2 | tr '\n' ',' | sed 's/,$//')

if [ -z "$MACHINE_IDS" ]; then
    echo "❌ No machines found in database. Please run setup_facilities.sh first"
    exit 1
fi

echo "✅ Found machine IDs: $MACHINE_IDS"

# Update simulator configuration with real machine IDs
echo ""
echo "⚙️  Updating simulator configuration..."

# Create a temporary application.properties for simulator
cat > /workspace/simulator/src/main/resources/application-simulator.properties << EOF
# Simulator Configuration
server.port=8081

# Machine IDs from database
simulator.machine.ids=$MACHINE_IDS

# MQTT Configuration
mqtt.broker.url=tcp://broker.hivemq.com:1883
mqtt.username=your-hivemq-username
mqtt.password=your-hivemq-password

# Database Configuration (for simulator's own data)
spring.datasource.url=jdbc:h2:mem:simulatordb
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.driver-class-name=org.h2.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect

# Logging
logging.level.com.coffeemachine.simulator=INFO
logging.level.org.eclipse.paho=WARN
EOF

echo "✅ Simulator configuration updated"

# Start the simulator
echo ""
echo "🚀 Starting simulator..."
cd /workspace/simulator

# Kill any existing simulator process
pkill -f "simulator.*spring-boot:run" 2>/dev/null

# Start simulator in background
nohup mvn spring-boot:run -Dspring-boot.run.profiles=simulator > simulator.log 2>&1 &
SIMULATOR_PID=$!

echo "✅ Simulator started with PID: $SIMULATOR_PID"
echo "📝 Logs: /workspace/simulator/simulator.log"

# Wait for simulator to start
echo ""
echo "⏳ Waiting for simulator to start..."
for i in {1..30}; do
    if curl -s http://localhost:8081/actuator/health > /dev/null 2>&1; then
        echo "✅ Simulator is running on port 8081"
        break
    fi
    echo "   Waiting... ($i/30)"
    sleep 2
done

if ! curl -s http://localhost:8081/actuator/health > /dev/null 2>&1; then
    echo "❌ Simulator failed to start. Check logs:"
    echo "   tail -f /workspace/simulator/simulator.log"
    exit 1
fi

echo ""
echo "🎉 SIMULATOR STARTED SUCCESSFULLY!"
echo "=================================="
echo "📊 Simulator Analytics: http://localhost:8081/api/analytics"
echo "🔍 Check data: ./check_simulator_data.sh"
echo "📝 View logs: tail -f /workspace/simulator/simulator.log"
echo "🛑 Stop simulator: kill $SIMULATOR_PID"
echo "=================================="