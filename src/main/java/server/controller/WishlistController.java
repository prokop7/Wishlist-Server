package server.controller;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.controller.exceptions.UserNotFoundException;
import server.controller.exceptions.WishlistNotFoundException;
import server.model.Account;
import server.model.Wishlist;
import server.persistence.AccountRepository;
import server.persistence.WishlistRepository;
import server.resources.AccountCommonResource;
import server.resources.ItemResource;
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

    @Value("${serverUri}")
    private String serverURI;

    @Autowired
    public WishlistController(WishlistRepository wishlistRepository,
                              AccountRepository accountRepository,
                              Mapper mapper) {
        this.wishlistRepository = wishlistRepository;
        this.accountRepository = accountRepository;
        this.mapper = mapper;
    }

    @RequestMapping(method = RequestMethod.GET)
    List<WishlistResource> getWishlists(@PathVariable int userId,
                                        @RequestAttribute Claims claims) {
        validateUserId(userId);
        List<Wishlist> wishlists;
        int roleId = Integer.valueOf(claims.getSubject());
        wishlists = roleId != userId
                ? wishlistRepository.getAllWithVisibility(userId, roleId)
                : wishlistRepository.getAllByAccount_Id(userId);
        List<WishlistResource> resources = new LinkedList<>();
        for (Wishlist wishlist : wishlists) {
            WishlistResource wishlistResource = new WishlistResource(wishlist);
            for (ItemResource itemResource : wishlistResource.getItems()) {
                if (itemResource.getTaker() != null) {
                    if (itemResource.getTaker().getId() == roleId)
                        itemResource.setState(3);
                    if (userId == roleId && itemResource.getState() != 2) {
                        itemResource.setState(0);
                    }
                }
            }
            resources.add(wishlistResource);
        }
        return resources;
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> addWishlist(@PathVariable int userId,
                                  @Valid @RequestBody WishlistResource wishlistResource) {
        validateUserId(userId);
        List<Account> exclusions = new ArrayList<>();
        for (AccountCommonResource exclusion : wishlistResource.getExclusions()) {
            exclusions.add(accountRepository.findAccountById(exclusion.getId()).orElseThrow(
                            () -> new UserNotFoundException(exclusion.getId())));
        }
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
    WishlistResource getWishlist(@PathVariable int userId,
                                 @PathVariable int wishlistId,
                                 @RequestAttribute Claims claims) {
        validateUserId(userId);
        validateWishlistId(wishlistId);
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
