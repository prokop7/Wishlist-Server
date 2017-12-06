package server.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class WishlistNotFoundException extends RuntimeException {

    public WishlistNotFoundException(long wId) {
        super("Could not find wishlist '" + wId + "'.");
    }
}
