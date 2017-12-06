package server.rest_resources;

import server.model.Account;
import server.model.Wishlist;

import java.util.LinkedList;
import java.util.List;

public class AccountFullResource extends AccountCommonResource {
    private List<AccountCommonResource> friends = new LinkedList<>();
    private List<Wishlist> wishlists;

    public AccountFullResource(Account account) {
        super(account);
        account.getFriends().forEach(friend -> friends.add(new AccountCommonResource(friend)));
        wishlists = account.getWishlists();
    }

    public List<AccountCommonResource> getFriends() {
        return friends;
    }

    public List<Wishlist> getWishlists() {
        return wishlists;
    }
}
