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
    public long id;

    public String username;
    public int vkId;
    public String vkToken;
    public int facebookId;
    public String facebookToken;


    @OneToMany(mappedBy = "account")
    public List<Wishlist> wishlists = new ArrayList<>();

    public Account(String username) {
        this.username = username;
    }

    public Account() {
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
