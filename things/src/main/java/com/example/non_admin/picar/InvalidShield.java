package com.example.non_admin.picar;

public class InvalidShield extends IndexOutOfBoundsException {
    public InvalidShield(String message){
        super(message);
    }
}
