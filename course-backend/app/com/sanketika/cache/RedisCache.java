package com.sanketika.cache;

import com.typesafe.config.Config;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Optional;

@Singleton
public class RedisCache {

    private final JedisPool jedisPool;

    @Inject
    public RedisCache(Config config) {
        String host = config.getString("redis.host");
        int port = config.getInt("redis.port");
        this.jedisPool = new JedisPool(new JedisPoolConfig(), host, port);
    }

    public void set(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, value);
        }
    }

    public void set(String key, String value, int ttlSeconds) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(key, ttlSeconds, value);
        }
    }

    public Optional<String> get(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return Optional.ofNullable(jedis.get(key));
        }
    }

    public void del(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        }
    }
}
