package server.resources;

import com.fasterxml.jackson.annotation.JsonIgnore;
import server.model.Account;

public class AccountCommonResource {
    private String name;
    private String photoLink;
    private int vkId;
    private boolean registered;

    public AccountCommonResource(Account account) {
        this.name = account.getUsername();
        this.photoLink = account.getPhotoLink();
        this.vkId = account.getVkId();
        this.registered = account.isRegistered();
    }

    //Only for Controllers
    public AccountCommonResource() {}

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
