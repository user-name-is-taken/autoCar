// https://www.youtube.com/watch?v=fE3Dw0slhIc
#include <regex>
#include <Wire.h> 
// the serial library?
#include <Adafruit_MotorShield.h>
#include "MotorShieldv2Lib.h"
#include <SoftwareSerial.h>
#include <Wire.h>
#include<unordered_map>

using namespace std;

//https://stackoverflow.com/questions/15733163/c-error-unordered-map-does-not-name-a-type

//SoftwareSerial MotorShield::ser;

const String MotorShield::SHIELD_PATTERN_START = "^MSv2_\d{2,2}_";
const String MotorShield::SPEED_PATTERN = "speed_[1,2,3,4]_\d{0,5}$";
const String MotorShield::DIR_PATTERN = "direction_[1,2,3,4]_[0,1,2]$";

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
static boolean MotorShield::checkMessage(String message){
  regex MotorShieldPattern("MSv2_\d{2,2}_(speed_[1,2,3,4]_\d{0,5}|direction_[1,2,3,4]_[0,1,2])");
  // note, there are a lot more of these patterns. You'll have to define them later, but this is the idea.
    // motor shield signals are of the format "MSv2_shield number_then the command"
  //https://www.geeksforgeeks.org/regex-regular-expression-in-c/
  // C++ regex uses the ecmaScript standard by default, http://www.softmake.com.au/regularExpressionTester
  // Official regex docs http://www.cplusplus.com/reference/regex/basic_regex/
  return regex_match(MotorShieldPattern, message);
}
