// Shows how to run three Steppers at once with varying speeds
//
// Requires the Adafruit_Motorshield v2 library 
//   https://github.com/adafruit/Adafruit_Motor_Shield_V2_Library
// And AccelStepper with AFMotor support 
//   https://github.com/adafruit/AccelStepper

// This tutorial is for Adafruit Motorshield v2 only!
// Will not work with v1 shields

/*
 * Have one MultiStepper object with all attached stepper motors
 * connected to it. This will require you to save what position the stepper motors were in
 * in the MultiStepper array and tie it to the the shield and stepper number so you can  
 * access it.
 */

#include <Wire.h>
#include <AccelStepper.h>
#include <Adafruit_MotorShield.h>
#include <MultiStepper.h>

/**
 * You have to override step0
 */
class MyAccelStepper: public AccelStepper
{
   public:
       MyAccelStepper(Adafruit_StepperMotor myStepper):AccelStepper(0,0,0,0,0,false)
       {
         //MyStepper(0, 0, 0, 0, 0, false);
         _myStepper = myStepper;
       }
   protected:
       void step0(long step){
          (void)(step);
          if(speed() > 0){
            _myStepper.onestep(FORWARD, DOUBLE);
          }else{
            _myStepper.onestep(BACKWARD, DOUBLE);            
          }
       }
    private:
       Adafruit_StepperMotor _myStepper;
};

/*
#include <ArduinoSTL.h>

using namespace std;

class Steppers{
  public:
    void addStepper(uint16_t steps_per_rev, uint8_t stepperNumb, uint8_t shield);
    Adafruit_StepperMotor getSavedStepper(uint8_t shield, uint8_t stepperNumb);
    Adafruit_StepperMotor getSavedStepper(int index);
  private:
  //Adafruit_MotorShield addresses are uint8_t
  //stepperNumb is uint8_T
    uint8_t steppers [10][2];  
};

void Steppers::addStepper(uint16 steps_per_rev, uint8_t stepperNumb, uint8_t shield){
  
}
*/
Adafruit_MotorShield AFMSbot(0x61); // Rightmost jumper closed
Adafruit_MotorShield AFMStop(0x60); // Default address, no jumpers

// Connect two steppers with 200 steps per revolution (1.8 degree)
// to the top shield
Adafruit_StepperMotor *myStepper1 = AFMStop.getStepper(200, 1);
Adafruit_StepperMotor *myStepper2 = AFMStop.getStepper(200, 2);

// you can change these to DOUBLE or INTERLEAVE or MICROSTEP!
// wrappers for the first motor!
void forwardstep1() {  
  myStepper1->onestep(FORWARD, DOUBLE);
  //DOUBLE is the high torque
}
void backwardstep1() {  
  myStepper1->onestep(BACKWARD, DOUBLE);
}
// wrappers for the second motor!
void forwardstep2() {  
  myStepper2->onestep(FORWARD, DOUBLE);
}
void backwardstep2() {  
  myStepper2->onestep(BACKWARD, DOUBLE);
}

// Now we'll wrap the 3 steppers in an AccelStepper object
AccelStepper stepper1(forwardstep1, backwardstep1);
AccelStepper stepper2(forwardstep2, backwardstep2);

MultiStepper steppers;

long positions [2];

void setup()
{  
  Serial.begin(9600);
  AFMSbot.begin(); // Start the bottom shield
  AFMStop.begin(); // Start the top shield
   
  stepper1.setMaxSpeed(100.0);
  stepper1.setSpeed(100.0);
  //stepper1.setAcceleration(100.0);
  //stepper1.moveTo(24);
  steppers.addStepper(stepper1);
  
  stepper2.setMaxSpeed(200.0);
  stepper2.setSpeed(100.0);
  //stepper2.setAcceleration(100.0);
  //stepper2.moveTo(50000);
  steppers.addStepper(stepper2);
  positions[0] = 1000;
  positions[1] = 500;
}

void loop()
{
  positions[0] += 100;
  positions[1] += 50;
  steppers.moveTo(positions);
  steppers.runSpeedToPosition();

  delay(1000);
  Serial.println("ho");
}
