package com.sanketika.course_backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
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

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {

        // 1️⃣ Check if Redis is actually reachable (PING)
        boolean redisAvailable = false;
        try {
            redisConnectionFactory.getConnection().ping();
            redisAvailable = true;
        } catch (Exception e) {
            log.warn("⚠ Redis DOWN. Falling back to in-memory cache.");
        }

        // 2️⃣ If Redis is UP → use RedisCacheManager
        if (redisAvailable) {
            log.info("✅ Using Redis cache manager");
            RedisCacheConfiguration config = RedisCacheConfiguration
                    .defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(10))
                    .disableCachingNullValues();

            return RedisCacheManager
                    .builder(redisConnectionFactory)
                    .cacheDefaults(config)
                    .build();
        }

        // 3️⃣ If Redis DOWN → use in-memory ConcurrentMapCacheManager
        log.warn("⚠ Using fallback in-memory cache (ConcurrentMapCacheManager)");
        return new ConcurrentMapCacheManager(
                "allCourses",
                "liveCourses",
                "courses"
        );
    }
}
