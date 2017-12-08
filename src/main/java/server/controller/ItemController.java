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
public class ItemController {
    private final WishlistRepository wishlistRepository;
    private final AccountRepository accountRepository;
    private ItemRepository itemRepository;
    private final Mapper mapper;

    @Value("${serverUri}")
    private String serverURI;

    @Autowired
    public ItemController(WishlistRepository wishlistRepository,
                          AccountRepository accountRepository,
                          ItemRepository itemRepository,
                          Mapper mapper) {
        this.wishlistRepository = wishlistRepository;
        this.accountRepository = accountRepository;
        this.itemRepository = itemRepository;
        this.mapper = mapper;
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> addItem(@PathVariable int userId,
                              @PathVariable int wishlistId,
                              @Valid @RequestBody ItemResource itemResource) {
        validateUserId(userId);
        validateWishlistId(wishlistId);
        Item item = mapper.map(itemResource);
        item.setWishlist(wishlistRepository.getOne(wishlistId));
        Item res = itemRepository.save(item);
        return itemRepository.findItemById(res.getId()).map(
                account -> {
                    //TODO address from config
                    URI loc = URI.create(String.format("%s/user/%d/wishlist/%d/item/%d",
                            serverURI,
                            userId,
                            wishlistId,
                            res.getId()));
                    return ResponseEntity.created(loc).build();
                }).orElse(ResponseEntity.noContent().build());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{itemId}")
    ItemResource findItem(@PathVariable int userId,
                          @PathVariable int wishlistId,
                          @PathVariable int itemId) {
        validateUserId(userId);
        validateWishlistId(wishlistId);
        return mapper.map(itemRepository.findByIdAndWishlistIdAnAndAccountId(itemId, wishlistId, userId).orElseThrow(
                () -> new WishlistNotFoundException(wishlistId)));
    }


    private void validateWishlistId(int wishlistId) {
        this.wishlistRepository.findById(wishlistId).orElseThrow(
                () -> new WishlistNotFoundException(wishlistId));
    }

    private void validateUserId(int userId) {
        this.accountRepository.findAccountById(userId).orElseThrow(
                () -> new UserNotFoundException(userId));
    }

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ValidationError handleException(MethodArgumentNotValidException exception) {
        return createValidationError(exception);
    }

    private ValidationError createValidationError(MethodArgumentNotValidException e) {
        return ValidationErrorBuilder.fromBindingErrors(e.getBindingResult());
    }
}
