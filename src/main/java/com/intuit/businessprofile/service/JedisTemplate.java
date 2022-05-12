package com.intuit.businessprofile.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

@Service
@RequiredArgsConstructor
@Slf4j
public class JedisTemplate {

    private final JedisPool jedisPool;

    public void del(String... keys) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(keys);
        } catch (Exception ex) {
            log.error("Exception while deleting the key from redis", ex);
        }
    }

    /**
     * Adds the key:value pair in redis with mentioned TTL.
     *
     * @param key             : redis key to be set
     * @param value           : value for the key
     * @param secondsToExpire : TTL in seconds. Pass "0" if no expiry needs to be set.
     */
    public void set(String key, String value, long secondsToExpire) {
        try (Jedis jedis = jedisPool.getResource()) {
            if (secondsToExpire == 0) {
                jedis.set(key, value);
            } else {
                // setting TTL sent by the caller.
                SetParams setParams = new SetParams();
                setParams.ex(secondsToExpire); // expiry set in seconds.

                jedis.set(key, value, setParams);
            }
        } catch (Exception ex) {
            log.error("Exception while adding the key:value into redis", ex);
        }
    }

    public Optional<String> getIfExists(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            if (jedis.exists(key)) {
                return Optional.of(jedis.get(key));
            }
            return Optional.empty();
        } catch (Exception ex) {
            log.error("Exception while getIfExists from redis", ex);
        }
        return Optional.empty();
    }
    
}
