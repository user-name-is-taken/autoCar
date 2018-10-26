#include <Adafruit_MotorShield.h>
#include <Wire.h>

#include <Regexp.h>

static const char DIR_PATTERN [] = "^MSv2_[67]%x_direction_[1-4]_[0-2]$";
static const char SHIELD_PATTERN_START [] = "^MSv2_[67]%x_";
static Adafruit_MotorShield *shields [32];

boolean getMotorShield(String message, Adafruit_MotorShield *shield){
// * https://stackoverflow.com/questions/45632093/convert-char-to-uint8-t-array-with-a-specific-format-in-c
// the above might help with the conversion
//https://learn.adafruit.com/adafruit-motor-shield-v2-for-arduino/stacking-shields
//Note: 0x70 is the broadcast

//pointers: https://stackoverflow.com/questions/28778625/whats-the-difference-between-and-in-c
   String shieldAddress = message.substring(5,7);//make sure this is the right length
   char carr [3];
   shieldAddress.toCharArray(carr, 3);
   uint8_t addr = strtol(carr, NULL, 16);
   //MSv2_60_speed_1_10
   if(addr < 96 || addr > 127){
     return false;
   }
   if(!shields[addr - 96]){//makes sure it's a null pointer
      //Adafruit_MotorShield *AMS = malloc(sizeof(Adafruit_MotorShield));
      //AMS->add
      Adafruit_MotorShield AMS = Adafruit_MotorShield(addr);
      AMS.begin();
      shields[addr - 96] = &AMS;
   }
   *shield = *shields[addr - 96]; 
   return true;
};


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
   Serial.println(speedIn);
   Serial.println(intSpeed);
   shield.getMotor(motorAddr)->setSpeed(intSpeed);
   Serial.println("done set speed");
   
   return true;
}

Adafruit_MotorShield sh;

  
void setup ()
{
  Serial.begin (9600);
  String *as;
  String a1 = "hi";
  String a2 = "heeee";
  as = &a1;
  as = &a2;
  Serial.print(a1);
  /*
  String speedMessage = "MSv2_60_speed_1_6F";
  getMotorShield(speedMessage, &sh);
  Serial.println("hi");
  sh.begin();
  */
  

}

void loop () {
  /*
  setMotorSpeed("MSv2_60_speed_1_6f",sh);
  setMotorDir("MSv2_60_direction_1_1", sh);
  delay(1000);
  setMotorDir("MSv2_60_direction_1_0", sh);
  setMotorSpeed("MSv2_60_speed_1_FF", sh);
  delay(1000);
  setMotorDir("MSv2_60_direction_1_1", sh);
  delay(1000);
  setMotorDir("MSv2_60_direction_1_0", sh);
  Serial.println("re loop");
  */
 }
