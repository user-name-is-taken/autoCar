#ifndef MotorShieldv2lib
#define MotorShieldv2lib

#if (ARDUINO >=100)
  #include <arduino.h>
#else
  #include "WProgram.h"
#endif
 
#include <Wire.h> 
#include <SoftwareSerial.h>
// the serial library?
// hardware vs software serial https://forum.arduino.cc/index.php?topic=407633.0
// maybe you don't need serial?
#include <Adafruit_MotorShield.h>
#include "MotorShieldv2Lib.h"
#include <Wire.h>

class MotorShield{
  Adafruit_MotorShield AFMS;// need to make a .begin for this?
  Stream *ser;
  //https://stackoverflow.com/questions/4296276/c-cannot-declare-field-to-be-of-abstract-type
  public:
    MotorShield(String name, Stream *ptrSer);
    boolean checkMessage(String message);  
};


#endif
