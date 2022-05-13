package com.intuit.businessprofile.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

@ExtendWith({ MockitoExtension.class })
class JedisTemplateTest {

    private static final String JEDIS_KEY = "key";
    private static final String JEDIS_VALUE = "value";

    @Mock
    private JedisPool jedisPool;

    @Mock
    private Jedis jedis;

    @InjectMocks
    private JedisTemplate jedisTemplate;

    @BeforeEach
    public void setup() {
        Mockito.when(jedisPool.getResource())
                .thenReturn(jedis);
    }

    @Test
    void delSuccessTest() {
        String[] delKeys = { JEDIS_KEY };
        jedisTemplate.del(delKeys);

        Mockito.verify(jedis, Mockito.times(1))
                .del(delKeys);
    }

    @Test
    void getIfExistsSuccessTest() {
        Mockito.when(jedis.exists(JEDIS_KEY))
                .thenReturn(true);
        Mockito.when(jedis.get(JEDIS_KEY))
                .thenReturn(JEDIS_VALUE);

        Assertions.assertEquals(JEDIS_VALUE, jedisTemplate.getIfExists(JEDIS_KEY)
                .get());
    }

    @Test
    void getIfExistsNegativeTest() {
        Mockito.when(jedis.exists(JEDIS_KEY))
                .thenReturn(false);

        Assertions.assertFalse(jedisTemplate.getIfExists(JEDIS_KEY)
                .isPresent());
    }

    @Test
    void setWithTTLTest() {
        jedisTemplate.set(JEDIS_KEY, JEDIS_VALUE, 21L);

        ArgumentCaptor<SetParams> paramsCaptor = ArgumentCaptor.forClass(SetParams.class);
        Mockito.verify(jedis, Mockito.times(1))
                .set(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), paramsCaptor.capture());

        Assertions.assertEquals(21L, (long) paramsCaptor.getValue()
                .getParam("ex"));
    }

    @Test
    void setUnlimitedTTLTest() {
        jedisTemplate.set(JEDIS_KEY, JEDIS_VALUE, 0);

        Mockito.verify(jedis, Mockito.times(1))
                .set(JEDIS_KEY, JEDIS_VALUE);
    }

}
