#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}=== Reliable Task System Test Runner ===${NC}"

# Function to check if Docker is running
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        echo -e "${RED}Error: Docker is not running. Please start Docker and try again.${NC}"
        exit 1
    fi
    echo -e "${GREEN}✓ Docker is running${NC}"
}

# Function to stop and remove existing containers
cleanup_containers() {
    echo -e "${YELLOW}Cleaning up existing containers...${NC}"
    docker-compose down -v 2>/dev/null || true
    echo -e "${GREEN}✓ Containers cleaned up${NC}"
}

# Function to start containers
start_containers() {
    echo -e "${YELLOW}Starting PostgreSQL and Redis containers...${NC}"
    docker-compose up -d
    
    if [ $? -ne 0 ]; then
        echo -e "${RED}Error: Failed to start containers${NC}"
        exit 1
    fi
    echo -e "${GREEN}✓ Containers started${NC}"
}

# Function to wait for containers to be healthy
wait_for_containers() {
    echo -e "${YELLOW}Waiting for containers to be ready...${NC}"
    
    # Wait for PostgreSQL
    echo -e "${BLUE}Waiting for PostgreSQL...${NC}"
    timeout=60
    while [ $timeout -gt 0 ]; do
        if docker-compose exec -T postgres pg_isready -U postgres -d reliable_tasks > /dev/null 2>&1; then
            echo -e "${GREEN}✓ PostgreSQL is ready${NC}"
            break
        fi
        sleep 2
        timeout=$((timeout - 2))
    done
    
    if [ $timeout -le 0 ]; then
        echo -e "${RED}Error: PostgreSQL failed to start within 60 seconds${NC}"
        exit 1
    fi
    
    # Wait for Redis
    echo -e "${BLUE}Waiting for Redis...${NC}"
    timeout=30
    while [ $timeout -gt 0 ]; do
        if docker-compose exec -T redis redis-cli ping > /dev/null 2>&1; then
            echo -e "${GREEN}✓ Redis is ready${NC}"
            break
        fi
        sleep 2
        timeout=$((timeout - 2))
    done
    
    if [ $timeout -le 0 ]; then
        echo -e "${RED}Error: Redis failed to start within 30 seconds${NC}"
        exit 1
    fi
}

# Function to run the test
run_test() {
    echo -e "${YELLOW}Running Reliable Test Scenario...${NC}"
    
    # Compile the project
    echo -e "${BLUE}Compiling project...${NC}"
    mvn clean compile
    
    if [ $? -ne 0 ]; then
        echo -e "${RED}Error: Compilation failed${NC}"
        exit 1
    fi
    echo -e "${GREEN}✓ Project compiled successfully${NC}"
    
    # Run the test
    echo -e "${BLUE}Starting test scenario...${NC}"
    mvn exec:java -Dexec.mainClass="org.example.reliable.SimpleTestRunner"
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Test completed successfully${NC}"
    else
        echo -e "${RED}✗ Test failed${NC}"
        exit 1
    fi
}

# Function to show container logs
show_logs() {
    echo -e "${YELLOW}Container logs:${NC}"
    echo -e "${BLUE}PostgreSQL logs:${NC}"
    docker-compose logs postgres
    echo -e "${BLUE}Redis logs:${NC}"
    docker-compose logs redis
}

# Function to cleanup on exit
cleanup() {
    echo -e "${YELLOW}Cleaning up...${NC}"
    docker-compose down -v
    echo -e "${GREEN}✓ Cleanup completed${NC}"
}

# Set up trap to cleanup on script exit
trap cleanup EXIT

# Main execution
main() {
    check_docker
    cleanup_containers
    start_containers
    wait_for_containers
    run_test
    show_logs
}

# Run main function
main
