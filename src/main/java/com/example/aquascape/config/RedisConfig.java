package com.example.aquascape.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    private ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, 
                ObjectMapper.DefaultTyping.NON_FINAL, 
                JsonTypeInfo.As.PROPERTY);
        return mapper;
    }

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(createObjectMapper());
        
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(12))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(createObjectMapper());

        RedisCacheConfiguration catalogCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(24))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

        RedisCacheConfiguration layoutCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(12))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

        RedisCacheConfiguration userTanksCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

        return RedisCacheManager.builder(connectionFactory)
                .withCacheConfiguration("catalog", catalogCacheConfig)
                .withCacheConfiguration("tank_layout", layoutCacheConfig)
                .withCacheConfiguration("user_tanks", userTanksCacheConfig)
                .build();
    }
}
