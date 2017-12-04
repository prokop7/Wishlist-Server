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
    private String description;

    @ManyToOne
    @JsonIgnore
    private Wishlist wishlist;

    public Item() {}

    public Item(String description, Wishlist wishlist) {
        this.description = description;
        this.wishlist = wishlist;
    }

}
