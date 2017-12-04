package server.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import server.model.Wishlist;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    Optional<Wishlist> findByAccount_Id(long user_id);
    List<Wishlist> getAllByAccount_Id(long account_id);
}
