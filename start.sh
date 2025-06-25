#!/bin/bash

# Set color output
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m' # No color

# Ensure the script is not run as root
if [ "$EUID" -eq 0 ]; then
  echo -e "${RED}Error: Do not run this script as root. Please use a regular user account.${NC}"
  exit 1
fi

# Required Java version
REQUIRED_JAVA_VERSION="1.8"

echo -e "${GREEN}Checking Java version...${NC}"
JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')

if [[ $JAVA_VERSION == $REQUIRED_JAVA_VERSION* ]]; then
  echo -e "${GREEN}Java version OK: $JAVA_VERSION${NC}"
else
  echo -e "${RED}Error: Java 8 is required. Current version: $JAVA_VERSION${NC}"
  exit 1
fi

# Extract MySQL host and port from application.yml
YML_PATH="./springboot/src/main/resources/application.yml"
DB_HOST=$(grep -E "jdbc:mysql://" "$YML_PATH" | sed -E 's/.*jdbc:mysql:\/\/([^:\/]+):([0-9]+).*/\1/')
DB_PORT=$(grep -E "jdbc:mysql://" "$YML_PATH" | sed -E 's/.*jdbc:mysql:\/\/([^:\/]+):([0-9]+).*/\2/')

echo -e "${GREEN}Checking database connectivity to $DB_HOST:$DB_PORT...${NC}"
if nc -z "$DB_HOST" "$DB_PORT"; then
  echo -e "${GREEN}Database connection OK.${NC}"
else
  echo -e "${RED}Error: Cannot connect to MySQL at $DB_HOST:$DB_PORT. Please make sure the database is running.${NC}"
  exit 1
fi

# Define cleanup function
cleanup() {
  echo -e "\n${RED}Stopping all processes...${NC}"
  [[ -n "$BACKEND_PID" ]] && kill $BACKEND_PID 2>/dev/null && echo -e "${RED}Backend stopped.${NC}"
  [[ -n "$FRONTEND_PID" ]] && kill $FRONTEND_PID 2>/dev/null && echo -e "${RED}Frontend stopped.${NC}"
  exit 0
}

# Trap Ctrl+C
trap cleanup SIGINT

# Start Spring Boot backend
echo -e "${GREEN}Starting Spring Boot backend...${NC}"
cd springboot || { echo -e "${RED}Backend folder not found!${NC}"; exit 1; }
mvn spring-boot:run &
BACKEND_PID=$!
cd ..

# Start Vue frontend
echo -e "${GREEN}Starting Vue frontend...${NC}"
cd vue || { echo -e "${RED}Frontend folder not found!${NC}"; exit 1; }
npm install
npm run serve &
FRONTEND_PID=$!
cd ..

# Final message
echo -e "${GREEN}Backend (PID $BACKEND_PID) and frontend (PID $FRONTEND_PID) started.${NC}"
echo -e "${GREEN}Press Ctrl+C to stop both.${NC}"

# Wait for both processes
wait $BACKEND_PID
wait $FRONTEND_PID
