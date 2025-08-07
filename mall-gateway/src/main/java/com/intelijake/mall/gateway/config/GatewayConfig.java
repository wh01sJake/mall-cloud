package com.intelijake.mall.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Gateway configuration class for rate limiting and other gateway features
 */
@Configuration
public class GatewayConfig {

    /**
     * API key resolver for rate limiting
     * Uses the request path as the key for rate limiting
     * This means rate limits are applied per endpoint path
     * 
     * @return KeyResolver that resolves to the request path
     */
    @Bean
    public KeyResolver apiKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getPath().value());
    }
    

    /**
     * Custom rate limiter for statistics endpoints
     * Configures a more restrictive rate limit for statistics endpoints
     * 
     * @return RedisRateLimiter with custom configuration
     */
    @Bean
    public RedisRateLimiter statisticsRateLimiter(ReactiveRedisTemplate<String, String> redisTemplate,
                                                 RedisScript<List<Long>> script) {
        // 5 requests per second with burst capacity of 10
        return new RedisRateLimiter(5, 10);
    }
}