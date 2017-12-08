package server.resources;

import server.model.Wishlist;

import java.util.LinkedList;
import java.util.List;

public class WishlistResource {
    private int id;
    private String name;

    //TODO change to ItemResource
    private List<ItemResource> items = new LinkedList<>();

    public WishlistResource(Wishlist wishlist) {
        this.id = wishlist.getId();
        this.name = wishlist.getName();
        wishlist.getItems().forEach(item -> this.items.add(new ItemResource(item)));
    }

    public WishlistResource() {
    }

    public String getName() {
        return name;
    }

    public List<ItemResource> getItems() {
        return items;
    }

    public int getId() {
        return id;
    }
}
