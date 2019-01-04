package com.example.non_admin.picar;

/**
 * This class will handle the logic for forwarding data from providers to other APIs
 * All "brains" goes here.
 * This will probably have a lot of Async crap happening.
 * Most of these methods will be static.
 */
public class RaspberryPi {
    //pass
    public static void deviceAdded(Device dev){
        if(dev.getName().contentEquals("motors")){
            MSv2 myArduino = (MSv2) dev.getAPI("MSv2");
            myArduino.setShield(0);
            MSv2Motors motor1 = myArduino.getShield(0).setDCMotor(1);
            MSv2Steppers step2 = myArduino.getShield(0).setStepperMotor(2);

            motor1.setSpeed(100);
            step2.setMoveAmount(100, 0);
            motor1.setDirection(true);
            myArduino.executeGroup(0);
        }

    }
}
