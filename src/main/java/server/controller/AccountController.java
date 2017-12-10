package server.controller;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.UserAuthResponse;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import com.vk.api.sdk.queries.users.UserField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.controller.parsers.FriendsResponseParser;
import server.controller.exceptions.UserNotFoundException;
import server.model.Account;
import server.persistence.AccountRepository;
import server.resources.AccountCommonResource;
import server.resources.AccountFullResource;
import server.resources.Mapper;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

@RestController
@PropertySource({"classpath:server.properties", "classpath:oAuth.properties"})
@RequestMapping("/user")
public class AccountController {
    private final AccountRepository accountRepository;
    private final VkApiClient vk;
    private Mapper mapper;

    @Value("${serverUri}")
    private String serverURI;

    @Value("${vkRedirectUri}")
    private String vkRedirectUri;

    @Value("${vkClientSecret}")
    private String vkClientSecret;

    @Value("${vkClientId}")
    private Integer vkClientId;

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
        UserAuthResponse authResponse = vk.oauth()
                .userAuthorizationCodeFlow(
                        vkClientId,
                        vkClientSecret,
                        vkRedirectUri,
                        code)
                .execute();

        UserActor actor = new UserActor(authResponse.getUserId(), authResponse.getAccessToken());
        List<UserXtrCounters> info = vk.users()
                .get(actor)
                .fields(UserField.PHOTO_100)
                .execute();
        Account account = mapper.map(info.get(0), accountRepository.getAccountByVkId(actor.getId()));
        account.setVkToken(actor.getAccessToken());
        setFriends(actor, account);
        return addAccount(account);
    }

    private void setFriends(UserActor actor, Account account) throws ClientException {
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

    private ResponseEntity<?> addAccount(Account input) {
        Account res = accountRepository.save(input);
        return accountRepository.findAccountById(res.getId()).map(
                account -> {
                    //TODO address from config
                    URI loc = URI.create(String.format("%s/user/%d", serverURI, res.getId()));
                    return ResponseEntity.created(loc).build();
                }).orElse(ResponseEntity.noContent().build());
    }

    @RequestMapping(method=RequestMethod.GET, value="/{userId}/friends")
    List<AccountCommonResource> getRegistredFriends(@PathVariable int userId) {
        validateUserId(userId);
        List<Account> friends = accountRepository.getRegisteredFriends(userId);
        List<AccountCommonResource> friendsResource = new LinkedList<>();
        friends.forEach(friend -> friendsResource.add(new AccountCommonResource(friend)));
        return friendsResource;
    }

    @RequestMapping(method=RequestMethod.GET, value="/{userId}/all_friends")
    List<AccountCommonResource> getAllFriends(@PathVariable int userId) {
        validateUserId(userId);
        List<Account> friends = accountRepository.getAllFriends(userId);
        List<AccountCommonResource> friendsResource = new LinkedList<>();
        friends.forEach(friend -> friendsResource.add(new AccountCommonResource(friend)));
        return friendsResource;
    }
}
