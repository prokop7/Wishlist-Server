package server.resources;

import server.model.Item;
import server.model.Wishlist;

import java.util.ArrayList;
import java.util.List;

public class WishlistResource {
    private String name;

    //TODO change to ItemResource
    private List<Item> items = new ArrayList<>();

    public WishlistResource(Wishlist wishlist) {
        this.name = wishlist.getName();
        this.items = wishlist.getItems();
    }

    public String getName() {
        return name;
    }

    public List<Item> getItems() {
        return items;
    }
}
