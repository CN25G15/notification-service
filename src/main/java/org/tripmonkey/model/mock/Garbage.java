package org.tripmonkey.model.mock;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

public class Garbage {

    @JsonProperty("user")
    UUID target;

    @JsonProperty("message")
    String msg = "You must be the trashman because you just received a garbage instance";

    @JsonProperty("timestamp")
    LocalDateTime ts = LocalDateTime.now();

    public Garbage(UUID user){
        this.target = user;
    }

}
