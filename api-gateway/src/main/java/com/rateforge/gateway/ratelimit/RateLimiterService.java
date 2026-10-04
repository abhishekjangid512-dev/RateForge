package com.rateforge.gateway.ratelimit;

import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Service
public class RateLimiterService {

    private final ReactiveStringRedisTemplate redisTemplate;

    private static final DefaultRedisScript<Long> RATE_LIMIT_SCRIPT=
            new DefaultRedisScript<>(
                    """
                            local current=redis.call('INCR',KEYS[1])
                            
                            if current == 1 then
                               redis.call('EXPIRE',KEYS[1],ARGV[1])
                               end
                               return current
                            """,
                    Long.class
            );
    public RateLimiterService(ReactiveStringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Mono<RateLimitResult> checkLimit(String clientId,long limit,long windowSeconds){
        String key="rate-limit:"+clientId;

        return redisTemplate.
                execute(
                        RATE_LIMIT_SCRIPT,
                        Collections.singletonList(key),
                        String.valueOf(windowSeconds)
                )
                .next()
                .map(currentCount->{
                    boolean allowed=currentCount <= limit;

                    long remaining=Math.max(0,limit-currentCount);

                    return new RateLimitResult(allowed,currentCount,limit,remaining);
                });
    }


}
