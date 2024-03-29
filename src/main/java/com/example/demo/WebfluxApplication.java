package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;
import org.springframework.web.server.session.HeaderWebSessionIdResolver;
import org.springframework.web.server.session.WebSessionIdResolver;

@SpringBootApplication
@EnableRedisWebSession
//@EnableDiscoveryClient
public class WebfluxApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebfluxApplication.class, args);
	}
	
	@Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }

    @Bean
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        RedisSerializationContext<String, Object> serializationContext =
            RedisSerializationContext.<String, Object>newSerializationContext(new StringRedisSerializer())
                                     .hashKey(new StringRedisSerializer())
                                     .hashValue(new GenericJackson2JsonRedisSerializer())
                                     .build();

        return new ReactiveRedisTemplate<>(factory, serializationContext);
    }

    @Bean
    public WebSessionIdResolver webSessionIdResolver() {
    	// header에있는 X-AUTH-TOKEN 값으로 session id check (redis)
        HeaderWebSessionIdResolver sessionIdResolver = new HeaderWebSessionIdResolver();
        sessionIdResolver.setHeaderName("X-AUTH-TOKEN");
        return sessionIdResolver;
    }
}
