package japi.accounting_processor.service;

import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import japi.accounting_processor.dto.TransactionEvent;
import japi.accounting_processor.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountingService {

    private final AccountRepository repository;
    private final StringRedisTemplate redisTemplate;

    @Bean
    public Consumer<TransactionEvent> processTransaction() {
        return event -> {
            repository.findById(event.userId()).ifPresentOrElse(
                account -> {
                    log.info("Processing in db to user: {}", event.userId());
                    // Postgres persitence

                    if ("DEPOSIT".equals(event.type())) {
                        account.setBalance(account.getBalance().add(event.amount()));
                    } else {
                        account.setBalance(account.getBalance().subtract(event.amount()));
                    }

                    repository.save(account);
                    log.info("New balance to {}: {}", event.userId(), account.getBalance());

                    //Redis syncronising
                    //Saving balance with 10 minutes expiration to dont fill RAM
                    String cacheKey = "balance:" + event.userId();
                    redisTemplate.opsForValue().set(cacheKey, account.getBalance().toString(), java.time.Duration.ofMinutes(10));

                    log.info("Redis cache updated! New balance in RAM: {}", account.getBalance());
                },
                () -> {
                    log.error("ERROR: User {} not found. Aborted transaction", event.userId());
                }
            );
        };
    }
}
