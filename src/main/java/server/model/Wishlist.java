package server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Wishlist {

    @Id
    @GeneratedValue
    private long id;
    private String name;
    private short visibility;
    private List<Account> users;

    @JsonIgnore
    @ManyToOne
    private Account account;

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
