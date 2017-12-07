package server.resources;

import org.springframework.beans.factory.annotation.Autowired;
import server.model.Account;
import server.persistence.AccountRepository;

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
