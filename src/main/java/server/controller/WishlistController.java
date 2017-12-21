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
import server.resources.*;

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
        List<Wishlist> wishlists = new ArrayList<>();
        int roleId = Integer.valueOf(claims.getSubject());
        for (Wishlist w : wishlistRepository.getAllByAccount_IdOrderByWishlistOrder(userId)) {
            boolean inExclusion = false;
            for (Account account : w.getExclusions()) {
                if (account.getId() == roleId) {
                    inExclusion = true;
                    break;
                }
            }
            if ((w.getVisibility().getValue() == 0) == inExclusion || roleId == userId)
                wishlists.add(w);
        }
        List<WishlistResource> resources = new LinkedList<>();
        for (Wishlist wishlist : wishlists) {
            WishlistResource wishlistResource = roleId == userId
                    ? new PrivateWishlistResource(wishlist)
                    : new WishlistResource(wishlist);
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
        int nextOrder = wishlistRepository.countAllByAccount_Id(userId);
        List<Account> exclusions = new ArrayList<>();
        for (AccountCommonResource exclusion : wishlistResource.getExclusions()) {
            exclusions.add(accountRepository.findAccountById(exclusion.getId()).orElseThrow(
                    () -> new UserNotFoundException(exclusion.getId())));
        }
        Wishlist wishlist = mapper.map(wishlistResource);
        wishlist.setWishlistOrder(nextOrder);
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

    @RequestMapping(method = RequestMethod.PUT, value = "/{wishlistId}")
    ResponseEntity<?> editWishlist(@PathVariable int userId,
                                   @PathVariable int wishlistId,
                                   @Valid @RequestBody WishlistResource wishlistResource) {
        validateUserId(userId);
        validateWishlistId(wishlistId);
        List<Account> exclusions = new ArrayList<>();
        for (AccountCommonResource exclusion : wishlistResource.getExclusions()) {
            exclusions.add(accountRepository.findAccountById(exclusion.getId()).orElseThrow(
                    () -> new UserNotFoundException(exclusion.getId())));
        }
        Wishlist wishlist = wishlistRepository.findByAccount_IdAndId(userId, wishlistId).orElseThrow(
                () -> new WishlistNotFoundException(wishlistId));
        mapper.map(wishlistResource, wishlist);
        wishlist.setExclusions(exclusions);
        Wishlist res = wishlistRepository.save(wishlist);
        return wishlistRepository.findById(res.getId()).map(
                account -> ResponseEntity.ok(res)).orElse(ResponseEntity.noContent().build());
    }

    @RequestMapping(method = RequestMethod.PUT)
    ResponseEntity<?> editWishlistOrder(@PathVariable int userId,
                                        @Valid @RequestBody List<WishlistResource> list) {
        validateUserId(userId);
        for (WishlistResource resource : list) {
            validateWishlistId(resource.getId());
            Wishlist wishlist = wishlistRepository.getOne(resource.getId());
            wishlist.setWishlistOrder(resource.getWishlistOrder());
            wishlistRepository.save(wishlist);
        }
        return ResponseEntity.ok().build();
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
