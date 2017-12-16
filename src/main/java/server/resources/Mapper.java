package server.resources;

import com.vk.api.sdk.objects.users.UserXtrCounters;
import server.model.Account;
import server.model.Item;
import server.model.Wishlist;

public class Mapper {
    public Mapper() {
    }

    @SuppressWarnings("unchecked")
    public <T> T map(Account account, Class<T> type) {
        if (type.equals(AccountFullResource.class))
            return (T) new AccountFullResource(account);
        if (type.equals(AccountCommonResource.class))
            return (T) new AccountCommonResource(account);
        return null;
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
        wishlistResource.getItems().forEach(itemResource -> wishlist.addItem(map(itemResource)));
        return wishlist;
    }

    public Item map(ItemResource itemResource, Item item) {
        item.setName(itemResource.getName());
        item.setDescription(itemResource.getDescription());
        item.setLink(itemResource.getLink());
        item.setPrice(itemResource.getPrice());
        return item;
    }


//    private Account map(AccountCommonResource resource) {
//        Account account = accountRepository.getAccountByVkId(resource.getVkId());
//        if (account == null)
//            account = new Account(resource.getName());
//        account.setPhotoLink(resource.getPhotoLink());
//        account.setPhotoLink(resource.getPhotoLink());
//        account.setVkId(resource.getVkId());
//        return account;
//    }
}
