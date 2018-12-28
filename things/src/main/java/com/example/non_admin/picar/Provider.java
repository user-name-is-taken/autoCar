package com.example.non_admin.picar;

public abstract class Provider extends ArduinoAPI {

    public Provider(Device dev){
        super(dev);
    }

    /**
     * Provides data to the appropriate place.
     * This will be called in the receive method after it validates the message.
     *
     * Not sure if I'll write the code that processes data here?
     */
    abstract void provide(String data);
}
