package server.controller.Parsers;

import com.vk.api.sdk.objects.users.User;
import server.model.Account;

import java.util.LinkedList;
import java.util.List;

class FriendResponseToAccountMap {
    public List<Account> GetFrom(FriendsResponse friendsResponse) {
        LinkedList<Account> accounts = new LinkedList<>();
        for (User user : friendsResponse.getItems()) {
            Account account = new Account(user.getFirstName() + " " + user.getLastName());
            account.setPhotoLink(user.getPhoto100());
            account.setVkId(user.getId());
            accounts.addLast(account);
        }
        return accounts;
    }
}
