package server.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Account {
    @Id
    @GeneratedValue
    private int id;

    private String username;
    private int vkId;
    private String vkToken;
    private int facebookId;
    private String facebookToken;

    @OneToMany(mappedBy = "account")

    public List<Wishlist> wishlists = new ArrayList<>();

    protected Account() {}

    public Account(String username) {
        this.username = username;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public long getVkId() {
        return vkId;
    }

    public long getFacebookId() {
        return facebookId;
    }

    public List<Wishlist> getWishlists() {
        return wishlists;
    }
}
