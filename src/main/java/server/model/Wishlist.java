package server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Wishlist {

    @Id
    @GeneratedValue
    protected long id;
    protected String name;

    @JsonIgnore
    @ManyToOne
    protected Account account;

    @OneToMany(mappedBy = "wishlist")
    public List<Item> items = new ArrayList<>();

    protected Wishlist() {}

    public Wishlist(String name, Account Account) {
        this.name = name;
        this.account = Account;
    }

    public String getName() {
        return name;
    }

    public Account getAccount() {
        return account;
    }
}
