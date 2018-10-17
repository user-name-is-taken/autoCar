#include <Regexp.h>
//from here: https://github.com/nickgammon/Regexp installed in computer's Arduino library, so you'll have to do this

// https://www.youtube.com/watch?v=fE3Dw0slhIc
#include <Wire.h> 
// the serial library?
#include <Adafruit_MotorShield.h>
#include "MotorShieldv2Lib.h"
#include <SoftwareSerial.h>


static const char SHIELD_PATTERN_START [] = "^MSv2_[67][0-9A-Fa-f]_";
static const char SPEED_PATTERN [] = "speed_[1-4]_[0-9]{0,5}$";
static const char DIR_PATTERN [] = "direction_[1-4]_[0-2]$";

static const Adafruit_MotorShield *shields [32] = {};
// Initialized as all null
//https://stackoverflow.com/questions/2615071/c-how-do-you-set-an-array-of-pointers-to-null-in-an-initialiser-list-like-way
  // the above link described this initialization
  // shields holds pointer to the shield objects.
  // shields are addressed 0x60 to 0x7F for a total of 32 unique addresses.
  // In this array, [0] == address 0x60, [31] == address 0x7F
  // note, static in this context means the array's pointer can't change, the array values can


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
 *   
 *   Remember, you NEED to de-reference toWrite with this: https://stackoverflow.com/questions/2229498/passing-by-reference-in-c
 
*/
boolean checkMotorShieldMessage(String message, String *toWrite){
  MatchState ms;
  char buf [message.length()];
  message.toCharArray(buf, message.length());
  ms.Target(buf);
  char isForShield = ms.Match(SHIELD_PATTERN_START);//check if the message is for the shield
  // converting to char array: https://www.arduino.cc/reference/en/language/variables/data-types/string/functions/tochararray/
  // regex from: https://github.com/nickgammon/Regexp also see the installed examples
  if(isForShield > 0){
    //parse out which shield, set it as a variable
    if(ms.Match(SPEED_PATTERN) > 0){
      //parse out params
      //set speed on the shield
      return true;
    }else if(ms.Match(DIR_PATTERN) > 0){
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

/*
 * Converts the message from the Serial port to its corresponding motor
 * 

 */
Adafruit_MotorShield getMotorShield(String message){
// * https://stackoverflow.com/questions/45632093/convert-char-to-uint8-t-array-with-a-specific-format-in-c
// the above might help with the conversion
//https://learn.adafruit.com/adafruit-motor-shield-v2-for-arduino/stacking-shields
//Note: 0x70 is the broadcast
   String shieldAddress = message.substring(5,7);//make sure this is the right length
   char carr [2];
   shieldAddress.toCharArray(carr, 2);
   uint8_t addr = strtol(carr, NULL, 16);
   //check if it exists in shields:
      //if not, create it
      //else return it.
   return Adafruit_MotorShield(addr); 
}
