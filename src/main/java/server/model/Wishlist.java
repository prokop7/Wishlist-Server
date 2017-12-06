package server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Wishlist {

    @Id
    @GeneratedValue
    private int id;
    private String name;
    private short visibility;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Account> users = new ArrayList<>();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
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
}
