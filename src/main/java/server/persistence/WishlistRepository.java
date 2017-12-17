package server.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import server.model.Wishlist;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {
    Optional<Wishlist> findByAccount_Id(int wishlistId);
    Optional<Wishlist> findById(int wishlistId);
    List<Wishlist> getAllByAccount_Id(int account_id);
    List<Wishlist> getAllByAccount_IdAndVisibility(int account_id, Wishlist.Visibility visibility);

    Optional<Wishlist> findByAccount_IdAndId(int userId, int wishlistId);
}
