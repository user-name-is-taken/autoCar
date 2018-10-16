#include <Regexp.h>
//from here: https://github.com/nickgammon/Regexp installed in computer's Arduino library, so you'll have to do this

// https://www.youtube.com/watch?v=fE3Dw0slhIc
#include <Wire.h> 
// the serial library?
#include <Adafruit_MotorShield.h>
#include "MotorShieldv2Lib.h"
#include <SoftwareSerial.h>

//SoftwareSerial MotorShield::ser;

const char MotorShield::SHIELD_PATTERN_START [] = "^MSv2_[0-9]{2,2}_";
const char MotorShield::SPEED_PATTERN [] = "speed_[1-4]_[0-9]{0,5}$";
const char MotorShield::DIR_PATTERN [] = "direction_[1-4]_[0-2]$";

const MotorShield *MotorShield::shields [32] = {};// should be all null
//https://stackoverflow.com/questions/2615071/c-how-do-you-set-an-array-of-pointers-to-null-in-an-initialiser-list-like-way
  // the above link described this initialization
  // shields holds the shields.
  // shields are addressed 0x60 to 0x7F for a total of 32 unique addresses.
  // In this array, [0] == address 0x60, [31] == address 0x7F

/*
 * This creates a MotorShield class.
 * Motor shield
https://stackoverflow.com/questions/1563897/c-static-constant-string-class-member - defining these static variables
*/
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
 * motor shield signals are of the format "MSv2_shield number_then the command_parameters"
 * see the constants at the top for the commands
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
  char isForShield = ms.Match(MotorShield::SHIELD_PATTERN_START);//check if the message is for the shield
  // converting to char array: https://www.arduino.cc/reference/en/language/variables/data-types/string/functions/tochararray/
  // regex from: https://github.com/nickgammon/Regexp also see the installed examples
  if(isForShield > 0){
    //parse out which shield, set it as a variable
    if(ms.Match(MotorShield::SPEED_PATTERN) > 0){
      //parse out params
      //set speed on the shield
      return true;
    }else if(ms.Match(MotorShield::DIR_PATTERN) > 0){
      //set direction
      return true;
    //ADD OTHER STUFF (SET SERVOS...)
      // note, people can put crap between the SHIELD_PATTERN_START and the parameter patterns, but this isn't really a problem
    }else{
      //probably throw an error, because nothing else will match this
      return false;
    }
  }else{
    return false;
  }
}
