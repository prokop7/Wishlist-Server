package server.controller;

import com.vk.api.sdk.client.Lang;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.UserAuthResponse;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import com.vk.api.sdk.queries.users.UserField;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.AuthorizationModule;
import server.AuthorizationObject;
import server.controller.parsers.FriendsResponseParser;
import server.model.Account;
import server.persistence.AccountRepository;
import server.resources.AccountCommonResource;
import server.resources.Mapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@PropertySource({"classpath:server-${spring.profiles.active}.properties",
        "classpath:oAuth-${spring.profiles.active}.properties"})
public class RegistrationController {
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
    public RegistrationController(AccountRepository accountRepository, VkApiClient vk, Mapper mapper) {
        this.accountRepository = accountRepository;
        this.vk = vk;
        this.mapper = mapper;
    }

    private Account updateAccount(UserActor actor, Lang lang) throws ClientException, ApiException {
        List<UserXtrCounters> info = vk.users()
                .get(actor)
                .fields(UserField.PHOTO_100)
                .lang(lang)
                .execute();
        Account dbAccount = accountRepository.getAccountByVkId(actor.getId());
        boolean isRegistered = dbAccount != null && dbAccount.isRegistered();
        Account account = mapper.map(info.get(0), dbAccount);
        account.setVkToken(actor.getAccessToken());
        accountRepository.save(account);
        if (!isRegistered)
            setFriends(actor, account, lang);

        return account;
    }

    //TODO handle exceptions
    @RequestMapping(method = RequestMethod.GET, value = "/registration")
    ResponseEntity<?> registerWithCode(@RequestParam String code, @RequestParam String locale) throws ClientException, ApiException {
        UserAuthResponse authResponse = vk.oauth()
                .userAuthorizationCodeFlow(
                        vkClientId,
                        vkClientSecret,
                        vkRedirectUri,
                        code)
                .execute();

        UserActor actor = new UserActor(authResponse.getUserId(), authResponse.getAccessToken());
        Lang lang = locale.equals("ru") ? Lang.RU : Lang.EN;
        Account account = updateAccount(actor, lang);
        String jwtToken;
        jwtToken = Jwts.builder()
                .setSubject(String.valueOf(account.getId()))
                .claim("roles", "user")
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, "secretkey").compact();
        return ResponseEntity.ok(String.format("{\"accessToken\":\"%s\"}", jwtToken));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/user/{userId}/friends/refresh")
    List<AccountCommonResource> refreshFriends(@PathVariable int userId,
                                               @RequestParam String locale,
                                               @RequestAttribute Claims claims) throws ClientException, ApiException {
        AuthorizationObject ao = new AuthorizationObject(claims);
        ao.setUserId(userId);
        ao.setAccessType(AuthorizationObject.AccessType.PRIVATE);
        AuthorizationModule.validate(ao);
        Lang lang = locale.equals("ru") ? Lang.RU : Lang.EN;
        Account account = accountRepository.getOne(userId);
        UserActor actor = new UserActor(account.getVkId(), account.getVkToken());
        setFriends(actor, account, lang);
        List<Account> friends = accountRepository.getAllFriends(userId);
        List<AccountCommonResource> friendsResource = new LinkedList<>();
        friends.forEach(friend -> friendsResource.add(new AccountCommonResource(friend)));
        return friendsResource;
    }

    private void setFriends(UserActor actor, Account account, Lang lang) throws ClientException {
        String friendsResponse = vk.friends()
                .get(actor)
                .lang(lang)
                .unsafeParam("order", "hints")
                .unsafeParam("fields", "first_name,last_name,photo_100")
                .executeAsString();
        List<Account> friendsRemote = new FriendsResponseParser().Parse(friendsResponse);
        List<Account> friendsDb = accountRepository.getAllFriends(account.getId());
        List<Account> saveList = getDifference(friendsRemote, friendsDb);
        List<Account> deleteList = getDifference(friendsDb, friendsRemote);

        for (Account friend : saveList) {
            if (accountRepository.getAccountByVkId(friend.getVkId()) == null)
                accountRepository.save(friend);
            else
                friend = accountRepository.getAccountByVkId(friend.getVkId());
            accountRepository.addFriendRelation(account.getId(), friend.getId());
        }

        for (Account exFriend : deleteList)
            accountRepository.removeFriendRelation(account.getId(), exFriend.getId());
    }

    private List<Account> getDifference(List<Account> list1, List<Account> list2) {
        List<Account> differenceList = new ArrayList<>();
        for (Account remoteAccount : list1) {
            boolean isPresent = false;
            for (Account dbAccount : list2)
                if (remoteAccount.getVkId() == dbAccount.getVkId()) {
                    isPresent = true;
                    break;
                }
            if (!isPresent)
                differenceList.add(remoteAccount);
        }
        return differenceList;
    }
}
