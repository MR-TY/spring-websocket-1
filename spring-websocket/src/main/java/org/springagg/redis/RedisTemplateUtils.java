package org.springagg.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author tangyu
 * @date 2020/8/2
 */
@Service
public class RedisTemplateUtils {

    @Autowired
    RedisTemplate<String, String> redisTemplate;


    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire( key, time, TimeUnit.SECONDS );
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire( key, TimeUnit.SECONDS );
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey( key );
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get( key );
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key, String value) {
        try {
            redisTemplate.opsForValue().set( key, value );
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, String value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set( key, value, time, TimeUnit.SECONDS );
            } else {
                set( key, value );
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 递增
     *
     * @param key 键
     * @return
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException( "递增因子必须大于0" );
        }
        return redisTemplate.opsForValue().increment( key, delta );
    }

    /**
     * 递减
     *
     * @param key 键
     * @return
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException( "递减因子必须大于0" );
        }
        return redisTemplate.opsForValue().increment( key, -delta );
    }

    // ================================Map=================================

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hget(String key, String item) {
        return redisTemplate.opsForHash().get( key, item );
    }

    public void remove(String key) {
        redisTemplate.delete( key );
    }

    public void setValue(String key, String value) {
        SetOperations<String, String> set = redisTemplate.opsForSet();
        set.add( key, value );
    }

    public Set<String> getValue(String key) {
        SetOperations<String, String> set = redisTemplate.opsForSet();
        Set<String> values = set.members( key );
        return values;
    }

    public void mapValue(String user, Map map) {
        HashOperations<String, String, String> listOperations = redisTemplate.opsForHash();
        listOperations.putAll( user, map );
    }

    public Map<String, String> getmapValue(String key) {
        HashOperations<String, String, String> listOperations = redisTemplate.opsForHash();
        return listOperations.entries(key);
    }

    public void deletemapValue(String key, String key1) {
        HashOperations<String, String, String> listOperations = redisTemplate.opsForHash();
        listOperations.delete( key,key1 );
    }
}
