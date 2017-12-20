package server.resources;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.validator.constraints.NotBlank;
import server.model.Item;

//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemResource {
    private int id;

    @NotBlank(message = "Name must not be blank!")
    private String name;
    private String description;
    private String price;
    private String link;
    private int state;
    @JsonIgnore
    private AccountCommonResource taker;

    public ItemResource(Item item) {
        this.id = item.getId();
        this.name = item.getName();
        this.description = item.getDescription();
        this.price = item.getPrice();
        this.link = item.getLink();
        this.state = item.getState();
        if (item.getTaker() != null)
            this.taker = new AccountCommonResource(item.getTaker());
    }

    public ItemResource() {
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getLink() {
        return link;
    }

    public int getId() {
        return id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {this.state = state;}

    public AccountCommonResource getTaker() {
        return taker;
    }
}
