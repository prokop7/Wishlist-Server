package server;

import io.jsonwebtoken.Claims;

public class AuthorizationObject {
    private Claims claims;
    private Integer userId;
    private Integer wishlistId;
    private Integer itemId;
    private AccessType accessType = AccessType.PUBLIC;

    public enum AccessType {
        PUBLIC,
        FRIENDS_ONLY,
        PRIVATE
    }

    public AuthorizationObject(Claims claims) {
        this.claims = claims;
    }

    public void setAccessType(AccessType accessType) {
        this.accessType = accessType;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public void setWishlistId(Integer wishlistId) {
        this.wishlistId = wishlistId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getWishlistId() {
        return wishlistId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public AccessType getAccessType() {
        return accessType;
    }

    public Claims getClaims() {
        return claims;
    }
}
