package com.iot.web.controller;

import com.iot.model.NotFoundException;
import com.iot.model.dto.RPiComponentDTO;
import com.iot.model.dto.RpiPinDto;
import com.iot.model.rpi.RPi;
import com.iot.model.rpicomponent.AbstractRPiComponent;
import com.iot.model.rpicomponent.RPiComponentType;
import com.iot.model.rpicomponent.component.HumiditySensor;
import com.iot.model.rpicomponent.component.MoistureSensor;
import com.iot.model.rpicomponent.component.Relay;
import com.iot.model.rpicomponent.component.TemperatureSensor;
import com.iot.model.rpicomponent.exception.InvalidRPiComponentTypeException;
import com.iot.model.rpicomponent.exception.RPiComponentNotFoundException;
import com.iot.model.rpipin.RPiPin;
import com.iot.repository.rpi.RPiRepository;
import com.iot.repository.rpicomponent.RPiComponentRepository;
import com.iot.repository.rpicomponent.RPiPinRepository;
import com.iot.utils.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping(value = "/api/component")
public class RPiComponentController {

    @Autowired
    private RPiComponentRepository rPiComponentRepository;

    @Autowired
    private RPiRepository rPiRepository;

    @Autowired
    private RPiPinRepository rPiPinRepository;

    @Value("${exception.rpi-component-not-found}")
    private String componentNotFoundException;

    @Value("${exception.invalid-rpi-component-type}")
    private String invalidComponentType;

    @GetMapping
    public Iterable<AbstractRPiComponent> getAllComponents() {
        return rPiComponentRepository.findAll();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public AbstractRPiComponent postOneComponent(@RequestBody RPiComponentDTO component) {
        AbstractRPiComponent abstractRPiComponent = null;
        Optional<RPi> rpi;
        switch (component.getType()) {
            case TEMPERATURE:
                abstractRPiComponent = new WebUtils<>(TemperatureSensor.class).convertToObject(component);
                break;
            case MOISTURE:
                abstractRPiComponent = new WebUtils<>(MoistureSensor.class).convertToObject(component);
                break;
            case HUMIDITY:
                abstractRPiComponent = new WebUtils<>(HumiditySensor.class).convertToObject(component);
                break;
            case RELAY:
                abstractRPiComponent = new WebUtils<>(Relay.class).convertToObject(component);
        }
        rpi = rPiRepository.findById(component.getRpiId());
        if (abstractRPiComponent != null) {
            abstractRPiComponent.setRpi(rpi.orElseThrow(() -> new NotFoundException("RPi " + component.getRpiId() + " not found")));
        }
        Optional<RPiPin> rPiPin;
        AbstractRPiComponent savedAbstractRPiComponent = rPiComponentRepository.save(abstractRPiComponent);
        RpiPinDto rpiPinDto = component.getPin();
        if(rpiPinDto!=null){
            RPiPin newRpiPin = new WebUtils<>(RPiPin.class).convertToObject(rpiPinDto);
            newRpiPin.setComponent(savedAbstractRPiComponent);
            newRpiPin.setUsable(true);
            newRpiPin.setPhysicalPin(rpiPinDto.getNumber());
            newRpiPin.setRpi(rpi.orElseThrow(() -> new NotFoundException("RPi " + component.getRpiId() + " not found")));
            rPiPin = Optional.of(newRpiPin);
            rPiPinRepository.save(rPiPin.get());
        }
        return savedAbstractRPiComponent;
    }

    @GetMapping(value = "/{id}")
    public AbstractRPiComponent getOneComponent(@PathVariable("id") Integer id) {
        return validateComponent(id);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public void updateOneComponent(@RequestBody AbstractRPiComponent component) {
        rPiComponentRepository.save(component);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public AbstractRPiComponent deleteOneComponent(@PathVariable Integer id) {
        AbstractRPiComponent component = validateComponent(id);
        rPiComponentRepository.deleteById(id);
        return component; //returns deleted component
    }

    @GetMapping(value = "/byType")
    public List<AbstractRPiComponent> getComponentsByType(@RequestParam(value = "type", required = true) String type) {
        RPiComponentType componentType = validateComponentType(type);
        return rPiComponentRepository.findAllByType(componentType);
    }


    private AbstractRPiComponent validateComponent(Integer id) {
        Optional<AbstractRPiComponent> component = rPiComponentRepository.findById(id);
        if (!component.isPresent())
            throw new RPiComponentNotFoundException(componentNotFoundException);
        else
            return component.get();
    }

    private RPiComponentType validateComponentType(String type) {
        RPiComponentType temp = RPiComponentType.from(type);
        if (temp == null)
            throw new InvalidRPiComponentTypeException(invalidComponentType);
        return temp;
    }

}
