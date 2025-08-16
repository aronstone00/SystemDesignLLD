-- Initialize the reliable_tasks database
CREATE DATABASE reliable_tasks;

-- Connect to the reliable_tasks database
\c reliable_tasks;

-- Create the jobs table
CREATE TABLE IF NOT EXISTS jobs (
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

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_jobs_reference_id ON jobs(reference_id);
CREATE INDEX IF NOT EXISTS idx_jobs_status ON jobs(status);
CREATE INDEX IF NOT EXISTS idx_jobs_created_at ON jobs(created_at);

-- Grant permissions
GRANT ALL PRIVILEGES ON DATABASE reliable_tasks TO postgres;
GRANT ALL PRIVILEGES ON TABLE jobs TO postgres;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO postgres;

-- Log the initialization
SELECT 'Database reliable_tasks initialized successfully' as status;
