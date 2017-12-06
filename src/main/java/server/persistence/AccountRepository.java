package server.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import server.model.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findAccountById(int id);

    @Override
    List<Account> findAll();

    Account getAccountByVkId(int vkId);
}
