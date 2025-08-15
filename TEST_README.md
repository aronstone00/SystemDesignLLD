# Reliable Task System - Test Setup

This document describes how to set up and run tests for the Reliable Task System using Docker containers for PostgreSQL and Redis.

## Prerequisites

- Docker and Docker Compose installed
- Java 21 or higher
- Maven 3.6 or higher

## Quick Start

### 1. Run the Complete Test Suite

```bash
./run-test.sh
```

This script will:
- Start PostgreSQL and Redis containers
- Wait for services to be ready
- Compile the project
- Run the test scenario
- Show container logs
- Clean up containers

### 2. Manual Setup

If you prefer to run things manually:

#### Start the containers:
```bash
docker-compose up -d
```

#### Wait for services to be ready:
```bash
# Check PostgreSQL
docker-compose exec postgres pg_isready -U postgres -d reliable_tasks

# Check Redis
docker-compose exec redis redis-cli ping
```

#### Run the test:
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="org.example.reliable.SimpleTestRunner"
```

#### Stop the containers:
```bash
docker-compose down -v
```

## Test Scenarios

### SimpleTestRunner
- **Fast Task Processing**: Tests quick task completion
- **Slow Task Processing**: Tests long-running tasks
- **Multiple Tasks**: Tests concurrent task processing
- **Status Monitoring**: Verifies task status updates

### ReliableTestScenario (Advanced)
- **Fast Processing**: Quick task completion
- **Slow Processing**: Long-running tasks with timeout handling
- **Error-Prone Processing**: Tasks that may fail randomly
- **Batch Processing**: Multiple tasks in sequence
- **Duplicate Submission**: Tests idempotency
- **Concurrent Submission**: Tests parallel processing

## Custom Handlers

The test includes several custom handlers:

### FastHandler
- Processes tasks quickly (100ms)
- Demonstrates fast task completion

### SlowHandler
- Takes 5 seconds to complete
- Tests timeout and long-running task handling

### ErrorHandler
- Has 30% chance of failure
- Tests error handling and retry mechanisms

### BatchHandler
- Takes 2 seconds to complete
- Simulates batch processing scenarios

## Docker Services

### PostgreSQL
- **Image**: postgres:15
- **Port**: 5432
- **Database**: reliable_tasks
- **User**: postgres
- **Password**: password
- **Volume**: postgres_data

### Redis
- **Image**: redis:7-alpine
- **Port**: 6379
- **Volume**: redis_data
- **Persistence**: AOF enabled

## Configuration

### Database Configuration
The application automatically connects to:
- Host: localhost:5432
- Database: reliable_tasks
- Username: postgres
- Password: password

### Redis Configuration
The application automatically connects to:
- Host: localhost:6379
- No password (default)

## Monitoring

### Container Logs
```bash
# View all logs
docker-compose logs

# View specific service logs
docker-compose logs postgres
docker-compose logs redis
```

### Database Connection
```bash
# Connect to PostgreSQL
docker-compose exec postgres psql -U postgres -d reliable_tasks

# View jobs table
SELECT * FROM jobs ORDER BY created_at DESC;
```

### Redis Connection
```bash
# Connect to Redis
docker-compose exec redis redis-cli

# View queues
KEYS *
LLEN taskQueue
LLEN processingQueue
```

## Troubleshooting

### Common Issues

1. **Port conflicts**: If ports 5432 or 6379 are already in use:
   ```bash
   # Check what's using the ports
   lsof -i :5432
   lsof -i :6379
   
   # Stop conflicting services or change ports in docker-compose.yml
   ```

2. **Docker not running**:
   ```bash
   # Start Docker Desktop or Docker daemon
   sudo systemctl start docker  # Linux
   # Or start Docker Desktop on macOS/Windows
   ```

3. **Permission issues**:
   ```bash
   # Make script executable
   chmod +x run-test.sh
   ```

4. **Maven issues**:
   ```bash
   # Clean and rebuild
   mvn clean install
   ```

### Debug Mode

To run with more verbose logging:

```bash
# Set log level
export LOG_LEVEL=DEBUG

# Run test with debug output
mvn exec:java -Dexec.mainClass="org.example.reliable.SimpleTestRunner" -Dorg.slf4j.simpleLogger.defaultLogLevel=debug
```

## Performance Testing

For performance testing, you can modify the test scenarios:

```java
// Increase concurrent tasks
for (int i = 1; i <= 100; i++) {
    // Submit more tasks
}

// Increase processing time
Thread.sleep(10000); // 10 seconds
```

## Cleanup

To completely clean up:

```bash
# Stop and remove containers with volumes
docker-compose down -v

# Remove images (optional)
docker rmi postgres:15 redis:7-alpine

# Clean Maven
mvn clean
```
