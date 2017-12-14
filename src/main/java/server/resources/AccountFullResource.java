package server.resources;

import server.model.Account;
import server.model.Wishlist;

import java.util.LinkedList;
import java.util.List;

public class AccountFullResource extends AccountCommonResource {
    //TODO Really not needed?
//    private List<AccountCommonResource> friends = new LinkedList<>();
    private List<WishlistResource> wishlists;

    public AccountFullResource(Account account) {
        super(account);
//        account.getFriends().forEach(friend -> friends.add(new AccountCommonResource(friend)));
        List<Wishlist> list = account.getWishlists();
        List<WishlistResource> wishlists = new LinkedList<>();
        list.forEach(wishlist -> wishlists.add(new WishlistResource(wishlist)));
        this.wishlists = wishlists;
    }

//    public List<AccountCommonResource> getFriends() {
//        return friends;
//    }

    public List<WishlistResource> getWishlists() {
        return wishlists;
    }
}
