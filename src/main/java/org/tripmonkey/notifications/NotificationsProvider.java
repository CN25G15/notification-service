package org.tripmonkey.notifications;

import io.smallrye.mutiny.Multi;
import org.tripmonkey.model.data.Notification;

public interface NotificationsProvider {

    Multi<Notification> poll();

}
