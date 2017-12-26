package server.resources;

import com.vk.api.sdk.objects.users.UserXtrCounters;
import server.model.Account;
import server.model.Item;
import server.model.Wishlist;

public class Mapper {
    public Mapper() {
    }

    public AccountCommonResource map(Account account) {
        return new AccountCommonResource(account);
    }

    public WishlistResource map(Wishlist wishlist) {
        return new WishlistResource(wishlist);
    }

    public Account map(UserXtrCounters info, Account account) {
        if (account == null)
            account = new Account(String.format("%s %s", info.getFirstName(), info.getLastName()));
        account.setVkId(info.getId());
        account.setPhotoLink(info.getPhoto100());
        account.setRegistered(true);
        return account;
    }

    public Item map(ItemResource itemResource) {
        Item item = new Item(itemResource.getName());
        item.setLink(itemResource.getLink());
        item.setDescription(itemResource.getDescription());
        item.setPrice(itemResource.getPrice());
        return item;
    }

    public ItemResource map(Item item) {
        return new ItemResource(item);
    }

    public Wishlist map(WishlistResource wishlistResource) {
        Wishlist wishlist = new Wishlist(wishlistResource.getName());
        wishlist.setVisibility(wishlistResource.getVisibility());
        return wishlist;
    }

    public void map(WishlistResource wishlistResource, Wishlist wishlist) {
        wishlist.setName(wishlistResource.getName());
        wishlist.setVisibility(wishlistResource.getVisibility());
    }

    public void map(ItemResource itemResource, Item item) {
        item.setName(itemResource.getName());
        item.setDescription(itemResource.getDescription());
        item.setLink(itemResource.getLink());
        item.setPrice(itemResource.getPrice());
    }
}
