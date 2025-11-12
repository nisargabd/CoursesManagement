package com.sanketika.course_backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    private static final Logger log = LoggerFactory.getLogger(RedisConfig.class);

    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    /**
     * ✅ Create Lettuce-based Redis connection.
     * Doesn't fail startup if Redis is offline.
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        String host = redisHost;
        int port = redisPort;

        LettuceConnectionFactory factory = new LettuceConnectionFactory(host, port);
        factory.setValidateConnection(false); // avoid blocking startup
        try {
            factory.afterPropertiesSet();
            log.info("✅ Connected to Redis at {}:{}", host, port);

            // Try ping once to confirm connectivity
            try (RedisConnection conn = factory.getConnection()) {
                String pong = conn.ping();
                log.info("✅ Redis responded to PING: {}", pong);
            }
        } catch (Exception e) {
            log.warn("⚠️ Redis not reachable at {}:{} — continuing without cache. {}", host, port, e.getMessage());
        }

        return factory;
    }

    /**
     * ✅ RedisTemplate with clean JSON serialization.
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        log.info("✅ RedisTemplate configured with JSON serializer");
        return template;
    }

    /**
     * ✅ Configure cache manager with TTL and resilience.
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10)) // cache TTL: 10 min
                .disableCachingNullValues();

        log.info("✅ RedisCacheManager configured with 10 min TTL");
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(cacheConfig)
                .build();
    }

    /**
     * ✅ Custom error handler — prevents Redis errors
     * from interrupting API responses.
     */
    @Bean
    public SimpleCacheErrorHandler cacheErrorHandler() {
        return new SimpleCacheErrorHandler() {
            private final Logger redisLogger = LoggerFactory.getLogger("RedisCacheErrorHandler");

            @Override
            public void handleCacheGetError(RuntimeException exception,
                                            org.springframework.cache.Cache cache, Object key) {
                redisLogger.warn("⚠️ Redis GET error for key '{}': {}", key, exception.getMessage());
            }

            @Override
            public void handleCachePutError(RuntimeException exception,
                                            org.springframework.cache.Cache cache, Object key, Object value) {
                redisLogger.warn("⚠️ Redis PUT error for key '{}': {}", key, exception.getMessage());
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception,
                                              org.springframework.cache.Cache cache, Object key) {
                redisLogger.warn("⚠️ Redis EVICT error for key '{}': {}", key, exception.getMessage());
            }

            @Override
            public void handleCacheClearError(RuntimeException exception,
                                              org.springframework.cache.Cache cache) {
                redisLogger.warn("⚠️ Redis CLEAR error: {}", exception.getMessage());
            }
        };
    }
}
