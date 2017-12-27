package server.controller;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.UserAuthResponse;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import com.vk.api.sdk.queries.users.UserField;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.controller.parsers.FriendsResponseParser;
import server.model.Account;
import server.persistence.AccountRepository;
import server.resources.Mapper;

import java.util.Date;
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

    //TODO handle exceptions
    @RequestMapping(method = RequestMethod.GET, value = "/registration")
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
        accountRepository.save(account);

        String jwtToken;
        jwtToken = Jwts.builder()
                .setSubject(String.valueOf(account.getId()))
                .claim("roles", "user")
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, "secretkey").compact();
        return ResponseEntity.ok(String.format("{\"accessToken\":\"%s\"}", jwtToken));
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
}
