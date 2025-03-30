package org.tripmonkey.notifications;

import io.smallrye.mutiny.Multi;
import org.jboss.logging.Logger;
import org.tripmonkey.model.data.Notification;
import org.tripmonkey.model.mock.Garbage;

import java.time.Duration;
import java.util.Random;
import java.util.UUID;

public class MockProvider implements NotificationsProvider{

    private static Logger logger = Logger.getLogger(MockProvider.class);
    private static Random r = new Random();

    UUID[] users = new UUID[]{
            UUID.fromString("58e157ac-cfc6-4753-949f-44a5a643b358"),
            UUID.fromString("cf14a177-a6a3-4686-9b9b-bcf71d17895a"),
            UUID.fromString("85eb1484-d554-4c5f-b36c-8d7db9e8e889"),
            UUID.fromString("e0c7ac61-c034-42f0-885c-32b43f4885d2"),
            UUID.fromString("b0333080-66f5-48ad-be82-c4d0a82f7e9d"),
    };

    @Override
    public Multi<Notification> poll() {
        return Multi.createFrom()
                .ticks().every(Duration.ofMillis(1500))
                .onOverflow().buffer(500) // Store up to
                .onOverflow()/*.call(Inform kubernetes to launch another instance)*/.drop()
                .map(tick -> {
                    UUID user = users[r.nextInt(users.length-1)];
                    logger.infof("Received notification for user %s", user.toString());
                    return new Notification<Garbage>(
                            users[r.nextInt(users.length-1)],
                            new Garbage(user)
                    );
                });
    }
}
