/* 
This is a test sketch for the Adafruit assembled Motor Shield for Arduino v2
It won't work with v1.x motor shields! Only for the v2's with built in PWM
control

For use with the Adafruit Motor Shield v2 
---->	http://www.adafruit.com/products/1438
*/

#include <Wire.h>
#include "MotorShieldv2Lib.h"
// consider including #include <Regexp.h> here because it might be used in multiple libraries.
#include <Adafruit_MotorShield.h>

String toWrite;
char *usb;
byte counter;

void setup() {
  Serial.begin(9600);           // set up Serial library at 9600 bps
  Serial.println("Adafruit Motorshield v2 - DC Motor test!");
  toWrite = "";
  counter = 0;
  usb = new char[50];
  //char letter;
}

void loop() {
   while(Serial.available() > 0){
      char letter = Serial.read();
      if(letter != '.'){
        usb[counter] = letter;
        counter ++;
      }else{
        usb[counter] = '\0';
        if(checkMotorShieldMessage(usb, &toWrite)){
         //https://stackoverflow.com/questions/2229498/passing-by-reference-in-c
         //make sure this changes 
         Serial.println(toWrite);//passing the pointer
         //delete usb;
        }//you can check something else here
        counter = 0;
        //usb = new char[50];
      }
    }
}
