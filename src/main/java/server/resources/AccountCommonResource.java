package server.resources;

import com.fasterxml.jackson.annotation.JsonIgnore;
import server.model.Account;

public class AccountCommonResource {
    private int id;
    private String name;
    private String photoLink;
    private Integer vkId;
    private boolean registered;

    public AccountCommonResource(Account account) {
        this.id = account.getId();
        this.name = account.getUsername();
        this.photoLink = account.getPhotoLink();
        this.vkId = account.getVkId();
        this.registered = account.isRegistered();
    }

    //Only for Controllers
    public AccountCommonResource() {}

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getVkId() {
        return vkId;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public boolean isRegistered() {
        return registered;
    }
}
