package server.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import server.model.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findAccountByIdAndRegisteredIsTrue(int id);

    @Query(value = "SELECT exists(SELECT * " +
            "              FROM account_friends " +
            "              WHERE account_id = :requestedId AND friends_id = :id)", nativeQuery = true)
    boolean isFriend(@Param("id") int id, @Param("requestedId") int requestedId);

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

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM account_friends " +
            "WHERE (account_id = :accountId AND friends_id = :friendId) OR " +
            "(account_id = :friendId AND friends_id = :accountId)", nativeQuery = true)
    void removeFriendRelation(@Param("accountId") int account,
                              @Param("friendId") int exFriend);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO account_friends " +
            "VALUES (:accountId, :friendId), (:friendId, :accountId)", nativeQuery = true)
    void addFriendRelation(@Param("accountId") int accountId,
                           @Param("friendId") int friendId);
}

