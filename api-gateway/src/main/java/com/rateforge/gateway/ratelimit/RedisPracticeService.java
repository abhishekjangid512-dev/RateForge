package com.rateforge.gateway.ratelimit;

import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class RedisPracticeService {

    private final ReactiveStringRedisTemplate redisTemplate;

    public RedisPracticeService(ReactiveStringRedisTemplate redisTemplate){
        this.redisTemplate=redisTemplate;
    }

    public Mono<Long> increment(String clientId){
        String key="test:rate:"+clientId;
        return redisTemplate.opsForValue().increment(key)
                .flatMap(count->{
                    if (count==1){
                        return redisTemplate.expire(key, Duration.ofSeconds(60)).thenReturn(count);
                    }
                    return Mono.just(count);
                });
    }
}
