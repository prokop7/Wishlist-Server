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
    public long id;
    public String description;

    @ManyToOne
    @JsonIgnore
    public Wishlist wishlist;

    public Item(String description, Wishlist wishlist) {
        this.description = description;
        this.wishlist = wishlist;
    }

    public Item() {

    }
}
