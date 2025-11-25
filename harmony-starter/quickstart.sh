#!/bin/bash

# Check if Node.js is installed
if command -v node &> /dev/null
then
    echo "Node.js is installed."
    node -v  # Print the installed Node.js version
else
    echo "Node.js is NOT installed."
    exit 1  # Exit the script
fi

(cd ../lib/api && ./mvnw clean install -DskipTests)
(cd api && ./mvnw clean install -DskipTests)

cd api
./mvnw spring-boot:run &
APP_PID=$!  # Capture the process ID
cd ..


(cd ../lib/ui && npm i)
(cd ui && npm i)

cd ui
npm run dev &
UI_APP_PID=$!
cd ..

# Function to handle script termination
cleanup() {
    echo "Stopping Spring Boot application..."
    kill $APP_PID
    wait $APP_PID 2>/dev/null  # Ensure the process is properly terminated
    echo "Application stopped."

    echo "Stopping Vue.js application..."
    kill $UI_APP_PID
    wait $UI_APP_PID 2>/dev/null  # Ensure the process is properly terminated
    echo "Vue.js Application stopped."
    exit 0
}

URL="http://localhost:3000"

if command -v xdg-open &> /dev/null; then
    xdg-open "$URL"
elif command -v open &> /dev/null; then
    open "$URL"
else
    echo "Could not detect the default browser."
    exit 1
fi

# Trap termination signals and call cleanup
trap cleanup SIGINT SIGTERM

# Wait for the application process to finish
wait $APP_PID

