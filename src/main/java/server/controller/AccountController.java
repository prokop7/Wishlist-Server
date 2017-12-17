package server.controller;

import com.vk.api.sdk.client.VkApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.*;
import server.controller.exceptions.UserNotFoundException;
import server.model.Account;
import server.persistence.AccountRepository;
import server.resources.AccountCommonResource;
import server.resources.Mapper;

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
    AccountCommonResource getAccounts(@PathVariable int userId, @RequestAttribute Object claims) {
        validateUserId(userId);
        return mapper.map(this.accountRepository.getOne(userId), AccountCommonResource.class);
    }

    private void validateUserId(int userId) {
        this.accountRepository.findAccountById(userId).orElseThrow(
                () -> new UserNotFoundException(userId));
    }
    
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
