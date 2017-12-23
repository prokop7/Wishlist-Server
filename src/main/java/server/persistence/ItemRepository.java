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
    Optional<List<Item>> getAllByWishlistId(int wishlist_id);

    Optional<Item> findItemById(int id);

    @Query("SELECT i FROM Item i, server.model.Wishlist w, server.model.Account a WHERE " +
            "i.id = :itemId AND " +
            "i.wishlist.id = w.id AND " +
            "w.id = :wId AND " +
            "w.account.id = a.id AND " +
            "a.id = :aId")
    Optional<Item> findByIdAndWishlistIdAnAndAccountId(@Param("itemId") int itemId,
                        @Param("wId") int wId,
                        @Param("aId") int aId);

    int countAllByWishlist_Id(int wishlist_id);

    @Transactional
    @Modifying
    @Query("UPDATE Item i SET state = 1 WHERE i.id = :itemId")
    void setTakenByItemId(@Param("itemId") int itemId);

    Optional<Item> findById(int itemId);
}
