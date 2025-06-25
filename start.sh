#!/bin/bash

# Set color output
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m' # No color

# Required Java version
REQUIRED_JAVA_VERSION="1.8"

echo -e "${GREEN}Checking Java version...${NC}"
JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')

# Check if Java version matches required version
if [[ $JAVA_VERSION == $REQUIRED_JAVA_VERSION* ]]; then
  echo -e "${GREEN}Java version OK: $JAVA_VERSION${NC}"
else
  echo -e "${RED}Error: Java 8 is required. Current version: $JAVA_VERSION${NC}"
  exit 1
fi

# Define cleanup function to kill background processes
cleanup() {
  echo -e "\n${RED}Stopping all processes...${NC}"
  [[ -n "$BACKEND_PID" ]] && kill $BACKEND_PID 2>/dev/null && echo -e "${RED}Backend stopped.${NC}"
  [[ -n "$FRONTEND_PID" ]] && kill $FRONTEND_PID 2>/dev/null && echo -e "${RED}Frontend stopped.${NC}"
  exit 0
}

# Trap SIGINT (Ctrl+C) to run cleanup
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

# Inform user both services are running
echo -e "${GREEN}Backend (PID $BACKEND_PID) and frontend (PID $FRONTEND_PID) started.${NC}"
echo -e "${GREEN}Press Ctrl+C to stop both.${NC}"

# Wait for both processes
wait $BACKEND_PID
wait $FRONTEND_PID
