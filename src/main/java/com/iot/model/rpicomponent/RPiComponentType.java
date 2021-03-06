package com.iot.model.rpicomponent;

public enum RPiComponentType {
    HUMIDITY,
    TEMPERATURE_HUMIDITY,
    MOISTURE,
    TEMPERATURE,
    PROXIMITY,
    RELAY;

    public static RPiComponentType from(String type) {
        RPiComponentType result = null;

        switch (type.toLowerCase()) {
            case "temperature_humidity":
                result = TEMPERATURE_HUMIDITY;
                break;
            case "temperature":
                result = TEMPERATURE;
                break;
            case "humidity":
                result = HUMIDITY;
                break;
            case "proximity":
                result = PROXIMITY;
                break;
            case "relay":
                result = RELAY;
                break;
            case "moisture":
                result = MOISTURE;
                break;
            default:
        }

        return result;
    }

}
