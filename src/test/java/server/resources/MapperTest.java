package server.resources;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import server.Application;
import server.model.Item;
import server.model.Wishlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
class MapperTest {
    @InjectMocks
    private Mapper mapper = new Mapper();

    @Test
    void mapWishlist() {
        Wishlist wishlist = new Wishlist("Name");
        List<Item> items = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < 20; i++) {
            Item item = new Item();
            item.setActive(r.nextBoolean());
            items.add(item);
        }

        wishlist.setItems(items);
        wishlist.setActive(false);
        wishlist.setWishlistOrder(10);
        WishlistResource resource = new WishlistResource();
        resource = mapper.map(wishlist);
        assertEqualsWishlist(wishlist, resource);
    }

    private void assertEqualsWishlist(Wishlist wishlist, WishlistResource resource) {
        assertEquals(wishlist.getName(), resource.getName());
        assertEquals(wishlist.getExclusions().size(), resource.getExclusions().size());
        assertEquals(wishlist.getVisibility(), resource.getVisibility());
        assertEquals(wishlist.getId(), resource.getId());
        assertEquals(wishlist.getWishlistOrder(), resource.getWishlistOrder());
        int count = 0;
        for (Item i : wishlist.getItems()) {
            if (i.isActive()) count++;
        }
        assertEquals(count, resource.getItems().size());
    }

    @Test
    void map1() {
    }

    @Test
    void map2() {
    }

    @Test
    void map3() {
    }

    @Test
    void map4() {
    }

    @Test
    void map5() {
    }

    @Test
    void map6() {
    }

    @Test
    void map7() {
    }

}