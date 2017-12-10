package server.controller.parsers;

import com.google.gson.annotations.SerializedName;
import com.vk.api.sdk.objects.users.User;

import java.util.List;
import java.util.Objects;

public class FriendsResponse {
    /**
     * Total friends number
     */
    @SerializedName("count")
    private Integer count;

    @SerializedName("items")
    private List<User> items;

    public Integer getCount() {
        return count;
    }

    public List<User> getItems() {
        return items;
    }

    @Override
    public int hashCode() {
        return Objects.hash(count, items);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendsResponse friendsResponse = (FriendsResponse) o;
        return Objects.equals(count, friendsResponse.count) &&
                Objects.equals(items, friendsResponse.items);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GetResponse{");
        sb.append("count=").append(count);
        sb.append(", items=").append(items);
        sb.append('}');
        return sb.toString();
    }
}
