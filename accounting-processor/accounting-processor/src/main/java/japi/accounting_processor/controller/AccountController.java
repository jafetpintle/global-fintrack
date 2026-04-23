package japi.accounting_processor.controller;

import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import japi.accounting_processor.repository.AccountRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final StringRedisTemplate redisTemplate;
    private final AccountRepository repository;

    @GetMapping("/{userId}/balance")
    public ResponseEntity<String> getBalance(@PathVariable UUID userId) {
        String cacheKey = "balance" + userId;

        //Triying to get balance from REDIS
        String cachedBalance = redisTemplate.opsForValue().get(cacheKey);

        if(cachedBalance != null){
            return ResponseEntity.ok("Balance from REDIS: (Cache HIT): "+ cachedBalance);
        }

        //if dont exist on redis, go to postgres
        return repository.findById(userId)
            .map(acc -> {
                redisTemplate.opsForValue().set(cacheKey, acc.getBalance().toString(), java.time.Duration.ofMinutes(10));
                return ResponseEntity.ok("Balance from POSTGRES (Cache Miss): " + acc.getBalance());
            })
            .orElse(ResponseEntity.notFound().build());
    }
    

}
