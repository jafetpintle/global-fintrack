package japi.accounting_processor.service;

import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import japi.accounting_processor.dto.TransactionEvent;
import japi.accounting_processor.entity.Account;
import japi.accounting_processor.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountingService {

    private final AccountRepository repository;

    @Bean
    public Consumer<TransactionEvent> processTransaction() {
        return event -> {
            log.info("Procesando transacción para el usuario: {}", event.userId());
            
            Account account = repository.findById(event.userId())
                .orElse(new Account(event.userId(), java.math.BigDecimal.ZERO, 0L));

            if ("DEPOSIT".equals(event.type())) {
                account.setBalance(account.getBalance().add(event.amount()));
            } else {
                account.setBalance(account.getBalance().subtract(event.amount()));
            }

            repository.save(account);
            log.info("Nuevo saldo para {}: {}", event.userId(), account.getBalance());
        };
    }
}
