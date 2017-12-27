package server.controller;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.AuthorizationModule;
import server.AuthorizationObject;
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

import static server.AuthorizationObject.*;

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
        AuthorizationObject ao = new AuthorizationObject(claims);
        ao.setUserId(userId);
        ao.setAccessType(AccessType.PUBLIC);
        AuthorizationModule.validate(ao);
        List<Wishlist> wishlists = new ArrayList<>();
        int roleId = Integer.parseInt(claims.getSubject());
        for (Wishlist w : wishlistRepository.getAllByAccount_IdAndActiveIsTrueOrderByWishlistOrder(userId)) {
            w.sortItems();
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
                                  @Valid @RequestBody WishlistResource wishlistResource,
                                  @RequestAttribute Claims claims) {
        AuthorizationObject ao = new AuthorizationObject(claims);
        ao.setUserId(userId);
        ao.setAccessType(AccessType.PRIVATE);
        AuthorizationModule.validate(ao);
        int nextOrder = wishlistRepository.countAllByAccount_IdAndActiveIsTrue(userId);
        List<Account> exclusions = new ArrayList<>();
        for (AccountCommonResource exclusion : wishlistResource.getExclusions()) {
            ao = new AuthorizationObject(claims);
            ao.setUserId(exclusion.getId());
            AuthorizationModule.validate(ao);
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
                                   @Valid @RequestBody WishlistResource wishlistResource,
                                   @RequestAttribute Claims claims) {
        AuthorizationObject ao = new AuthorizationObject(claims);
        ao.setUserId(userId);
        ao.setWishlistId(wishlistId);
        ao.setAccessType(AccessType.PRIVATE);
        AuthorizationModule.validate(ao);
        List<Account> exclusions = new ArrayList<>();
        for (AccountCommonResource exclusion : wishlistResource.getExclusions()) {
            ao = new AuthorizationObject(claims);
            ao.setUserId(exclusion.getId());
            AuthorizationModule.validate(ao);
        }
        Wishlist wishlist = wishlistRepository.getOne(wishlistId);
        mapper.map(wishlistResource, wishlist);
        wishlist.setExclusions(exclusions);
        wishlistRepository.save(wishlist);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{wishlistId}")
    ResponseEntity<?> deleteWishlist(@PathVariable int userId,
                                     @PathVariable int wishlistId,
                                     @RequestAttribute Claims claims) {
        AuthorizationObject ao = new AuthorizationObject(claims);
        ao.setUserId(userId);
        ao.setWishlistId(wishlistId);
        ao.setAccessType(AccessType.PRIVATE);
        AuthorizationModule.validate(ao);
        wishlistRepository.setActiveFalse(wishlistId);
        List<Wishlist> wishlists = wishlistRepository.getAllByAccount_IdAndActiveIsTrueOrderByWishlistOrder(userId);
        for (int i = 0; i < wishlists.size(); i++)
            wishlists.get(i).setWishlistOrder(i);
        wishlistRepository.save(wishlists);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/order")
    ResponseEntity<?> editWishlistOrder(@PathVariable int userId,
                                        @Valid @RequestBody List<WishlistResource> list,
                                        @RequestAttribute Claims claims) {
        AuthorizationObject ao = new AuthorizationObject(claims);
        ao.setUserId(userId);
        ao.setAccessType(AccessType.PRIVATE);
        AuthorizationModule.validate(ao);
        for (WishlistResource resource : list) {
            AuthorizationModule.validateWishlists(userId, resource.getId());
            Wishlist wishlist = wishlistRepository.getOne(resource.getId());
            wishlist.setWishlistOrder(resource.getWishlistOrder());
            wishlistRepository.save(wishlist);
        }
        return ResponseEntity.ok().build();
    }
}
