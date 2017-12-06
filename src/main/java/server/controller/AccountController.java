package server.controller;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.Actor;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.UserAuthResponse;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import com.vk.api.sdk.queries.friends.FriendsGetOrder;
import com.vk.api.sdk.queries.users.UserField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.controller.Parsers.FriendsResponseParser;
import server.controller.exceptions.UserNotFoundException;
import server.model.Account;
import server.persistence.AccountRepository;
import server.persistence.WishlistRepository;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/user")
public class AccountController {
    private final WishlistRepository wishlistRepository;
    private final AccountRepository accountRepository;
    private final VkApiClient vk;

    @Autowired
    public AccountController(WishlistRepository wishlistRepository, AccountRepository accountRepository, VkApiClient vkApiClient) {
        this.wishlistRepository = wishlistRepository;
        this.accountRepository = accountRepository;
        this.vk = vkApiClient;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{userId}")
    Account getAccounts(@PathVariable int userId) {
        validateUserId(userId);
        return this.accountRepository.findAccountById(userId).get();
    }

    private void validateUserId(int userId) {
        this.accountRepository.findAccountById(userId).orElseThrow(
                () -> new UserNotFoundException(userId));
    }

    //TODO handle exceptions
    @RequestMapping(method = RequestMethod.GET, value = "/registration/vk")
    @CrossOrigin(origins = "*")
    ResponseEntity<?> registerWithCode(@RequestParam String code) throws ClientException, ApiException {
        //TODO grab data from config
        UserAuthResponse authResponse = vk.oauth()
                .userAuthorizationCodeFlow(
                        6284569,
                        "kpcK0qaf4kI9dnzTpjOj",
                        "http://localhost:8081/user",
                        code)
                .execute();

        UserActor actor = new UserActor(authResponse.getUserId(), authResponse.getAccessToken());
        List<UserXtrCounters> info = vk.users()
                .get(actor)
                .fields(UserField.PERSONAL)
                .execute();
        Account account = accountRepository.getAccountByVkId(actor.getId()) == null
                ? new Account(info.get(0).getFirstName() + " " + info.get(0).getLastName())
                : accountRepository.getAccountByVkId(actor.getId());
        account.setVkId(info.get(0).getId());
        account.setVkToken(actor.getAccessToken());
        setFriends(actor, account);
        return addAccount(account);
    }

    void setFriends(UserActor actor, Account account) throws ClientException {
        String friendsResponse = vk.friends()
                .get(actor)
                .unsafeParam("fields", "first_name,last_name,photo_medium")
                .order(FriendsGetOrder.HINTS)
                .executeAsString();
        FriendsResponseParser parser = new FriendsResponseParser();
        List<Account> friends = parser.Parse(friendsResponse);
        for (int i = 0; i < friends.size(); i++) {
            Account friend = friends.get(i);
            if (accountRepository.getAccountByVkId(friend.getVkId()) == null)
                accountRepository.save(friend);
            else
                friends.set(i, accountRepository.getAccountByVkId(friend.getVkId()));
        }
        account.setFriends(friends);
    }

//    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> addAccount(@RequestBody Account input) {
        Account res = accountRepository.save(input);
        return accountRepository.findAccountById(res.getId()).map(
                account -> {
                    URI loc = URI.create("http://localhost:8080/user/" + res.getId());
                    return ResponseEntity.created(loc).build();
                }).orElse(ResponseEntity.noContent().build());
    }
}
