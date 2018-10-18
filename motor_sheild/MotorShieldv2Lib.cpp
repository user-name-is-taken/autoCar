#include <Regexp.h>
//from here: https://github.com/nickgammon/Regexp installed in computer's Arduino library, so you'll have to do this

// https://www.youtube.com/watch?v=fE3Dw0slhIc
//#include <Wire.h> 
// the serial library?
#include <Adafruit_MotorShield.h>
#include "MotorShieldv2Lib.h"



static const char SHIELD_PATTERN_START [] = "^MSv2_[67][0-9A-Fa-f]_";//len == 8
static const char SPEED_PATTERN [] = "^MSv2_[67][0-9A-Fa-f]_speed_[1-4]_[0-9a-fA-F]{2,2}$";
//make sure you send hex bytes!
static const char DIR_PATTERN [] = "^MSv2_[67][0-9A-Fa-f]_direction_[1-4]_[0-2]$";

static Adafruit_MotorShield *shields [32] = {};
// Initialized as all null
//https://stackoverflow.com/questions/2615071/c-how-do-you-set-an-array-of-pointers-to-null-in-an-initialiser-list-like-way
  // the above link described this initialization
  // shields holds pointer to the shield objects.
  // shields are addressed 0x60 to 0x7F for a total of 32 unique addresses.
  // In this array, [0] == address 0x60, [31] == address 0x7F
  // note, static in this context means the array's pointer can't change, the array values can


/*
 * Converts the message from the Serial port to its corresponding motor
 * 
 */
boolean getMotorShield(String message, Adafruit_MotorShield *shield){
// * https://stackoverflow.com/questions/45632093/convert-char-to-uint8-t-array-with-a-specific-format-in-c
// the above might help with the conversion
//https://learn.adafruit.com/adafruit-motor-shield-v2-for-arduino/stacking-shields
//Note: 0x70 is the broadcast

//pointers: https://stackoverflow.com/questions/28778625/whats-the-difference-between-and-in-c
   String shieldAddress = message.substring(5,7);//make sure this is the right length
   char carr [2];
   shieldAddress.toCharArray(carr, 2);
   uint8_t addr = strtol(carr, NULL, 16);
   if(addr<96 || addr > 127){
     Serial.print(addr);
     Serial.print(shieldAddress +"\n");//not sure if this will work
     return false;
   }
   if(!shields[addr - 96]){//checks for null pointer
      //Adafruit_MotorShield *AMS = malloc(sizeof(Adafruit_MotorShield));
      //AMS->add
      Adafruit_MotorShield AMS = Adafruit_MotorShield(addr);
      shields[addr - 96] = &AMS;
   }
   *shield = *shields[addr - 96]; 
   return true;
};

/*
 * gets the motor, then sets the speed
 */
boolean setMotorSpeed(String message, Adafruit_MotorShield shield){
   String motorID = message.substring(15,16);//make sure this is the right length
   char carr [1];
   motorID.toCharArray(carr, 1);
   uint8_t motorAddr = strtol(carr, NULL, 16);
   
   String speedIn = message.substring(16,18);//make sure this is the right length
   char speedCarr [2];
   speedIn.toCharArray(speedCarr, 2);
   uint8_t intSpeed = strtol(speedCarr, NULL, 16);

   shield.getMotor(motorAddr)->setSpeed(intSpeed);
   return true;
}

/*
 * gets the motor, then sets the direction
 * 
 * see here: https://learn.adafruit.com/adafruit-motor-shield-v2-for-arduino/library-reference#void-run-uint8-t-9-7
 */
boolean setMotorDir(String message, Adafruit_MotorShield shield){
   String motorID = message.substring(19,20);//make sure this is the right length
   char carr [1];
   motorID.toCharArray(carr, 1);
   uint8_t motorAddr = strtol(carr, NULL, 16);
   
   String dirIn = message.substring(21,23);//make sure this is the right length
   char dirCarr [2];
   dirIn.toCharArray(dirCarr, 2);
   uint8_t intDir = strtol(dirCarr, NULL, 16);

   shield.getMotor(motorAddr)->run(intDir);
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
    Adafruit_MotorShield as = Adafruit_MotorShield();//can't be named asm?
    if(!getMotorShield(message, &as)){
       //set toWrite to an error message saying this isn't a valid number
       *toWrite = String("MotorShield: That isn't a valid shield address." + message);
       return true;
    }
    
    if(ms.Match(SPEED_PATTERN) > 0){
      //parse out params
      //set speed on the shield
      setMotorSpeed(message, as);
      return true;
    }else if(ms.Match(DIR_PATTERN) > 0){
      //set direction
      setMotorDir(message, as);
      return true;
    //ADD OTHER STUFF (SET SERVOS...)
      // note, people can put crap between the SHIELD_PATTERN_START and the parameter patterns, but this isn't really a problem
    }else{
      *toWrite = String("MotorShield: No matching command found.");
      return false;
    }
  }else{
    return false;
  }
}
