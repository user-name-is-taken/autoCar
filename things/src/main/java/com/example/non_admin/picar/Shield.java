package com.example.non_admin.picar;

import java.util.HashMap;

public class Shield {

    private Boolean[] direction = {true, true, true, true};
    private int[] speed = {0,0,0,0};
    private String name;

    public Shield(String name){
        setName(name);
    }



    private void setName(String name){
        this.name = name;
    }

    /**
     * sets the direction for this motor, on this shield.
     * @param motor
     * @param direction
     */
    public void setDirection(int motor, Boolean direction) {
        if(motor < 4){
            this.direction[motor] = direction;
        }else{
            //throw error
        }
    }

    /**
     * gets the direction for a motor on this shield
     * @param motor
     * @return
     */
    public boolean getDirection(int motor) {
        if(motor < 4) {
            return this.direction[motor];
        }else{
            //throw error
            return false;
        }
    }
    /**
     * sets the speed for this motor, on this shield.
     * @param motor
     * @param speed
     */
    public void setSpeed(int motor, int speed) {
        this.speed[motor] = speed;
    }

    /**
     * gets the speed for a motor on this shield
     * @param motor
     * @return
     */
    public int getSpeed(int motor) {
        if(motor < 4) {
            return this.speed[motor];
        }else{
            //throw error
            return 0;
        }
    }
}