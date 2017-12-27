package server;

import server.controller.exceptions.AccessDeniedException;
import server.controller.exceptions.ItemNotFoundException;
import server.controller.exceptions.UserNotFoundException;
import server.controller.exceptions.WishlistNotFoundException;
import server.persistence.AccountRepository;
import server.persistence.ItemRepository;
import server.persistence.WishlistRepository;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import static server.AuthorizationObject.AccessType.*;

public class AuthorizationModule {
    private static AccountRepository accountRepository;
    private static WishlistRepository wishlistRepository;
    private static ItemRepository itemRepository;


    public static void validate(AuthorizationObject ao) {
        int userId = Integer.parseInt(ao.getClaims().getSubject());
        if (ao.getUserId() == null)
            throw new NotImplementedException();
        if (!userExists(ao.getUserId()))
            throw new UserNotFoundException(ao.getUserId());

        if (ao.getAccessType() == PRIVATE)
            validatePrivate(userId, ao.getUserId(), ao.getWishlistId(), ao.getItemId());
        else if (ao.getAccessType() == FRIENDS_ONLY || ao.getAccessType() == PUBLIC)
            validateFriendsOnly(userId, ao.getUserId(), ao.getWishlistId(), ao.getItemId());
//        else if (ao.getAccessType() == PUBLIC)
//            validateWishlistsAndItems(ao.getUserId(), ao.getWishlistId(), ao.getItemId());
    }

    private static void validateFriendsOnly(int userId,
                                               int requestedUserId,
                                               Integer requestedWishlistId,
                                               Integer requestedItemId) {
        if (userId != requestedUserId && !isFriend(userId, requestedUserId))
            throw new AccessDeniedException();
        validateWishlistsAndItems(requestedUserId, requestedWishlistId, requestedItemId);
    }
    private static void validatePrivate(int userId,
                                            int requestedUserId,
                                            Integer requestedWishlistId,
                                            Integer requestedItemId) {
        if (userId != requestedUserId)
            throw new AccessDeniedException();
        validateWishlistsAndItems(requestedUserId, requestedWishlistId, requestedItemId);
    }

    public static void validateWishlists(int userId, int wishlistId) {
        if (!isBelong(userId, wishlistId)) throw new WishlistNotFoundException(wishlistId);
    }

    public static void validateWishlistsAndItems(int userId, Integer wishlistId, Integer itemId) {
        if (wishlistId == null)
            return;
        if (itemId == null)
            if (isBelong(userId, wishlistId))
                return;
            else
                throw new WishlistNotFoundException(wishlistId);
        if (!isBelong(userId, wishlistId, itemId))
            throw new ItemNotFoundException(itemId);
    }

    private static boolean isFriend(int userId, int requestedUserId) {
        return accountRepository.isFriend(userId, requestedUserId);
    }

    private static boolean userExists(int userId) {
        return accountRepository.findAccountByIdAndRegisteredIsTrue(userId).isPresent();
    }

    private static boolean isBelong(int userId, int wishlistId, int itemId) {
        return itemRepository.findByIdAndUserAndWishlist(userId, wishlistId, itemId).isPresent();
    }

    private static boolean isBelong(int userId, int wishlistId) {
        return wishlistRepository.findByAccount_IdAndIdAndActiveIsTrue(userId, wishlistId).isPresent();
    }

    public static void setAccountRepository(AccountRepository accountRepository) {
        AuthorizationModule.accountRepository = accountRepository;
    }

    public static void setWishlistRepository(WishlistRepository wishlistRepository) {
        AuthorizationModule.wishlistRepository = wishlistRepository;
    }

    public static void setItemRepository(ItemRepository itemRepository) {
        AuthorizationModule.itemRepository = itemRepository;
    }
}
