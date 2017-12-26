package server.controller;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.controller.exceptions.UserNotFoundException;
import server.controller.exceptions.WishlistNotFoundException;
import server.model.Account;
import server.model.Wishlist;
import server.persistence.AccountRepository;
import server.persistence.ItemRepository;
import server.persistence.WishlistRepository;
import server.resources.Mapper;
import server.resources.WishlistResource;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/user/{userId}/wishlist")
public class WishlistController {
    private final WishlistRepository wishlistRepository;
    private final AccountRepository accountRepository;
    private Mapper mapper;
    private ItemRepository itemRepository;

    @Value("${serverUri}")
    private String serverURI;

    @Autowired
    public WishlistController(WishlistRepository wishlistRepository,
                              AccountRepository accountRepository,
                              Mapper mapper,
                              ItemRepository itemRepository) {
        this.wishlistRepository = wishlistRepository;
        this.accountRepository = accountRepository;
        this.mapper = mapper;
        this.itemRepository = itemRepository;
    }
    
    @RequestMapping(method = RequestMethod.GET)
    @ResponseStatus(value= HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    List<WishlistResource> getWishlists(@PathVariable int userId,
                                        @RequestAttribute Claims claims) {
        validateUserId(userId);
        List<Wishlist> wishlists;
        int roleId = Integer.valueOf(claims.getSubject());
        wishlists = roleId != userId
                ? wishlistRepository.getAllWithVisibility(userId, roleId)
                : wishlistRepository.getAllByAccount_Id(userId);
        List<WishlistResource> resources = new LinkedList<>();
        wishlists.forEach(wishlist -> resources.add(new WishlistResource(wishlist)));
        return resources;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(value= HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    ResponseEntity<?> addWishlist(@PathVariable int userId,
                                  @Valid @RequestBody WishlistResource wishlistResource) {
        validateUserId(userId);
        List<Account> exclusions = new ArrayList<>();
        wishlistResource.getExclusions().forEach(exclusion -> exclusions.add(
                accountRepository.findAccountById(exclusion.getId()).orElseThrow(
                        () -> new UserNotFoundException(exclusion.getId()))));
        Wishlist wishlist = mapper.map(wishlistResource);
        wishlist.setAccount(accountRepository.getOne(userId));
        wishlist.setExclusions(exclusions);
        Wishlist res = wishlistRepository.save(wishlist);
        return wishlistRepository.findById(res.getId()).map(
                account -> {
                    URI loc = URI.create(String.format("%s/user/%d/wishlist/%d",
                            serverURI,
                            userId,
                            res.getId()));
                    return ResponseEntity.created(loc).build();
                }).orElse(ResponseEntity.noContent().build());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{wishlistId}")
    @ResponseStatus(value= HttpStatus.NOT_FOUND)
    @ExceptionHandler({UserNotFoundException.class, WishlistNotFoundException.class})
    WishlistResource getWishlist(@PathVariable int userId,
                                 @PathVariable int wishlistId,
                                 @RequestAttribute Claims claims) {
        validateUserId(userId);
        validateWishlistId(wishlistId);
        System.out.println(claims.getId());
        return mapper.map(wishlistRepository.findByAccount_IdAndId(userId, wishlistId).orElseThrow(
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
}
