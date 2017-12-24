package server.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotBlank;
import server.model.Item;
import server.model.Wishlist;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WishlistResource {
    private int id;
    @NotBlank(message = "Wishlist name must not be blank")
    private String name;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Wishlist.Visibility visibility;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<AccountCommonResource> exclusions = new ArrayList<>();
    private int wishlistOrder;

    private List<ItemResource> items = new LinkedList<>();

    public WishlistResource(Wishlist wishlist) {
        this.id = wishlist.getId();
        this.name = wishlist.getName();
        this.visibility = wishlist.getVisibility();
        this.wishlistOrder = wishlist.getWishlistOrder();
        for (Item item : wishlist.getItems())
            if (item.isActive()) this.items.add(new ItemResource(item));
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

    public List<AccountCommonResource> getExclusions() {
        return exclusions;
    }

    public int getWishlistOrder() {
        return wishlistOrder;
    }
}
