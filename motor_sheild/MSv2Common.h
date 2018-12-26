#ifndef MSv2Common
#define MSv2Common

#if (ARDUINO >=100)
  #include <Arduino.h>
#else
  #include "WProgram.h"
#endif

using namespace std;

#include <Adafruit_MotorShield.h>


static const char API_PATTERN [] = "^APIs";
//Android sends the API_PATTERN to the arduino to ask for this API's name.


extern Adafruit_MotorShield *shields [32];
  // shields holds pointer to the shield objects.
  // shields are addressed 0x60 to 0x7F for a total of 32 unique addresses.
  // In this array, [0] == address 0x60, [31] == address 0x7F

/*
 * converts a substring between A and B from message to a uint8_t
 */
uint8_t substr2num(char *message, int A, int B);

/** 
 * Checks if an I2C device is connected at shieldAddr. This effectively checks if a 
 * motor shield exists at that location.
 *
 */
boolean shieldConnected(uint8_t shieldAddr);

uint8_t getMotorShield(char *message);

uint8_t getMotorShield(uint8_t message);

#endif
