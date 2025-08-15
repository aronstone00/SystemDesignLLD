# Reliable Task System - PostgreSQL & Redis Implementation

This project implements a reliable task system with PostgreSQL as the database backend and Redis for distributed task queues.

## Database Setup

### 1. Install PostgreSQL
Make sure PostgreSQL is installed and running on your system.

### 2. Create Database
```sql
CREATE DATABASE reliable_tasks;
```

### 3. Update Database Configuration
Edit the database connection details in `src/main/java/org/example/reliable/client/repo/config/DatabaseConfig.java`:

```java
private static final String DB_URL = "jdbc:postgresql://localhost:5432/reliable_tasks";
private static final String DB_USER = "your_username";
private static final String DB_PASSWORD = "your_password";
```

## Redis Setup

### 1. Install Redis
Make sure Redis is installed and running on your system.

### 2. Update Redis Configuration
Edit the Redis connection details in `src/main/java/org/example/reliable/client/config/RedissonConfig.java`:

```java
private static final String REDIS_HOST = "localhost";
private static final int REDIS_PORT = 6379;
private static final String REDIS_PASSWORD = null; // Set password if needed
```

## Project Structure

- `JobDao` - Interface defining database operations
- `PostgresJobDao` - PostgreSQL implementation of the DAO
- `DatabaseConfig` - Database connection and table setup
- `JobEntity` - Entity class representing job data
- `RedissonTaskManager` - Redis-based task queue implementation
- `RedissonConfig` - Redis connection configuration
- `ProcessingQueuePayload` - Payload for processing queue items

## Features

- **Connection Pooling**: Uses HikariCP for efficient database connections
- **Automatic Table Creation**: Creates the `jobs` table if it doesn't exist
- **JSON Serialization**: Handles complex objects like `HandlerBaseResponse`
- **Error Handling**: Comprehensive error handling and logging
- **Prepared Statements**: SQL injection protection
- **Distributed Task Queues**: Uses Redis with Redisson for scalable task processing
- **Task Expiry Management**: Automatic cleanup of expired tasks
- **Concurrent Processing**: Multi-threaded task execution with configurable parallelism
- **Type-Safe Request Handling**: Custom serialization/deserialization for `HandlerBaseRequest`
- **Extensible Handler System**: Easy to add new request types and handlers

## Usage Example

```java
JobDao jobDao = new PostgresJobDao();

// Create a job
JobEntity job = JobEntity.builder()
    .jobId("job-123")
    .status("PENDING")
    .payload("{\"data\": \"sample\"}")
    .referenceId("ref-456")
    .createdAt(System.currentTimeMillis())
    .updatedAt(System.currentTimeMillis())
    .build();

// Persist job
jobDao.persist(job);

// Fetch by job ID
JobEntity fetched = jobDao.fetchByJobId("job-123");

// Fetch by reference ID
Optional<JobEntity> byRef = jobDao.fetchJobsByRefId("ref-456");

// Update job
job.setStatus("COMPLETED");
jobDao.update(job);
```

## Dependencies

- PostgreSQL JDBC Driver (42.7.2)
- HikariCP Connection Pool (5.1.0)
- Redisson for Redis (3.27.1)
- Jackson for JSON serialization
- Lombok for boilerplate reduction
- SLF4J for logging

## Running the Example

1. Ensure PostgreSQL is running
2. Ensure Redis is running
3. Update database credentials in `DatabaseConfig`
4. Update Redis configuration in `RedissonConfig`
5. Run `JobDaoExample.main()` to see the database implementation in action
6. Run `SerializationExample.main()` to see the request serialization system in action

## Database Schema

The `jobs` table has the following structure:

```sql
CREATE TABLE jobs (
    job_id VARCHAR(255) PRIMARY KEY,
    status VARCHAR(50) NOT NULL,
    payload TEXT,
    payload_hash VARCHAR(255),
    reference_id VARCHAR(255),
    handler_name VARCHAR(255),
    response TEXT,
    error TEXT,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL
);
```

## Request Serialization System

The system includes a custom serialization/deserialization system for `HandlerBaseRequest` objects:

### Key Components

- **HandlerSerializationUtil**: Main utility class for serialization/deserialization
- **Request Type Registry**: Automatically registers request types for deserialization
- **Type-Safe Handling**: Ensures proper type information is preserved

### Creating Custom Request Types

1. **Extend HandlerBaseRequest**:
```java
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class MyCustomRequest extends HandlerBaseRequest {
    private String data;
    private int priority;
}
```

2. **Register the Type** (in your handler's static block):
```java
static {
    HandlerSerializationUtil.registerRequestType("MyCustomRequest", MyCustomRequest.class);
}
```

3. **Use in Handler**:
```java
@Override
public HandlerBaseResponse handle(HandlerBaseRequest request) {
    if (request instanceof MyCustomRequest) {
        MyCustomRequest customRequest = (MyCustomRequest) request;
        // Process the request
        return new MyResponse(true, "Processed");
    }
    throw new IllegalArgumentException("Unsupported request type");
}
```

### Serialization Format

The system uses a wrapper format to preserve type information:

```json
{
  "type": "SampleHandlerRequest",
  "data": "{\"message\":\"Hello World\",\"priority\":5,\"urgent\":true}"
}
```
