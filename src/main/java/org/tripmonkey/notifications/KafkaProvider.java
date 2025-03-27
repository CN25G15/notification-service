package org.tripmonkey.notifications;

import io.smallrye.mutiny.Multi;
import org.tripmonkey.model.data.Notification;

public class KafkaProvider implements NotificationsProvider {

    @Override
    public Multi<Notification> poll() {
        return null;
    }

}
