package server.controller;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.UserAuthResponse;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import com.vk.api.sdk.queries.users.UserField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.controller.Parsers.FriendsResponseParser;
import server.controller.exceptions.UserNotFoundException;
import server.model.Account;
import server.model.Wishlist;
import server.persistence.AccountRepository;
import server.rest_resources.AccountFullResource;
import server.rest_resources.Mapper;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
public class AccountController {
    private final AccountRepository accountRepository;
    private final VkApiClient vk;
    private Mapper mapper;

    @Autowired
    public AccountController(AccountRepository accountRepository, VkApiClient vkApiClient, Mapper mapper) {
        this.accountRepository = accountRepository;
        this.vk = vkApiClient;
        this.mapper = mapper;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{userId}")
    AccountFullResource getAccounts(@PathVariable int userId) {
        validateUserId(userId);
        return mapper.map(this.accountRepository.getOne(userId), AccountFullResource.class);
    }

    private void validateUserId(int userId) {
        this.accountRepository.findAccountById(userId).orElseThrow(
                () -> new UserNotFoundException(userId));
    }

    //TODO handle exceptions
    @RequestMapping(method = RequestMethod.GET, value = "/registration")
    @CrossOrigin(origins = "*")
    ResponseEntity<?> registerWithCode(@RequestParam String code) throws ClientException, ApiException {
        //TODO grab data from config
        UserAuthResponse authResponse = vk.oauth()
                .userAuthorizationCodeFlow(
                        6284569,
                        "kpcK0qaf4kI9dnzTpjOj",
                        "http://10.241.1.87:8081/user",
                        code)
                .execute();

        UserActor actor = new UserActor(authResponse.getUserId(), authResponse.getAccessToken());
        List<UserXtrCounters> info = vk.users()
                .get(actor)
                .fields(UserField.PHOTO_100)
                .execute();
        Account account = accountRepository.getAccountByVkId(actor.getId()) == null
                ? new Account(info.get(0).getFirstName() + " " + info.get(0).getLastName())
                : accountRepository.getAccountByVkId(actor.getId());
        account.setVkId(info.get(0).getId());
        account.setPhotoLink(info.get(0).getPhoto100());
        account.setVkToken(actor.getAccessToken());
        setFriends(actor, account);
        return addAccount(account);
    }

    void setFriends(UserActor actor, Account account) throws ClientException {
        String friendsResponse = vk.friends()
                .get(actor)
                .unsafeParam("order", "name")
                .unsafeParam("fields", "first_name,last_name,photo_100")
                .executeAsString();
        List<Account> friends = new FriendsResponseParser().Parse(friendsResponse);
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

    @RequestMapping(method=RequestMethod.GET, value="/{userId}/friends")
    List<Account> getFriends(@PathVariable int userId) {
        validateUserId(userId);
        //TODO: get friends
        return new ArrayList<Account>();
    }

    @RequestMapping(method=RequestMethod.GET, value="/{userId}/wishlists")
    List<Wishlist> getWishlists(@PathVariable int userId) {
        validateUserId(userId);
        //TODO: get wishlists for a user
        return new ArrayList<Wishlist>();
    }
}
