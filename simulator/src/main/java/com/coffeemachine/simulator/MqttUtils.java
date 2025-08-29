package com.coffeemachine.simulator;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

public class MqttUtils {
    public static MqttConnectOptions getOptions(String username, String password) {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setCleanSession(true);
        return options;
    }
}
