package server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
public class Item {

    @Id
    @GeneratedValue(generator="itemIncrement")
    @GenericGenerator(name="itemIncrement", strategy="increment")
    private int id;
    private String name;
    private String description;
    private String link;
    private String price;
    private int state; // 0 - basic, 1 - taken, 2 - presented
    private int itemOrder;
    private boolean active = true;

    @ManyToOne
    @JsonIgnore
    private Wishlist wishlist;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Account taker;

    public Item() {}

    public Item(String name){
        this.name = name;
    }

    public Item(String name, Wishlist wishlist) {
        this.name = name;
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

    public Account getTaker() {
        return taker;
    }

    public void setTaker(Account taker) {
        this.taker = taker;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getItemOrder() {
        return itemOrder;
    }

    public void setItemOrder(int itemOrder) {
        this.itemOrder = itemOrder;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
