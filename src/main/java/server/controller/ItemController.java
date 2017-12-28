package server.controller;


import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import server.AuthorizationModule;
import server.AuthorizationObject;
import server.controller.exceptions.ItemNotFoundException;
import server.controller.exceptions.ValidationError;
import server.controller.exceptions.ValidationErrorBuilder;
import server.model.Account;
import server.model.Item;
import server.persistence.AccountRepository;
import server.persistence.ItemRepository;
import server.persistence.WishlistRepository;
import server.resources.ItemResource;
import server.resources.Mapper;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static server.AuthorizationObject.AccessType;

@RestController
@PropertySource("classpath:server-${spring.profiles.active}.properties")
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
    ItemResource addItem(@PathVariable int userId,
                         @PathVariable int wishlistId,
                         @Valid @RequestBody ItemResource itemResource,
                         @RequestAttribute Claims claims) {
        AuthorizationObject ao = new AuthorizationObject(claims);
        ao.setUserId(userId);
        ao.setWishlistId(wishlistId);
        ao.setAccessType(AccessType.PRIVATE);
        AuthorizationModule.validate(ao);
        Item item = mapper.map(itemResource);
        item.setWishlist(wishlistRepository.getOne(wishlistId));
        int order = itemRepository.countAllByWishlist_IdAndActiveIsTrue(wishlistId);
        item.setItemOrder(order);
        Item res = itemRepository.save(item);
        return mapper.map(res);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{itemId}")
    ResponseEntity<?> editItem(@PathVariable int userId,
                          @PathVariable int wishlistId,
                          @PathVariable int itemId,
                          @Valid @RequestBody ItemResource itemResource,
                          @RequestAttribute Claims claims) {
        AuthorizationObject ao = new AuthorizationObject(claims);
        ao.setUserId(userId);
        ao.setWishlistId(wishlistId);
        ao.setItemId(itemId);
        ao.setAccessType(AccessType.PRIVATE);
        AuthorizationModule.validate(ao);
        Item item = this.itemRepository.getOne(itemId);
        mapper.map(itemResource, item);
        Item res = this.itemRepository.save(item);
        return ResponseEntity.ok().build();
    }


    @RequestMapping(method = RequestMethod.DELETE, value = "/{itemId}")
    ResponseEntity<?> deleteItem(@PathVariable int userId,
                                 @PathVariable int wishlistId,
                                 @PathVariable int itemId,
                                 @RequestAttribute Claims claims) {
        AuthorizationObject ao = new AuthorizationObject(claims);
        ao.setUserId(userId);
        ao.setWishlistId(wishlistId);
        ao.setItemId(itemId);
        ao.setAccessType(AccessType.PRIVATE);
        AuthorizationModule.validate(ao);
        itemRepository.setActiveFalse(itemId);
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
                                    @Valid @RequestBody List<ItemResource> itemResources,
                                    @RequestAttribute Claims claims) {
        AuthorizationObject ao = new AuthorizationObject(claims);
        ao.setUserId(userId);
        ao.setWishlistId(wishlistId);
        ao.setAccessType(AccessType.PRIVATE);
        AuthorizationModule.validate(ao);
        List<Item> itemsToSave = new ArrayList<>();
        for (ItemResource resource : itemResources) {
            AuthorizationModule.validateWishlistsAndItems(userId, wishlistId, resource.getId());
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
        AuthorizationObject ao = new AuthorizationObject(claims);
        ao.setUserId(userId);
        ao.setWishlistId(wishlistId);
        ao.setItemId(itemId);
        ao.setAccessType(AccessType.FRIENDS_ONLY);
        AuthorizationModule.validate(ao);
        int roleId = Integer.valueOf(claims.getSubject());
        Item item = itemRepository.getOne(itemId);
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

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ValidationError handleException(MethodArgumentNotValidException exception) {
        return createValidationError(exception);
    }

    private ValidationError createValidationError(MethodArgumentNotValidException e) {
        return ValidationErrorBuilder.fromBindingErrors(e.getBindingResult());
    }
}
