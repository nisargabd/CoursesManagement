package com.sanketika.course_backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
// import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    private static final Logger log = LoggerFactory.getLogger(RedisConfig.class);

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {

        if (isRedisAvailable(redisConnectionFactory)) {
            log.info("✅ Redis is UP — using RedisCacheManager");

            RedisCacheConfiguration config = RedisCacheConfiguration
                    .defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(10))
                    .disableCachingNullValues()
                    .serializeValuesWith(
                            RedisSerializationContext.SerializationPair.fromSerializer(
                                    new JdkSerializationRedisSerializer()
                            )
                    );

            return RedisCacheManager.builder(redisConnectionFactory)
                    .cacheDefaults(config)
                    .build();
        }

        log.warn("⚠ Redis is DOWN — using in-memory ConcurrentMapCacheManager");
        return new ConcurrentMapCacheManager("allCourses", "liveCourses", "courses");
    }

    private boolean isRedisAvailable(RedisConnectionFactory redisConnectionFactory) {
        try {
            var connection = redisConnectionFactory.getConnection();
            connection.getConfig("*"); // simple, fast check
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
