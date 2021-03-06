package com.iot.messaging.service;

import com.iot.model.rpicomponent.AbstractRPiComponent;
import com.iot.model.rpicomponent.component.HumiditySensor;
import com.iot.model.rpicomponent.data.HumidityDTO;
import com.iot.model.rpicomponent.data.HumidityData;
import com.iot.repository.data.HumidityDataRepository;
import com.iot.repository.rpicomponent.RPiComponentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HumidityPersistenceService {

    @Autowired
    private RPiComponentRepository rPiComponentRepository;

    @Autowired
    private HumidityDataRepository humidityDataRepository;

    @ServiceActivator(inputChannel = "humidityServiceChannel")
    public void process(Message<HumidityDTO> message) {
        HumidityDTO payload = message.getPayload();
        Optional<AbstractRPiComponent> component = rPiComponentRepository.findById(payload.getComponentId());
        if (component.isPresent()) {
            HumidityData data = new HumidityData();
            data.setHumidity(payload.getHumidity());
            data.setTimestamp(payload.getTimestamp());
            data.setComponent((HumiditySensor) component.get());
            humidityDataRepository.save(data);
        }
    }

}
