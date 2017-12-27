package server.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import server.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    Optional<List<Item>> getAllByWishlistIdAndActiveIsTrue(int wishlist_id);

    Optional<Item> findItemById(int id);

    @Query("SELECT i FROM Item i, server.model.Wishlist w, server.model.Account a WHERE " +
            "i.id = :itemId AND " +
            "i.wishlist.id = w.id AND " +
            "w.id = :wId AND " +
            "w.account.id = a.id AND " +
            "a.id = :aId AND " +
            "w.active = TRUE AND " +
            "i.active = TRUE ")
    Optional<Item> findByIdAndWishlistIdAnAndAccountId(@Param("itemId") int itemId,
                                                       @Param("wId") int wishlistId,
                                                       @Param("aId") int accountId);

    @Query(value = "SELECT i.* FROM Item i " +
            "  JOIN wishlist ON i.wishlist_id = wishlist.id " +
            "  JOIN account ON wishlist.account_id = account.id " +
            "WHERE wishlist.id = :wishlistId AND " +
            "account.id = :userId AND " +
            "i.active = TRUE AND " +
            "wishlist.active = TRUE " +
            "ORDER BY i.item_order", nativeQuery = true)
    Optional<List<Item>> getAll(@Param("userId") int userId,
                                @Param("wishlistId") int wishlistId);

    int countAllByWishlist_IdAndActiveIsTrue(int wishlist_id);

    @Transactional
    @Modifying
    @Query("UPDATE Item i SET state = 1 WHERE i.id = :itemId")
    void setTakenByItemId(@Param("itemId") int itemId);

    Optional<Item> findById(int itemId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE item " +
            "SET active = FALSE " +
            "WHERE item.id = :itemId AND " +
            "      item.wishlist_id = :wishlistId AND " +
            "      EXISTS(SELECT wishlist " +
            "             FROM wishlist " +
            "               JOIN account " +
            "                 ON wishlist.account_id = account.id " +
            "             WHERE account_id = :userId)", nativeQuery = true)
    void setActiveFalse(@Param("userId") int userId,
                        @Param("wishlistId") int wishlistId,
                        @Param("itemId") int itemId);

    @Query(value = "SELECT i.* FROM Item i " +
            "  JOIN wishlist ON i.wishlist_id = wishlist.id " +
            "  JOIN account ON wishlist.account_id = account.id " +
            "WHERE wishlist.id = :wishlistId AND " +
            "account.id = :userId AND " +
            "i.active = TRUE AND " +
            "wishlist.active = TRUE AND " +
            "i.id = :itemId", nativeQuery = true)
    Optional<Item> findByIdAndUserAndWishlist(@Param("userId") int userId,
                                              @Param("wishlistId") int wishlistId,
                                              @Param("itemId") int itemId);
}
