package com.example.non_admin.picar;

import com.example.non_admin.picar.ArduinoAPI;

public class MSv2Steppers extends ArduinoAPI {
    @Override
    boolean receive(String message) {
        return false;
    }
}
