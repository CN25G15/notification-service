package org.tripmonkey.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.mutiny.Multi;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Channel;

import org.jboss.logging.Logger;
import org.tripmonkey.notification.service.Notification;

@Path("/notifications/{uuid}")
public class NotificationsEndpoint {

    @Inject
    Logger log;

    @Inject
    @Channel("notifications-service")
    Multi<Notification> workspace_service;

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public Multi<String> feedNotifications(@PathParam("uuid") String uuid) {
        log.infof("Initialized SSE session for user %s", uuid);
        return workspace_service.onItem().transform(notification -> {
                    log.infof("Received notification %s", notification);
                    return notification;
                })
                .filter(workspacePatch ->
                        workspacePatch.getUsersList().stream().anyMatch(user -> user.getUserId().equals(uuid)))
                .map(workspacePatch -> {
                    try {
                        return JsonFormat.printer().print(workspacePatch.getAction());
                    } catch (InvalidProtocolBufferException e) {
                        throw new RuntimeException(e);
                    }
                }).onFailure().recoverWithItem("Internal server error");
    }

}
