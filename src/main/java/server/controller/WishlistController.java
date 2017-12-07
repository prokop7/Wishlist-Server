package server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import server.controller.exceptions.UserNotFoundException;
import server.controller.exceptions.WishlistNotFoundException;
import server.model.Wishlist;
import server.persistence.AccountRepository;
import server.persistence.WishlistRepository;
import server.resources.Mapper;
import server.resources.WishlistResource;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/user/{userId}/wishlist")
public class WishlistController {
    private final WishlistRepository wishlistRepository;
    private final AccountRepository accountRepository;
    private Mapper mapper;

    @Autowired
    public WishlistController(WishlistRepository wishlistRepository,
                              AccountRepository accountRepository,
                              Mapper mapper) {
        this.wishlistRepository = wishlistRepository;
        this.accountRepository = accountRepository;
        this.mapper = mapper;
    }

    @RequestMapping(method=RequestMethod.GET)
    List<WishlistResource> getWishlists(@PathVariable int userId) {
        validateUserId(userId);
        //TODO: get wishlists for a user, change initialization of wishlist
        List<Wishlist> wishlists = new ArrayList<>();
        List<WishlistResource> resources = new LinkedList<>();
        wishlists.forEach(wishlist -> resources.add(new WishlistResource(wishlist)));
        return resources;
    }

    @RequestMapping(method=RequestMethod.POST)
    ResponseEntity<?> addWishlist(@PathVariable int userID, @PathVariable Wishlist inputWishlist) {
        validateUserId(userID);
        //TODO: implement adding wishlist
        throw new NotImplementedException();
    }

    @RequestMapping(method=RequestMethod.POST, value="/{wishlistId}/item")
    ResponseEntity<?> addItem(@PathVariable int userID, @PathVariable int wishlistId) {
        validateUserId(userID);
        validateWishlistId(wishlistId);
        //TODO: implement adding item to the wishlist
        throw new NotImplementedException();
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
