package com.iot.messaging.transformer;

import com.iot.model.rpicomponent.data.HumidityDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.support.json.Jackson2JsonObjectMapper;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

@MessageEndpoint
public class HumidityMessageTransformer {

    @Autowired
    private Jackson2JsonObjectMapper jackson2JsonObjectMapper;

    @Transformer(inputChannel = "humidityTransformerChannel", outputChannel = "humidityServiceChannel")
    public Message<HumidityDTO> transform(Message<String> message) throws Exception {
        String payload = message.getPayload();
        HumidityDTO data = jackson2JsonObjectMapper.fromJson(payload, HumidityDTO.class);
        return MessageBuilder.createMessage(data, message.getHeaders());
    }

}
