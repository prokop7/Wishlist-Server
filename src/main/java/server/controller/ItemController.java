package server.controller;


import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import server.controller.exceptions.*;
import server.model.Account;
import server.model.Item;
import server.persistence.AccountRepository;
import server.persistence.ItemRepository;
import server.persistence.WishlistRepository;
import server.resources.ItemResource;
import server.resources.Mapper;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@PropertySource("classpath:server.properties")
@RequestMapping("/user/{userId}/wishlist/{wishlistId}/item")
@CrossOrigin("*")
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
        int order = itemRepository.countAllByWishlist_IdAndActiveIsTrue(wishlistId);
        item.setItemOrder(order);
        Item res = itemRepository.save(item);
        return itemRepository.findItemById(res.getId()).map(
                account -> {
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

    @RequestMapping(method = RequestMethod.PUT, value = "/{itemId}")
    ResponseEntity<?> editItem(@PathVariable int userId,
                               @PathVariable int wishlistId,
                               @PathVariable int itemId,
                               @Valid @RequestBody ItemResource itemResource) {
        validateUserId(userId);
        validateWishlistId(wishlistId);
        validateItemId(itemId);
        Item item = this.itemRepository.getOne(itemId);
        mapper.map(itemResource, item);
        Item res = this.itemRepository.save(item);

        return itemRepository.findItemById(res.getId()).map(
                account -> {
                    URI loc = URI.create(String.format("%s/user/%d/wishlist/%d/item/%d",
                            serverURI,
                            userId,
                            wishlistId,
                            res.getId()));
                    return ResponseEntity.created(loc).build();
                }).orElse(ResponseEntity.noContent().build());
    }


    @RequestMapping(method = RequestMethod.DELETE, value = "/{itemId}")
    ResponseEntity<?> deleteItem(@PathVariable int userId,
                                 @PathVariable int wishlistId,
                                 @PathVariable int itemId) {
        validateUserId(userId);
        validateWishlistId(wishlistId);
        validateItemId(itemId);
        itemRepository.setActiveFalse(userId, wishlistId, itemId);
        List<Item> items = itemRepository.getAll(userId, wishlistId).orElseThrow(
                () -> new ItemNotFoundException(itemId));
        for (int i = 0; i < items.size(); i++) {
            items.get(i).setItemOrder(i);
        }
        itemRepository.save(items);
        return ResponseEntity.ok().build();
    }


    @RequestMapping(method = RequestMethod.PUT, value = "/order")
    ResponseEntity<?> setItemsOrder(@PathVariable int userId,
                                    @PathVariable int wishlistId,
                                    @Valid @RequestBody List<ItemResource> itemResources) {
        validateUserId(userId);
        validateWishlistId(wishlistId);
        List<Item> itemsToSave = new ArrayList<>();
        for (ItemResource resource : itemResources) {
            validateItemId(resource.getId());
            Item item = itemRepository.getOne(resource.getId());
            item.setItemOrder(resource.getItemOrder());
            itemsToSave.add(item);
        }
        itemRepository.save(itemsToSave);
        return ResponseEntity.ok().build();
    }


    @RequestMapping(method = RequestMethod.POST, value = "/{itemId}/state")
    ResponseEntity<?> takeItem(@PathVariable int userId,
                               @PathVariable int wishlistId,
                               @PathVariable int itemId,
                               @RequestBody int state,
                               @RequestAttribute Claims claims) {
        validateUserId(userId);
        validateWishlistId(wishlistId);
        validateItemId(itemId);
        int roleId = Integer.valueOf(claims.getSubject());
        Item item = itemRepository.findByIdAndWishlistIdAnAndAccountId(itemId, wishlistId, userId).orElseThrow(
                () -> new ItemNotFoundException(userId));
        Account user = accountRepository.getOne(roleId);
        if (roleId != userId)
            item.setTaker(user);
        if (state == 0)
            item.setTaker(null);
        item.setState(state);
//        itemRepository.setTakenByItemId(itemId);
        Item res = itemRepository.save(item);
        return itemRepository.findById(res.getId()).map(
                account -> ResponseEntity.ok().build()).orElse(ResponseEntity.noContent().build());
    }


    private void validateWishlistId(int wishlistId) {
        this.wishlistRepository.findById(wishlistId).orElseThrow(
                () -> new WishlistNotFoundException(wishlistId));
    }

    private void validateUserId(int userId) {
        this.accountRepository.findAccountById(userId).orElseThrow(
                () -> new UserNotFoundException(userId));
    }

    private void validateItemId(int itemId) {
        this.itemRepository.findItemById(itemId).orElseThrow(
                () -> new ItemNotFoundException(itemId));
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
