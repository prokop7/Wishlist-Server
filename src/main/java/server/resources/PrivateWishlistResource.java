package server.resources;

import server.model.Wishlist;

import java.util.ArrayList;
import java.util.List;

public class PrivateWishlistResource extends WishlistResource {
    private Wishlist.Visibility visibility;
    private List<AccountCommonResource> exclusions = new ArrayList<>();

    public PrivateWishlistResource(Wishlist wishlist) {
        super(wishlist);
        this.visibility = wishlist.getVisibility();
        wishlist.getExclusions().forEach(account -> this.exclusions.add(new AccountCommonResource(account)));
    }

    @Override
    public List<AccountCommonResource> getExclusions() {
        return exclusions;
    }

    @Override
    public Wishlist.Visibility getVisibility() {
        return visibility;
    }
}
