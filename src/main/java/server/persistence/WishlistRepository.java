package server.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import server.model.Wishlist;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    Optional<Wishlist> findWishlistById(long wId);

    List<Wishlist> getAllByUserId(long userId);
}
