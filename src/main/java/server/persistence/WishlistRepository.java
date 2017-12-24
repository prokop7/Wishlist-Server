package server.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;
import server.model.Wishlist;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {
    Optional<Wishlist> findByAccount_Id(int wishlistId);

    Optional<Wishlist> findById(int wishlistId);

    List<Wishlist> getAllByAccount_IdAndActiveIsTrueOrderByWishlistOrder(int account_id);

    List<Wishlist> getAllByAccount_IdAndVisibilityAndActiveIsTrue(int account_id, Wishlist.Visibility visibility);

    Optional<Wishlist> findByAccount_IdAndIdAndActiveIsTrue(int userId, int wishlistId);

    int countAllByAccount_IdAndActiveIsTrue(int account_id);

    @Query(value = "SELECT DISTINCT " +
            "wishlist.id AS id, " +
            "wishlist.account_id AS account_id, " +
            "wishlist.name AS name, " +
            "wishlist.visibility AS visibility, " +
            "wishlist.wishlist_order AS wishlist_order " +
            "FROM wishlist " +
            "  LEFT JOIN wishlist_exclusions we ON wishlist.id = we.wishlist_id " +
            "WHERE wishlist.account_id = :userId AND wishlist.active = TRUE AND " +
            "      ((we IS NULL AND wishlist.visibility = 2) OR " +
            "       (we.exclusions_id = :roleId AND wishlist.visibility = 0) OR " +
            "       (we.exclusions_id != :roleId AND wishlist.visibility = 2)) ORDER BY wishlist_order", nativeQuery = true)
    List<Wishlist> getAllWithVisibility(
            @Param("userId")
                    int userId,
            @Param("roleId")
                    int roleId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE wishlist " +
            "SET active = FALSE " +
            "WHERE wishlist.id = :wishlistId AND wishlist.account_id = :userId", nativeQuery = true)
    void setActiveFalse(@Param("userId") int userId, @Param("wishlistId") int wishlistId);
}
