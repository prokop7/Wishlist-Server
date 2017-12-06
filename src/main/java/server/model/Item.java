package server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Item {

    @Id
    @GeneratedValue
    private int id;
    private String name;
    private String description;
    private String link;
    private String price;

    @ManyToOne
    @JsonIgnore
    private Wishlist wishlist;

    public Item() {}

    public Item(String description, Wishlist wishlist) {
        this.description = description;
        this.wishlist = wishlist;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    public String getPrice() {
        return price;
    }

    public Wishlist getWishlist() {
        return wishlist;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setWishlist(Wishlist wishlist) {
        this.wishlist = wishlist;
    }
}
