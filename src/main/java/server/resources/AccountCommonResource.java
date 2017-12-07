package server.resources;

import com.fasterxml.jackson.annotation.JsonIgnore;
import server.model.Account;

public class AccountCommonResource {
    @JsonIgnore
    private final Account account;
    private String name;
    private String photoLink;
    private int vkId;

    public String getName() {
        return name;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public int getVkId() {
        return vkId;
    }

    public AccountCommonResource(Account account) {
        this.account = account;
        this.name = account.getUsername();
        this.photoLink = account.getPhotoLink();
        this.vkId = account.getVkId();
    }

}
