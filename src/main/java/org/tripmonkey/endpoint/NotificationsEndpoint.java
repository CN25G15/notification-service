package org.tripmonkey.endpoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Multi;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.tripmonkey.notifications.NotificationsProvider;


import java.util.UUID;

@Path("/notifications/{uuid}")
public class NotificationsEndpoint {

    @Inject
    ObjectMapper om;

    private static Logger logger = Logger.getLogger(NotificationsEndpoint.class);

    @Inject
    NotificationsProvider nopro;

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public Multi<String> feedNotifications(@PathParam("uuid") String uuid) {
        logger.infof("Received a request for an SSE connection for client %s", uuid);
        try {
            UUID user = UUID.fromString(uuid);
            return nopro.poll().filter(notification -> notification.shouldSend(user)).map(notification -> {
                try {
                    return om.writeValueAsString(notification);
                } catch (JsonProcessingException e) {
                    logger.fatalf("Internal error when converting Notification for user %s", uuid);
                    throw new WebApplicationException("Notification Parsing error", Response.Status.INTERNAL_SERVER_ERROR);
                }
            }) /* 🤔.onFailure(throwable -> ) */ ;
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException("Invalid user id", Response.Status.BAD_REQUEST);
        }
    }

}
