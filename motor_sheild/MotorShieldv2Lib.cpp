#include <Regexp.h>
//from here: https://github.com/nickgammon/Regexp installed in computer's Arduino library, so you'll have to do this

// https://www.youtube.com/watch?v=fE3Dw0slhIc - arduino libraries
#include <Wire.h> 
// the serial library?
#include <Adafruit_MotorShield.h>
#include "MotorShieldv2Lib.h"


static const char SHIELD_PATTERN_START [] = "^MSv2_[67]%x_";
static const char SPEED_PATTERN [] = "^MSv2_[67]%x_speed_[1-4]_%x%x";
//make sure you send hex bytes!
static const char DIR_PATTERN [] = "^MSv2_[67]%x_direction_[1-4]_[0-2]";

static Adafruit_MotorShield *shields [32];
// Initialized as all null
//https://stackoverflow.com/questions/2615071/c-how-do-you-set-an-array-of-pointers-to-null-in-an-initialiser-list-like-way
  // the above link described this initialization
  // shields holds pointer to the shield objects.
  // shields are addressed 0x60 to 0x7F for a total of 32 unique addresses.
  // In this array, [0] == address 0x60, [31] == address 0x7F

/*
 * converts a substring between A and B from message to a uint8_t
 * 
 * http://www.cplusplus.com/reference/cstring/strncpy/
 * 
 * Tested, it works
 */
uint8_t substr2num(char *message, int A, int B){
  char str[(B - A) + 1];
  strncpy(str, message + A, B - A);
  str[B-A] = '\0';
  return strtol(str, NULL, 16);
}

/*
 * Converts the message from the Serial port to its shield's int location 
 * in the shields array.
 * 
 * If a motor shield doesn't exist, it creates it before returning the int
 * 
 * Note: 0x70 is the broadcast
 * 
 * //https://learn.adafruit.com/adafruit-motor-shield-v2-for-arduino/stacking-shields
 */
int getMotorShield(char *message){
// * https://stackoverflow.com/questions/45632093/convert-char-to-uint8-t-array-with-a-specific-format-in-c
// the above might help with the conversion

//pointers: https://stackoverflow.com/questions/28778625/whats-the-difference-between-and-in-c
   char addr = substr2num(message, 5,7);//make sure this is the right length
   //MSv2_60_speed_1_10
   if(addr < 96 || addr > 127){
     return -1;
   }

   if(!shields[addr - 96]){//makes sure it's a null pointer before setting it
    //This describes the pointer magic here:
      //https://stackoverflow.com/questions/5467999/c-new-pointer-from-pointer-to-pointer#5468009
      shields[addr - 96] = new Adafruit_MotorShield;
      *shields[addr - 96] = Adafruit_MotorShield(addr);
      shields[addr - 96]->begin();
   }
   return (int)(addr - 96);
};

/*
 * gets the motor, then sets the speed. Speed is between 00 (0) and FF (255)
 * 
 * pattern: ^MSv2_[67][0-9A-Fa-f]_speed_[1-4]_[0-9a-fA-F]{2,2}$
 *   - example: MSv2_60_speed_1_10
 */
boolean setMotorSpeed(char *message, Adafruit_MotorShield shield){
   uint8_t motorAddr = substr2num(message, 14, 15);//make sure this is the right length

   uint8_t intSpeed = substr2num(message, 16, 18);//make sure this is the right length
   
   shield.getMotor(motorAddr)->setSpeed(intSpeed);
   
   return true;
}

/*
 * gets the motor, then sets the direction
 * 
 * see here: https://learn.adafruit.com/adafruit-motor-shield-v2-for-arduino/library-reference#void-run-uint8-t-9-7
 * 
 * DIR_PATTERN: ^MSv2_[67][0-9A-Fa-f]_direction_[1-4]_[0-2]$
 *   - example: MSv2_60_direction_1_1
 */
boolean setMotorDir(char *message, Adafruit_MotorShield shield){
   uint8_t motorAddr = substr2num(message, 18,19);//make sure this is the right length
   
   if(message[20] == '0'){
     shield.getMotor(motorAddr)->run(RELEASE); 
   }else if (message[20] == '1'){
     shield.getMotor(motorAddr)->run(FORWARD);
   }else if (message[20] == '2'){
     shield.getMotor(motorAddr)->run(BACKWARD);
   }else{
    return false;
   }
   return true;
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
 *   
 *   Remember, you NEED to de-reference toWrite with this: https://stackoverflow.com/questions/2229498/passing-by-reference-in-c
 
*/
boolean checkMotorShieldMessage(char *message, String *toWrite){
  MatchState ms;
  ms.Target(message);
  Serial.println(message);
  char isForShield = ms.Match(SHIELD_PATTERN_START);//check if the message is for the shield
  // converting to char array: https://www.arduino.cc/reference/en/language/variables/data-types/string/functions/tochararray/
  // regex from: https://github.com/nickgammon/Regexp also see the installed examples
  if(isForShield > 0){
    //parse out which shield, set it as a variable
    Serial.println("match");//only works on the first one?
    int shieldInt = getMotorShield(message);
    if(shieldInt < 0){
       //set toWrite to an error message saying this isn't a valid number
       *toWrite = String("MotorShield: That isn't a valid shield address." + String(message));
    }else{
      if(ms.Match(SPEED_PATTERN) > 0){
        //parse out params
        //set speed on the shield
        if(setMotorSpeed(message, *shields[shieldInt])){
          *toWrite = String("MotorShield: speed set success.");  
        }else{
          *toWrite = String("MotorShield: speed set fail.");
        }
      }else if(ms.Match(DIR_PATTERN) > 0){
        //set direction
        if(setMotorDir(message, *shields[shieldInt])){
          *toWrite = String("MotorShield: direction set success.");
        }else{
          *toWrite = String("MotorShield: direction set failed.");
        }
      //ADD OTHER STUFF (SET SERVOS...)
        // note, people can put crap between the SHIELD_PATTERN_START and the parameter patterns, but this isn't really a problem
      }else{
        *toWrite = String("MotorShield: No matching command found.");
      }
    }
    delete message;
    return true;
  }else{
    return false;
  }
}
