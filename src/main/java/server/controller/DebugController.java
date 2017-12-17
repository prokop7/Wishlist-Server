package server.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import server.controller.exceptions.UserNotFoundException;
import server.controller.exceptions.ValidationError;
import server.controller.exceptions.ValidationErrorBuilder;
import server.controller.exceptions.WishlistNotFoundException;
import server.model.Account;
import server.model.Item;
import server.persistence.AccountRepository;
import server.persistence.ItemRepository;
import server.persistence.WishlistRepository;
import server.resources.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@PropertySource("classpath:server.properties")
@RequestMapping("/test/{userId}")
public class DebugController {
    private final WishlistRepository wishlistRepository;
    private final AccountRepository accountRepository;
    private ItemRepository itemRepository;
    private final Mapper mapper;

    @Autowired
    public DebugController(WishlistRepository wishlistRepository,
                          AccountRepository accountRepository,
                          ItemRepository itemRepository,
                          Mapper mapper) {
        this.wishlistRepository = wishlistRepository;
        this.accountRepository = accountRepository;
        this.itemRepository = itemRepository;
        this.mapper = mapper;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Resource getUser(@PathVariable int userId) {
        try {
            validateUserId(userId);
            return mapper.map(this.accountRepository.getOne(userId), AccountFullResource.class);
        } catch (UserNotFoundException ex) {
            MessageResource mr = new MessageResource(false, ex.getMessage());
            return mr;
        }

    }

    private void validateUserId(int userId) {
        this.accountRepository.findAccountById(userId).orElseThrow(
                () -> new UserNotFoundException(userId));
    }



}
