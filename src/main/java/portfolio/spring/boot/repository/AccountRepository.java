package portfolio.spring.boot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import portfolio.spring.boot.model.Account;

public interface AccountRepository extends JpaRepository<Account, String> {

}