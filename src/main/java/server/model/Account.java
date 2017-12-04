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

    private String username;
    private String vkId;
    private String facebookId;

    @OneToMany(mappedBy = "account")

    public List<Wishlist> wishlists = new ArrayList<>();

    public Account(String username, String vkId, String facebookId) {
        this.username = username;
        this.vkId = vkId;
        this.facebookId = facebookId;
    }

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

    public String getVkId() {
        return vkId;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public List<Wishlist> getWishlists() {
        return wishlists;
    }
}
