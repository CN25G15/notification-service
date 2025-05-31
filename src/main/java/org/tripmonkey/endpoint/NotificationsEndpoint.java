package org.tripmonkey.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.BaseUnits;
import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Channel;

import org.jboss.logging.Logger;
import org.tripmonkey.notification.service.Notification;

import java.util.concurrent.atomic.AtomicInteger;

@Path("/notifications/{uuid}")
public class NotificationsEndpoint {

    @Inject
    Logger log;

    @Inject
    MeterRegistry registry;

    @Inject
    @Channel("notifications-service")
    Multi<Notification> workspace_service;

    AtomicInteger totalClients = new AtomicInteger(0);

    @PostConstruct
    void initMetrics(){
        Gauge.builder("SSE.subscribed.clients", totalClients::get)
                .baseUnit(BaseUnits.SESSIONS)
                .description("The number of users actively listening for events")
                .register(registry);
    }

    @Counted(value = "total.subscriptions", extraTags = {"integer","totalCounter"})
    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public Multi<String> feedNotifications(@PathParam("uuid") String uuid) {
        totalClients.getAndIncrement();
        log.infof("Initialized SSE session for user %s", uuid);
        return workspace_service.runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
                .onItem().transform(notification -> {
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
                }).onFailure().recoverWithItem("Internal server error")
                .onTermination().invoke(() -> totalClients.getAndDecrement());
    }

}
