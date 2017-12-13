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

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


import java.net.URI;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@RestController
@PropertySource({"classpath:server.properties", "classpath:oAuth.properties"})
@RequestMapping("/user")
@CrossOrigin(origins = "*")
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
    AccountFullResource getAccounts(@PathVariable int userId, @RequestAttribute Object claims) {
        validateUserId(userId);
        return mapper.map(this.accountRepository.getOne(userId), AccountFullResource.class);
    }

    private void validateUserId(int userId) {
        this.accountRepository.findAccountById(userId).orElseThrow(
                () -> new UserNotFoundException(userId));
    }

//    private ResponseEntity<?> addAccount(Account input) {
//        Account res = accountRepository.save(input);
//        return accountRepository.findAccountById(res.getId()).map(
//                account -> {
//                    URI loc = URI.create(String.format("%s/user/%d", serverURI, res.getId()));
//                    return ResponseEntity.created(loc).build();
//                }).orElse(ResponseEntity.noContent().build());
//    }

    @RequestMapping(method = RequestMethod.GET, value = "/{userId}/friends")
    List<AccountCommonResource> getRegistredFriends(@PathVariable int userId) {
        validateUserId(userId);
        List<Account> friends = accountRepository.getRegisteredFriends(userId);
        List<AccountCommonResource> friendsResource = new LinkedList<>();
        friends.forEach(friend -> friendsResource.add(new AccountCommonResource(friend)));
        return friendsResource;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{userId}/all_friends")
    List<AccountCommonResource> getAllFriends(@PathVariable int userId) {
        validateUserId(userId);
        List<Account> friends = accountRepository.getAllFriends(userId);
        List<AccountCommonResource> friendsResource = new LinkedList<>();
        friends.forEach(friend -> friendsResource.add(new AccountCommonResource(friend)));
        return friendsResource;
    }
}
