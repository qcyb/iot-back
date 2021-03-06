package com.iot.messaging.gateway;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.Message;

@MessagingGateway
public interface MqttOutboundGateway {

    @Gateway(requestChannel = "mqttOutboundChannel")
    void send(Message<String> message);
}
