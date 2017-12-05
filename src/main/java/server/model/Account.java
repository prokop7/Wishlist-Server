package server.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table (name = "Accounts")
public class Account {

    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy="increment")
    private long id;

    private String username;
    private long vkId;
    private String vkToken;
    private long facebookId;
    private String facebookToken;
    private String photoLink;

    @OneToMany(mappedBy = "account")
    private List<Wishlist> wishlists = new ArrayList<>();

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

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }

    public String getPhotoLink() {
        return photoLink;
    }
}
