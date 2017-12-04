package server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import server.model.Wishlist;
import server.persistence.WishlistRepository;

import java.util.List;

@RestController
@RequestMapping("{userId}/wishlists")
public class WishlistController {
    private final WishlistRepository wishlistRepository;
    private final AccountController accountController;

    @Autowired
    public WishlistController(WishlistRepository wishlistRepository,
                              AccountController accountController) {
        this.wishlistRepository = wishlistRepository;
        this.accountController = accountController;
    }

    @RequestMapping(method = RequestMethod.GET)
    List<Wishlist> getWishlists(@PathVariable long userId) {
        return wishlistRepository.getAllByAccount_Id(userId);
    }

}
