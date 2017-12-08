package server.resources;

import org.hibernate.validator.constraints.NotBlank;
import server.model.Item;

public class ItemResource {
    private int id;

    @NotBlank(message = "Name must not be blank!")
    private String name;
    private String description;
    private String price;
    private String link;

    public ItemResource(Item item) {
        this.id = item.getId();
        this.name = item.getName();
        this.description = item.getDescription();
        this.price = item.getPrice();
        this.link = item.getLink();
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
}
