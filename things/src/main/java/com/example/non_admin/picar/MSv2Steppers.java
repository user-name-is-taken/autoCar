package com.example.non_admin.picar;

import com.example.non_admin.picar.ArduinoAPI;

public class MSv2Steppers extends ArduinoAPI {
    @Override
    boolean receive(String message) {
        return false;
    }

    /**
     * constructor
     * @param dev
     */
    public MSv2Steppers(Device dev){
        super(dev);
    }

    /**
     * example: MSv2Steppers_60_move_1_+0FF_group_00
     * @param ticks
     * @param group
     * @param shield
     */
    public void setMoveAmount(int ticks, int group, int shield){
        String tickAmount;
        if(ticks > 4095){
            //invalid  number of ticks
            //throw error
        }
        if(ticks < 0){
            ticks *= -1;
            tickAmount = "-" + intToHex(ticks);
        }else{
            tickAmount = "+" + intToHex(ticks);
        }
        if(group >= 16 || group < 0){
            //invalid group
            //throw error
        }
        if(shield < 0 || shield > 32){
            throw new InvalidShield("The number " + shield + "isn't a valid shield");
        }
        String toSend = this.name + "_" + intToHex(96 + shield) + "_move_" +
                tickAmount + "_group_" + intToHex(group);

        this.dev.send(toSend);
    }

}