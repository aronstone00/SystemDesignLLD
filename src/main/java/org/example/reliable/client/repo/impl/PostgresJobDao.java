package org.example.reliable.client.repo.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.reliable.client.repo.JobDao;
import org.example.reliable.client.repo.config.DatabaseConfig;
import org.example.reliable.client.repo.entity.JobEntity;
import org.example.reliable.model.HandlerBaseResponse;
import org.example.reliable.util.HandlerSerializationUtil;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

import static org.example.reliable.util.HandlerSerializationUtil.deserializeResponse;

@Slf4j
public class PostgresJobDao implements JobDao {
    
    private final DataSource dataSource;
    private final ObjectMapper objectMapper;
    
    public PostgresJobDao() {
        this.dataSource = DatabaseConfig.getDataSource();
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public Optional<JobEntity> fetchJobsByRefId(String refId) {
        String sql = "SELECT * FROM jobs WHERE reference_id = ?";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, refId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToJobEntity(resultSet));
                }
            }
        } catch (SQLException e) {
            log.error("Error fetching job by refId: {}", refId, e);
        }
        
        return Optional.empty();
    }
    
    @Override
    public void persist(JobEntity jobEntity) {
        String sql = """
            INSERT INTO jobs (job_id, status, payload, payload_hash, reference_id, 
                             handler_name, response, error, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, jobEntity.getJobId());
            statement.setString(2, jobEntity.getStatus());
            statement.setString(3, jobEntity.getPayload());
            statement.setString(4, jobEntity.getPayloadHash());
            statement.setString(5, jobEntity.getReferenceId());
            statement.setString(6, jobEntity.getHandlerName());
            statement.setString(7, HandlerSerializationUtil.serialize(jobEntity.getResponse()));
            statement.setString(8, jobEntity.getError());
            statement.setLong(9, jobEntity.getCreatedAt());
            statement.setLong(10, jobEntity.getUpdatedAt());
            
            statement.executeUpdate();
            log.debug("Job persisted successfully: {}", jobEntity.getJobId());
            
        } catch (SQLException e) {
            log.error("Error persisting job: {}", jobEntity.getJobId(), e);
            throw new RuntimeException("Failed to persist job", e);
        }
    }
    
    @Override
    public JobEntity fetchByJobId(String jobId) {
        String sql = "SELECT * FROM jobs WHERE job_id = ?";
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, jobId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToJobEntity(resultSet);
                }
            }
        } catch (SQLException e) {
            log.error("Error fetching job by jobId: {}", jobId, e);
        }
        
        return null;
    }
    
    @Override
    public void update(JobEntity jobEntity) {
        String sql = """
            UPDATE jobs SET status = ?, payload = ?, payload_hash = ?, reference_id = ?,
                           handler_name = ?, response = ?, error = ?, updated_at = ?
            WHERE job_id = ?
            """;
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, jobEntity.getStatus());
            statement.setString(2, jobEntity.getPayload());
            statement.setString(3, jobEntity.getPayloadHash());
            statement.setString(4, jobEntity.getReferenceId());
            statement.setString(5, jobEntity.getHandlerName());
            statement.setString(6, HandlerSerializationUtil.serialize(jobEntity.getResponse()));
            statement.setString(7, jobEntity.getError());
            statement.setLong(8, jobEntity.getUpdatedAt());
            statement.setString(9, jobEntity.getJobId());
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                log.warn("No job found to update with jobId: {}", jobEntity.getJobId());
            } else {
                log.debug("Job updated successfully: {}", jobEntity.getJobId());
            }
            
        } catch (SQLException e) {
            log.error("Error updating job: {}", jobEntity.getJobId(), e);
            throw new RuntimeException("Failed to update job", e);
        }
    }

    private JobEntity mapResultSetToJobEntity(ResultSet resultSet) throws SQLException {
        return JobEntity.builder()
                .jobId(resultSet.getString("job_id"))
                .status(resultSet.getString("status"))
                .payload(resultSet.getString("payload"))
                .payloadHash(resultSet.getString("payload_hash"))
                .referenceId(resultSet.getString("reference_id"))
                .handlerName(resultSet.getString("handler_name"))
                .response(HandlerSerializationUtil.deserializeResponse(resultSet.getString("response")))
                .error(resultSet.getString("error"))
                .createdAt(resultSet.getLong("created_at"))
                .updatedAt(resultSet.getLong("updated_at"))
                .build();
    }
    

}
