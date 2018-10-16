#include <Regexp.h>
//from here: https://github.com/nickgammon/Regexp installed in computer's Arduino library, so you'll have to do this

// https://www.youtube.com/watch?v=fE3Dw0slhIc
#include <Wire.h> 
// the serial library?
#include <Adafruit_MotorShield.h>
#include "MotorShieldv2Lib.h"
#include <SoftwareSerial.h>
//#include<unordered_map>


//https://stackoverflow.com/questions/15733163/c-error-unordered-map-does-not-name-a-type
//





//SoftwareSerial MotorShield::ser;

const String MotorShield::SHIELD_PATTERN_START = "^MSv2_[0-9]{2,2}_";
const String MotorShield::SPEED_PATTERN = "speed_[1-4]_[0-9]{0,5}$";
const String MotorShield::DIR_PATTERN = "direction_[1-4]_[0-2]$";

//https://stackoverflow.com/questions/1563897/c-static-constant-string-class-member - defining these static variables
MotorShield::MotorShield(String address, Stream *prtSer){
  // the MotorShield constructor
  ser = prtSer;
  // Create the motor shield object with the default I2C address
  AFMS = Adafruit_MotorShield(); 
  // Or, create it with a different I2C address (say for stacking)
  // Adafruit_MotorShield AFMS = Adafruit_MotorShield(0x61);    
  //AFMS.begin here?
}

/*
 * messages meant for 
 * 
 * if the message is meant for a motor shield:
 *   - If the shield doesn't exist, create it and add it to shields
 *     - If there's not a shield connected with the corresponding address, throw an error
 *   - Run the function on the right shield
 *   - return true
 * else: 
 *   - return false
 
*/
boolean MotorShield::checkMessage(String message){
  MatchState ms;
  char buf [message.length()];
  message.toCharArray(buf, message.length());
  ms.Target(buf);
  char result = ms.Match("^MSv2_[0-9]{2,2}_(speed_[1-4]_[0-9]{0,5}|direction_[1-4]_[0-2])");
  // note, there are a lot more of these patterns. You'll have to define them later, but this is the idea.
    // motor shield signals are of the format "MSv2_shield number_then the command"

  // converting to char array: https://www.arduino.cc/reference/en/language/variables/data-types/string/functions/tochararray/
  // regex from: https://github.com/nickgammon/Regexp also see the installed examples
  return result != 0;
}
