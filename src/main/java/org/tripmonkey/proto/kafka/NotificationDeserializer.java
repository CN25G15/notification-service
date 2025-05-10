package org.tripmonkey.proto.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.kafka.common.serialization.Deserializer;
import org.tripmonkey.notification.service.Notification;

public class NotificationDeserializer implements Deserializer<Notification> {

    public Notification deserialize(String topic, byte[] data) {
        try {
            return Notification.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            return null;
        }
    }

}
