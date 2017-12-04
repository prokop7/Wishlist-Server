package server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Wishlist {

    @Id
    @GeneratedValue
    public long id;
    public String name;

    @JsonIgnore
    @ManyToOne
    public Account account;

    @OneToMany(mappedBy = "wishlist")
    public List<Item> items = new ArrayList<>();

    public Wishlist(String name, Account Account) {
        this.name = name;
        this.account = Account;
    }

    public Wishlist() {
    }

    public String getName() {
        return name;
    }

    public Account getAccount() {
        return account;
    }
}
