package org.tripmonkey.model.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.json.stream.JsonParsingException;

import java.util.UUID;

public class Notification<Content> {

    @JsonIgnore
    UUID userUuid;

    @JsonProperty("notification")
    Content notifData;

    public Notification(UUID uuid, Content data){
        this.userUuid = uuid;
        this.notifData = data;
    }

    public boolean shouldSend(UUID client){
        return userUuid.equals(client);
    }

}
