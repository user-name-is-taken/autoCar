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
  if(Serial.available() > 0){
   while(Serial.available() > 0){
      char letter = Serial.read();
      if(letter == '\n')
        break;//handles the last '\n' char of arduino serial input
      usb[counter] = letter;
      counter ++;
      //the last character is a new line, destroying my regex.
      //how can I handle this?
   }
   usb[counter] = '\0';//terminates the string (replacing the newline)
   if(checkMotorShieldMessage(usb, &toWrite)){
     //https://stackoverflow.com/questions/2229498/passing-by-reference-in-c
     Serial.println(toWrite);//passing the pointer
   }else if(usb == "APIs"){//tells what APIs are connected
     Serial.println(toWrite);
      //you can check something else here (another API's check...)
   }
   counter = 0;
   /*
    * note, this program assumes the entire message is in the buffer
    * when Serial.avaliable reads it, and that another message doesn't
    * get put into the buffer until after you've read this one.
    */
 } 
}
