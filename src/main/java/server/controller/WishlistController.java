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

import java.util.List;

@RestController
@RequestMapping("/user/{userId}/wishlist")
public class WishlistController {
    private final WishlistRepository wishlistRepository;
    private final AccountController accountController;
    private final AccountRepository accountRepository;

    @Autowired
    public WishlistController(WishlistRepository wishlistRepository,
                              AccountController accountController,
                              AccountRepository accountRepository) {
        this.wishlistRepository = wishlistRepository;
        this.accountController = accountController;
        this.accountRepository = accountRepository;
    }

    @RequestMapping(method=RequestMethod.POST)
    ResponseEntity<?> addWishlist(@PathVariable long userID, @PathVariable Wishlist inputWishlist) {
        validateUserId(userID);
        //TODO: implement adding wishlist
    }

    @RequestMapping(method=RequestMethod.POST, value="/{wId}/item")
    ResponseEntity<?> addItem(@PathVariable long userID, @PathVariable long wId) {
        validateUserId(userID);
        validateWishlistId(wId);
        //TODO: implement adding item to the wishlist
    }

    private void validateWishlistId(long wId) {
        this.wishlistRepository.findWishlistById(wId).orElseThrow(
                () -> new WishlistNotFoundException(wId));
    }

    private void validateUserId(long userId) {
        this.accountRepository.findAccountById(userId).orElseThrow(
                () -> new UserNotFoundException(userId));
    }

}
