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
import server.model.Item;
import server.persistence.AccountRepository;
import server.persistence.ItemRepository;
import server.persistence.WishlistRepository;
import server.resources.ItemResource;
import server.resources.Mapper;

import javax.validation.Valid;
import java.net.URI;

@RestController
@PropertySource("classpath:server.properties")
@RequestMapping("/user/{userId}/wishlist/{wishlistId}/item")
public class DebugController {
    private final WishlistRepository wishlistRepository;
    private final AccountRepository accountRepository;
    private ItemRepository itemRepository;


}
