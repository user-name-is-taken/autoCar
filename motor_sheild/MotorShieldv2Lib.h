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

//pass


using namespace std;

class MotorShield{
  Adafruit_MotorShield AFMS;// need to make a .begin for this?
  private:
    Stream *ser;

    
    //static unordered_map<string, MotorShield> shields;// holds all the shields
    //https://www.geeksforgeeks.org/unordered_map-at-cpp/ - unordered map use
    //http://www.cplusplus.com/reference/unordered_map/unordered_map/ - unordered map docs
  // see this for why ser must be a pointer https://stackoverflow.com/questions/4296276/c-cannot-declare-field-to-be-of-abstract-type
  public:
    MotorShield(String address, Stream *ptrSer);
    static boolean checkMessage(String message);
    static const char SHIELD_PATTERN_START [];
    static const char SPEED_PATTERN [];
    static const char DIR_PATTERN [];
    
    
};


#endif
