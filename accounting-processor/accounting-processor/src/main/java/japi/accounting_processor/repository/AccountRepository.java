package japi.accounting_processor.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import japi.accounting_processor.entity.Account;

public interface AccountRepository extends JpaRepository<Account, UUID>{

}
