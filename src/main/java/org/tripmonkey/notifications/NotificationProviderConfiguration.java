package org.tripmonkey.notifications;

import io.quarkus.arc.profile.IfBuildProfile;
import jakarta.enterprise.inject.Default;
import jakarta.ws.rs.Produces;
import org.jboss.logging.Logger;
import org.tripmonkey.endpoint.NotificationsEndpoint;

public class NotificationProviderConfiguration {

    private static Logger logger = Logger.getLogger(NotificationsEndpoint.class);

    @Produces
    @IfBuildProfile("kafka")
    public KafkaProvider getKafkaProvider(){
        logger.info("Producing a Kafka Provider for this service");
        return null;
    }

    @Produces
    @IfBuildProfile("google")
    public GooglePubSubProvider getPubSubProvider(){
        logger.info("Producing a Google Pub Sub Provider for this service");
        return null;
    }

    @Produces
    @Default
    public MockProvider getMockProvider(){
        logger.info("Producing a Mock Provider for this service");
        return new MockProvider();
    }

}
