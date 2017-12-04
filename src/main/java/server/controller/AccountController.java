package server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import server.persistence.AccountRepository;
import server.model.Account;
import server.controller.exceptions.UserNotFoundException;
import server.persistence.WishlistRepository;

import java.net.URI;

@RestController
@RequestMapping("/user")
public class AccountController {
    private final WishlistRepository wishlistRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public AccountController(WishlistRepository wishlistRepository, AccountRepository accountRepository) {
        this.wishlistRepository = wishlistRepository;
        this.accountRepository = accountRepository;
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

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> addAccount(@RequestBody Account input) {
        Account res = accountRepository.save(input);
        return accountRepository.findAccountById(res.id).map(
                account -> {
                    URI loc = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                            .buildAndExpand(res.getId()).toUri();
                    return ResponseEntity.created(loc).build();
                }).orElse(ResponseEntity.noContent().build());
    }
}
