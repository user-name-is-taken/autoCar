#include <Adafruit_MotorShield.h>
#include <Wire.h>

#include <Regexp.h>

static const char DIR_PATTERN [] = "^MSv2_[67]%x_direction_[1-4]_[0-2]$";
static const char SHIELD_PATTERN_START [] = "^MSv2_[67]%x_";

boolean setMotorDir(String message, Adafruit_MotorShield shield){
   String motorID = message.substring(18,19);//make sure this is the right length
   char carr [2];
   motorID.toCharArray(carr, 2);
   uint8_t motorAddr = strtol(carr, NULL, 16);
   
   String dirIn = message.substring(20,21);//make sure this is the right length
   
   Serial.println(dirIn);
   Serial.println(motorAddr);
   
   if(dirIn.equals("0")){
    shield.getMotor(motorAddr)->run(RELEASE); 
   }else if (dirIn.equals("1")){
    shield.getMotor(motorAddr)->run(FORWARD);
   }else if (dirIn.equals("2")){
    shield.getMotor(motorAddr)->run(BACKWARD);
   }else{
    return false;
   }
   Serial.println("set Direction");
   return true;
}

boolean setMotorSpeed(String message, Adafruit_MotorShield shield){
   String motorID = message.substring(14,15);//make sure this is the right length
   char carr [2];
   motorID.toCharArray(carr, 2);
   uint8_t motorAddr = strtol(carr, NULL, 16);
   
   String speedIn = message.substring(16,18);//make sure this is the right length
   char speedCarr [3];
   speedIn.toCharArray(speedCarr, 3);
   uint8_t intSpeed = strtol(speedCarr, NULL, 16);

   Serial.println(motorAddr);
   Serial.println("speed set");//not sure why I need this line
   Serial.println(intSpeed);
   shield.getMotor(motorAddr)->setSpeed(intSpeed);
   Serial.println("done set speed");
   
   return true;
}

void setup ()
{
  Serial.begin (9600);
  String dirMessage = "MSv2_60_direction_1_1";
  String speedMessage = "MSv2_60_speed_1_6F";
  Serial.println("hi");
  Adafruit_MotorShield sh = Adafruit_MotorShield();
  sh.begin();
  setMotorSpeed(speedMessage, sh);
  setMotorDir(dirMessage, sh);

}

void loop () {}
