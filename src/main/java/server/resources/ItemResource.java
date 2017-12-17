package server.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.validator.constraints.NotBlank;
import server.model.Item;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemResource {
    private int id;

    @NotBlank(message = "Name must not be blank!")
    private String name;
    private String description;
    private String price;
    private String link;
    private boolean taken;
    private boolean presented;

    public ItemResource(Item item) {
        this.id = item.getId();
        this.name = item.getName();
        this.description = item.getDescription();
        this.price = item.getPrice();
        this.link = item.getLink();
        this.taken = item.isTaken();
        this.presented = item.isPresented();
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

    public boolean isTaken() {
        return taken;
    }

    public void setTaken(boolean taken) {
        this.taken = taken;
    }

    public boolean isPresented() {
        return presented;
    }

    public void setPresented(boolean presented) {
        this.presented = presented;
    }
}
