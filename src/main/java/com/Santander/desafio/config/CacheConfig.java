package com.Santander.desafio.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching // Habilita o sistema de cache do Spring
public class CacheConfig {

    private final RedisConnectionFactory redisConnectionFactory;

    // Injeta a fábrica de conexão Redis, configurada pelo Spring Boot automaticamente
    public CacheConfig(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Bean
    public CacheManager cacheManager() {
        
        // Configuração Padrão do Cache:
        // 1. Define o TTL de 5 minutos.
        // 2. Não armazena valores nulos.
        // 3. Usa JSON para serialização dos valores (objetos)
        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5)) // TTL de 5 minutos (Requisito)
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        // Cria o gerenciador de cache usando a configuração e a fábrica de conexão
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(cacheConfig)
                // Opcional: Aqui você pode criar caches específicos com TTLs diferentes, se necessário.
                .withCacheConfiguration("distancias", cacheConfig) // Usa o nome "distancias" do Service
                .build();
    }
}
