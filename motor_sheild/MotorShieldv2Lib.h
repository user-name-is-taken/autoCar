#ifndef MotorShieldv2lib
#define MotorShieldv2lib

#if (ARDUINO >=100)
  #include <arduino.h>
#else
  #include "WProgram.h"
#endif
 
#include <Wire.h> 
// the serial library?
#include <Adafruit_MotorShield.h>

class MotorShield{
  public:
    MotorShield(String name);

    boolean checkMessage(String message);

  
};


#endif
