package server.controller.Parsers;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.vk.api.sdk.exceptions.ClientException;
import server.model.Account;

import java.io.StringReader;
import java.util.List;

public class FriendsResponseParser {
    private Gson gson = new Gson();

    public List<Account> Parse(String textResponse) throws ClientException {
        JsonReader jsonReader = new JsonReader(new StringReader(textResponse));
        JsonObject json = (JsonObject) new JsonParser().parse(jsonReader);

        JsonElement response = json;
        if (json.has("response")) {
            response = json.get("response");
        }
        try {
            FriendsResponse friendsResponse = gson.fromJson(response, FriendsResponse.class);
            FriendResponseToAccountMap map = new FriendResponseToAccountMap();
            return map.GetFrom(friendsResponse);
        } catch (JsonSyntaxException e) {
            throw new ClientException("Can't parse json response");
        }
    }
}

