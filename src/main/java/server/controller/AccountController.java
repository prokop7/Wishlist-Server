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
import org.springframework.web.servlet.view.RedirectView;
import server.model.Wishlist;
import server.persistence.AccountRepository;
import server.model.Account;
import server.controller.exceptions.UserNotFoundException;
import server.persistence.WishlistRepository;

import java.net.URI;
import java.util.ArrayList;
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
    Account getAccounts(@PathVariable long userId) {
        validateUserId(userId);
        return this.accountRepository.findAccountById(userId).get();
    }

    private void validateUserId(long userId) {
        this.accountRepository.findAccountById(userId).orElseThrow(
                () -> new UserNotFoundException(userId));
    }

    //TODO handle exceptions
    @RequestMapping(method = RequestMethod.GET, value = "/registration")
    ResponseEntity<?> registerWithCode(@RequestParam String code) throws ClientException, ApiException {
        //TODO grab data from
        UserAuthResponse authResponse = vk.oauth()
                .userAuthorizationCodeFlow(
                        6284569,
                        "kpcK0qaf4kI9dnzTpjOj",
                        "http://localhost:8080/user/registration",
                        code)
                .execute();

        UserActor actor = new UserActor(authResponse.getUserId(), authResponse.getAccessToken());
        List<UserXtrCounters> info = vk.users()
                .get(actor)
                .fields(UserField.PERSONAL)
                .execute();
        Account account = new Account(info.get(0).getFirstName() + info.get(0).getLastName());
        account.setVkId(info.get(0).getId());
        account.setVkToken(actor.getAccessToken());
        return addAccount(account);
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> addAccount(@RequestBody Account input) {
        Account res = accountRepository.save(input);
        return accountRepository.findAccountById(res.getId()).map(
                account -> {
                    URI loc = URI.create("http://localhost:8080/user/" + res.getId());
                    return ResponseEntity.created(loc).build();
                }).orElse(ResponseEntity.noContent().build());
    }

    @RequestMapping(method=RequestMethod.GET, value="/{iserId}/friends")
    List<Account> getFriends(@PathVariable long userId) {
        validateUserId(userId);
        //TODO: get friends
        return new ArrayList<Account>();
    }

    @RequestMapping(method=RequestMethod.GET, value="/{userId}/wishlists")
    List<Wishlist> getWishlists(@PathVariable long userId) {
        validateUserId(userId);
        //TODO: get wishlists for a user
        return new ArrayList<Wishlist>();
    }
}
