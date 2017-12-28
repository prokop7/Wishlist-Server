package server.controller;

import com.fasterxml.jackson.databind.node.TextNode;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.AuthorizationModule;
import server.AuthorizationObject;
import server.AuthorizationObject.AccessType;
import server.model.Account;
import server.persistence.AccountRepository;
import server.resources.AccountCommonResource;
import server.resources.AccountFullResource;
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
    AccountFullResource getAccounts(@PathVariable int userId, @RequestAttribute Claims claims) {
        AuthorizationObject ao = new AuthorizationObject(claims);
        ao.setUserId(userId);
        ao.setAccessType(AccessType.PRIVATE);
        AuthorizationModule.validate(ao);
        return mapper.map(this.accountRepository.getOne(userId));
    }


    @RequestMapping(method = RequestMethod.PUT, value = "/{userId}")
    ResponseEntity<?> editBackground(@PathVariable int userId,
                                  @RequestBody TextNode background,
                                  @RequestAttribute Claims claims) {
        AuthorizationObject ao = new AuthorizationObject(claims);
        ao.setUserId(userId);
        ao.setAccessType(AccessType.PRIVATE);
        AuthorizationModule.validate(ao);

        //TODO replace with UPDATE query
        Account account = accountRepository.getOne(userId);
        account.setBackground(background.asText());
        accountRepository.save(account);

        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{userId}/friends")
    List<AccountCommonResource> getAllFriends(@PathVariable int userId, @RequestAttribute Claims claims) {
        AuthorizationObject ao = new AuthorizationObject(claims);
        ao.setUserId(userId);
        ao.setAccessType(AccessType.PRIVATE);
        AuthorizationModule.validate(ao);
        List<Account> friends = accountRepository.getAllFriends(userId);
        List<AccountCommonResource> friendsResource = new LinkedList<>();
        friends.forEach(friend -> friendsResource.add(new AccountCommonResource(friend)));
        return friendsResource;
    }
}
