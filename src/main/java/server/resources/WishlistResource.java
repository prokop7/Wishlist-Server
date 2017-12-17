package server.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.validator.constraints.NotBlank;
import server.model.Wishlist;

import java.util.LinkedList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WishlistResource {
    private int id;
    @NotBlank(message = "Wishlist name must not be blank")
    private String name;
    private Wishlist.Visibility visibility;

    //TODO change to ItemResource
    private List<ItemResource> items = new LinkedList<>();

    public WishlistResource(Wishlist wishlist) {
        this.id = wishlist.getId();
        this.name = wishlist.getName();
        this.visibility = wishlist.getVisibility();
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

    public Wishlist.Visibility getVisibility() {
        return visibility;
    }
}
