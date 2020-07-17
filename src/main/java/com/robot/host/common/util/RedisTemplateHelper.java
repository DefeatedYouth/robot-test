package com.robot.host.common.util;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author exrick
 */
@Component("redisTemplateHelper")
public class RedisTemplateHelper {

    @Autowired
    private StringRedisTemplate redisTemplate;
    public final static long NOT_EXPIRE = -1;

    /**
     * scan 实现
     *
     * @param pattern  表达式
     * @param consumer 对迭代到的key进行操作
     */
    private void scan(String pattern, Consumer<byte[]> consumer) {

        redisTemplate.execute((RedisConnection connection) -> {
            try (Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().count(Long.MAX_VALUE).match(pattern).build())) {
                cursor.forEachRemaining(consumer);
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
    }

    public <T> T get(String prefix, String key, Class<T> clazz, long expire) {
        String value = redisTemplate.opsForValue().get(prefix + key);
        if (expire != NOT_EXPIRE) {
            redisTemplate.expire(prefix + key, expire, TimeUnit.SECONDS);
        }
        return value == null ? null : fromJson(value, clazz);
    }

    public <T> T get(String prefix, String key, Class<T> clazz) {
        return get(prefix, key, clazz, NOT_EXPIRE);
    }

    public String getset(String prefix, String key, String value) {
        return redisTemplate.opsForValue().getAndSet(prefix + key, value);
    }

    public String get(String prefix, String key, long expire) {
        String value = redisTemplate.opsForValue().get(prefix + key);
        if (expire != NOT_EXPIRE) {
            redisTemplate.expire(prefix + key, expire, TimeUnit.SECONDS);
        }
        return value;
    }

    public String get(String prefix, String key) {
        return get(prefix, key, NOT_EXPIRE);
    }

    public Long increment(String prefix, String key) {
        return redisTemplate.opsForValue().increment(prefix + key);
    }

    /**
     * 获取符合条件的key
     *
     * @param pattern 表达式
     * @return
     */
    public Set<String> keys(String pattern) {
        Set<String> keys = new HashSet<>();
        this.scan(pattern, item -> {
            // 符合条件的key
            String key = new String(item, StandardCharsets.UTF_8);
            keys.add(key);
        });
        return keys;
    }

    public void set(String prefix, String key, Object value, long expire) {
        redisTemplate.opsForValue().set(prefix + key, toJson(value));
        if (expire != NOT_EXPIRE) {
            redisTemplate.expire(prefix + key, expire, TimeUnit.SECONDS);
        }
    }

    public void increment(String prefix, String key, long num) {
        redisTemplate.opsForValue().increment(prefix + key, num);
    }

    public void increment(String prefix, String key, double num) {
        redisTemplate.opsForValue().increment(prefix + key, num);
    }

    public void setEx(String prefix, String key, long expire) {
        redisTemplate.expire(prefix + key, expire, TimeUnit.SECONDS);
    }

    public void set(String prefix, String key, Object value) {
        set(prefix, key, value, NOT_EXPIRE);
    }

    /**
     * Object转成JSON数据
     */
    private String toJson(Object object) {
        if (object instanceof Integer || object instanceof Long || object instanceof Float ||
                object instanceof Double || object instanceof Boolean || object instanceof String) {
            return String.valueOf(object);
        }
        return JSON.toJSONString(object);
    }

    /**
     * JSON数据，转成Object
     */
    private <T> T fromJson(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    public Object hget(String prefix, String key, String hashKey) {
        return redisTemplate.opsForHash().get(prefix + key, hashKey);
    }

    public void hset(String prefix, String key, String hashKey, String hVal) {
        redisTemplate.opsForHash().put(prefix + key, hashKey, hVal);
    }

    public long sset(String prefix, String key, String... val) {
        return redisTemplate.opsForSet().add(prefix + key, val);
    }

    public long srem(String prefix, String key, String val) {
        return redisTemplate.opsForSet().remove(prefix + key, val);
    }
    public long ssize(String prefix, String key) {
        return redisTemplate.opsForSet().size(prefix + key);
    }
    public Set<String> smember(String prefix, String key) {
      return   redisTemplate.opsForSet().members(prefix + key);
    }

    public long hincrease(String prefix, String key, String hashKey) {
        return redisTemplate.opsForHash().increment(prefix + key, hashKey, 1);
    }
    public long hincrease(String prefix, String key, String hashKey,int size) {
        return redisTemplate.opsForHash().increment(prefix + key, hashKey, size);
    }
}
