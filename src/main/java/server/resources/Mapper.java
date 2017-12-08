package server.resources;

import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import org.springframework.beans.factory.annotation.Autowired;
import server.model.Account;
import server.model.Item;
import server.model.Wishlist;
import server.persistence.AccountRepository;

import java.util.List;

public class Mapper {
    private AccountRepository accountRepository;

    @Autowired
    public Mapper(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
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
