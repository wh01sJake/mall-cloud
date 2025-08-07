package com.intelijake.mall.admin.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Value("${REDIS_URL:redis://127.0.0.1:6379}")
    private String redisUrl;

    @Bean
    @ConditionalOnProperty(name = "redisson.enabled", havingValue = "true", matchIfMissing = false)
    public RedissonClient redissonClient(){
        Config config = new Config();

        // Handle Heroku Redis SSL connection
        if (redisUrl.startsWith("rediss://")) {
            // For SSL connections, use the rediss:// URL directly
            config.useSingleServer()
                .setAddress(redisUrl)
                .setSslEnableEndpointIdentification(false);
        } else {
            config.useSingleServer().setAddress(redisUrl);
        }

        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}