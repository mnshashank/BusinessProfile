package com.intuit.businessprofile.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@RequiredArgsConstructor
@Configuration
public class RedisConfiguration {

    @Bean
    public JedisPool getJedisConnection() {
        return new JedisPool(new JedisPoolConfig(), "localhost", Integer.parseInt("6379"));
    }
}
