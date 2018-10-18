/* 
This is a test sketch for the Adafruit assembled Motor Shield for Arduino v2
It won't work with v1.x motor shields! Only for the v2's with built in PWM
control

For use with the Adafruit Motor Shield v2 
---->	http://www.adafruit.com/products/1438
*/

#include <Wire.h>
#include "MotorShieldv2Lib.h"
//#include <Regexp.h>
#include <Adafruit_MotorShield.h>

String toWrite;

void setup() {
  Serial.begin(9600);           // set up Serial library at 9600 bps
  Serial.println("Adafruit Motorshield v2 - DC Motor test!");
  toWrite = "";
}

void loop() {
   String usb = Serial.readString();
   if(checkMotorShieldMessage(usb, &toWrite)){
     //https://stackoverflow.com/questions/2229498/passing-by-reference-in-c
     //make sure this changes 
     Serial.print(toWrite);//passing the pointer
   }
}
