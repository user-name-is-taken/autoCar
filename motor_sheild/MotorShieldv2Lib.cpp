// https://www.youtube.com/watch?v=fE3Dw0slhIc
#include <Wire.h> 
// the serial library?
#include <Adafruit_MotorShield.h>
#include "MotorShieldv2Lib.h"
#include <SoftwareSerial.h>
#include <Wire.h>

//SoftwareSerial MotorShield::ser;

MotorShield::MotorShield(String name, Stream *prtSer){
  // the MotorShield constructor
  ser = prtSer;

  // Create the motor shield object with the default I2C address
  AFMS = Adafruit_MotorShield(); 
  // Or, create it with a different I2C address (say for stacking)
  // Adafruit_MotorShield AFMS = Adafruit_MotorShield(0x61); 
  //AFMS.begin here?
}

boolean MotorShield::checkMessage(String message){
  // the MotorShield constructor
  return false;
}
