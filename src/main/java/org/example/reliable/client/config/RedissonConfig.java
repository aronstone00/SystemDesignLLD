package org.example.reliable.client.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

@Slf4j
public class RedissonConfig {
    
    private static RedissonClient redissonClient;
    
    private static final String REDIS_HOST = "localhost";
    private static final int REDIS_PORT = 6379;
    private static final String REDIS_PASSWORD = null; // Set password if needed
    
    static {
        initializeRedissonClient();
    }
    
    private static void initializeRedissonClient() {
        try {
            Config config = new Config();
            
            // Configure single server
            config.useSingleServer()
                    .setAddress("redis://" + REDIS_HOST + ":" + REDIS_PORT)
                    .setConnectionPoolSize(64)
                    .setConnectionMinimumIdleSize(10)
                    .setConnectTimeout(10000)
                    .setTimeout(3000)
                    .setRetryAttempts(3)
                    .setRetryInterval(1500);
            
            // Set password if provided
            if (REDIS_PASSWORD != null && !REDIS_PASSWORD.isEmpty()) {
                config.useSingleServer().setPassword(REDIS_PASSWORD);
            }
            
            redissonClient = Redisson.create(config);
            log.info("Redisson client initialized successfully");
            
        } catch (Exception e) {
            log.error("Failed to initialize Redisson client", e);
            throw new RuntimeException("Redisson initialization failed", e);
        }
    }
    
    public static RedissonClient getRedissonClient() {
        return redissonClient;
    }
    
    public static void shutdown() {
        if (redissonClient != null && !redissonClient.isShutdown()) {
            redissonClient.shutdown();
            log.info("Redisson client shutdown successfully");
        }
    }
}
