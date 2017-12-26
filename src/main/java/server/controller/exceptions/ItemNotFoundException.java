package server.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ItemNotFoundException extends RuntimeException {

    public ItemNotFoundException(long itemId) {
        super("could not find item '" + itemId + "'.");
    }
}
