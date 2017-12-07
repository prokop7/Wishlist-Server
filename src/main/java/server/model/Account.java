package server.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Account {
    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy="increment")
    private int id;

    private String username;
    private int vkId;
    private String vkToken;
    private int facebookId;
    private String facebookToken;
    private String photoLink;
    private boolean registered = false;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Account> friends;


    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<Wishlist> wishlists = new ArrayList<>();

    protected Account() {}

    public Account(String username) {
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public int getVkId() {
        return vkId;
    }

    public int getFacebookId() {
        return facebookId;
    }

    public List<Wishlist> getWishlists() {
        return wishlists;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setVkId(int vkId) {
        this.vkId = vkId;
    }

    public String getVkToken() {
        return vkToken;
    }

    public void setVkToken(String vkToken) {
        this.vkToken = vkToken;
    }

    public void setFacebookId(int facebookId) {
        this.facebookId = facebookId;
    }

    public String getFacebookToken() {
        return facebookToken;
    }

    public void setFacebookToken(String facebookToken) {
        this.facebookToken = facebookToken;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }

    public List<Account> getFriends() {
        return friends;
    }

    public void setFriends(List<Account> friends) {
        this.friends = friends;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }
}
