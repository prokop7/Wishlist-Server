package server.resources;

import server.model.Item;

public class ItemResource {
    private String name;
    private String description;
    private String price;
    private String link;

    public ItemResource(Item item) {
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
}
