package server.controller;

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
@PropertySource({"classpath:server-${spring.profiles.active}.properties",
        "classpath:oAuth-${spring.profiles.active}.properties"})
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class AccountController {
    private final AccountRepository accountRepository;
    private Mapper mapper;

    @Value("${serverUri}")
    private String serverURI;

    @Autowired
    public AccountController(AccountRepository accountRepository, Mapper mapper) {
        this.accountRepository = accountRepository;
        this.mapper = mapper;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{userId}")
    AccountCommonResource getAccounts(@PathVariable int userId, @RequestAttribute Object claims) {
        validateUserId(userId);
        return mapper.map(this.accountRepository.getOne(userId));
    }

    private void validateUserId(int userId) {
        this.accountRepository.findAccountByIdAndRegisteredIsTrue(userId).orElseThrow(
                () -> new UserNotFoundException(userId));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{userId}/friends")
    List<AccountCommonResource> getAllFriends(@PathVariable int userId) {
        validateUserId(userId);
        List<Account> friends = accountRepository.getAllFriends(userId);
        List<AccountCommonResource> friendsResource = new LinkedList<>();
        friends.forEach(friend -> friendsResource.add(new AccountCommonResource(friend)));
        return friendsResource;
    }
}
