package server.resources;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageResource implements Resource {
    private boolean status;

    private List<String> messages;

    protected MessageResource() {}

    public MessageResource(boolean status, String message) {
        this.status = status;
        this.messages = new ArrayList<>();
        this.messages.add(message);
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void addMessage(String message) {
        this.messages.add(message);
    }


}
