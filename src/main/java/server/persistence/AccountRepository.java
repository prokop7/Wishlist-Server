package server.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import server.model.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findAccountById(int id);

    @Override
    List<Account> findAll();

    Account getAccountByVkId(int vkId);

    @Query("SELECT a1 FROM Account a2 join a2.friends a1 " +
            "WHERE " +
            "a1.registered = TRUE and " +
            "a2.id = :accountId")
    List<Account> getRegisteredFriends(@Param("accountId") int accountId);


    @Query("SELECT a1 FROM Account a2 join a2.friends a1 " +
            "WHERE a2.id = :accountId")
    List<Account> getAllFriends(@Param("accountId") int accountId);
}

