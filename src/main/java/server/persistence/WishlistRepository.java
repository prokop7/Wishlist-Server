package server.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import server.model.Wishlist;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {
    Optional<Wishlist> findByAccount_Id(int wishlistId);

    Optional<Wishlist> findById(int wishlistId);

    List<Wishlist> getAllByAccount_Id(int account_id);

    List<Wishlist> getAllByAccount_IdAndVisibility(int account_id, Wishlist.Visibility visibility);

    Optional<Wishlist> findByAccount_IdAndId(int userId, int wishlistId);

    @Query(value = "SELECT DISTINCT " +
            "wishlist.id AS id, " +
            "wishlist.account_id AS account_id, " +
            "wishlist.name AS name, " +
            "wishlist.visibility AS visibility " +
            "FROM wishlist " +
            "  LEFT JOIN wishlist_exclusions we ON wishlist.id = we.wishlist_id " +
            "WHERE wishlist.account_id = :userId AND " +
            "      ((we IS NULL AND wishlist.visibility = 2) OR " +
            "       (we.exclusions_id = :roleId AND wishlist.visibility = 0) OR " +
            "       (we.exclusions_id != :roleId AND wishlist.visibility = 2))", nativeQuery = true
    )
    List<Wishlist> getAllWithVisibility(
            @Param("userId")
                    int userId,
            @Param("roleId")
                    int roleId);
}
