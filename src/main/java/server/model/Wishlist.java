package server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Wishlist {

    @Id
    @GeneratedValue(generator="wishlistIncrement")
    @GenericGenerator(name="wishlistIncrement", strategy="increment")
    private int id;
    private String name;
    private short visibility;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Account> users = new ArrayList<>();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    @OneToMany(mappedBy = "wishlist", cascade = CascadeType.ALL)
    private List<Item> items = new ArrayList<>();

    protected Wishlist() {}

    public Wishlist(String name, Account Account) {
        this.name = name;
        this.account = Account;
    }
    public Wishlist(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Account getAccount() {
        return account;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVisibility(short visibility) {
        this.visibility = visibility;
    }

    public void setUsers(List<Account> users) {
        this.users = users;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public int getId() {

        return id;
    }

    public short getVisibility() {
        return visibility;
    }

    public List<Account> getUsers() {
        return users;
    }

    public List<Item> getItems() {
        return items;
    }

    public void addItem(Item item) {
        items.add(item);
        item.setWishlist(this);
    }
}
