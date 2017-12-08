package server.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import server.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    Optional<List<Item>> getAllByWishlistId(int wishlist_id);

    Optional<Item> findItemById(int id);
}
