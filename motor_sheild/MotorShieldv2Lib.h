#ifndef MotorShieldv2lib
#define MotorShieldv2lib

#if (ARDUINO >=100)
  #include <arduino.h>
#else
  #include "WProgram.h"
#endif
 
#include <Wire.h> 
#include <SoftwareSerial.h>
// the serial library?
// hardware vs software serial https://forum.arduino.cc/index.php?topic=407633.0
// maybe you don't need serial?

#include <Adafruit_MotorShield.h>
#include "MotorShieldv2Lib.h"

using namespace std;

/*
 * Take a message and a Stream (Serial object)
 *   - the message was received from the stream
 *   - The stream will send a message back if it has to (error code...)
 */
boolean checkMotorShieldMessage(String message, String *toWrite);

#endif
