#include "MSv2Common.h"
#include <Wire.h> 
// the serial library?
#include <Regexp.h>
#include <Adafruit_MotorShield.h>
#include "MSv2Steppers.h"

#include <AccelStepper.h>
#include <MultiStepper.h>

//general pattern: MSv2Steppers_(shield I2C address)_(command)_(stepper motor number)_(parameter)

//You should have int params telling where parts of the string start and stop.

/*TESTS:
 * MSv2Steppers_60_move_1_+0FF_group_00
 * MSv2Steppers_60_move_0_+0FF_group_00
 * 
 * MSv2Steppers_execute_group_00 
 * 
*/

static const char STEPPER_PATTERN_START [] = "^MSv2Steppers_";
static const char MOVE_PATTERN [] = "^MSv2Steppers_[67]%x_move_[0-1]_[+-]"
                                    "%x%x%x_group_%x%x$";
//The end matches longs https://stackoverflow.com/questions/11243204/how-to-match-a-long-with-java-regex

static const char EXE_PATTERN [] = "^MSv2Steppers_execute_group_%x%x$";

static const String NAME = "MSv2Steppers";

//************************************STEPPER MOTORS*******************************************


/**
 * A customized AccelStepper that takes an Adafruit_StepperMotor.
 * 
 */
class MyAccelStepper: public AccelStepper
{
   public:
       MyAccelStepper(Adafruit_StepperMotor *myStepper):AccelStepper(0,0,0,0,0,false)
       {
         //MyStepper(0, 0, 0, 0, 0, false);
         _myStepper = myStepper;
       }
   protected:
       void step0(long step) override{
          if(_myStepper == NULL){
            AccelStepper::step0(step);
          }else{
            (void)(step);
            if(speed() > 0){
              _myStepper->onestep(FORWARD, DOUBLE);
            }else{
              _myStepper->onestep(BACKWARD, DOUBLE);            
            }          
          }
       }
    private:
       Adafruit_StepperMotor *_myStepper;
};


/*
 * Each MultiStepper can contain up to 10 objects. This class wraps MultiStepper to
 * handle these objects
 */
class Steppers: public MultiStepper{
  public:
    Steppers(){
      curStepperIndex = 0;
      //steppersIndexes [10][2] = new uint8_t [10][2];
    }
    
    /**
     * Adds a stepper motor to act in unison with the other bound stepper motors 
     * 
     * params:
     * stepperNum: the number
     * with 200 steps
     */
    void addStepper(uint8_t stepperNumb, uint8_t shield, uint16_t steps_per_rev = 200){
      if(curStepperIndex >= 9){
        //error, too many shields
        Serial.println(NAME + "too many steppers to add another.");
      }else if(stepperNumb != 0 and stepperNumb!=1){
         //Invalid Stepper motor
         Serial.println(NAME + "invalid stepper number " + String(stepperNumb));         
      }else if(getSavedStepperIndex(shield, stepperNumb) != 255){
         //stepper already added.
         //This will happen in most cases.
         Serial.println(NAME + "stepper already exists at " +
                        String(getSavedStepperIndex(shield, stepperNumb)));
         Serial.println(NAME + "step index: " +String(curStepperIndex));
      }else if(!shieldConnected(shield)){
        // shield not connected.
         //this is actually redundant in the current form.
         Serial.println(NAME + "shield not connected");
      }else{
        Adafruit_MotorShield AFMS = *shields[shield];//parsed out by getMotorShield?
        Adafruit_StepperMotor *myStep = AFMS.getStepper(steps_per_rev, stepperNumb);
        
        MyAccelStepper curStepper(myStep);// create a MyAccelStepper named curStepper
        MultiStepper::addStepper(curStepper);//super class's method
        steppersIndexes[curStepperIndex][0] = shield;
        steppersIndexes[curStepperIndex][1] = stepperNumb;//this could be bool?
        curStepperIndex++;
      }
    }

    /**
     * Finds the index of stepper motor in the class's array that's on this shield,
     * and is this stepper motor. 
     * 
     * Returns: the index in the array if the motor is in the array, otherwise it returns 255
    */
    uint8_t getSavedStepperIndex(uint8_t shield, uint8_t stepperNumb){
      for(int index = 0; index <= curStepperIndex; index++){
        if(steppersIndexes[index][0] == shield 
          && steppersIndexes[index][1] == stepperNumb){
            Serial.print("Index at: ");
            Serial.println(index);
          return index; 
        }
      }
      return 255;
    }

    /**
     * Tells the motor to move moveAmount ticks relative to the position it's in when it's executed.
     */
    void setToMove(uint8_t shield, uint8_t stepperNumb, long moveAmount){
       setToMove(getSavedStepperIndex(shield, stepperNumb), moveAmount);
    }

    /**
     * Tells the motor to move moveAmount ticks relative to the position it's in.
     */
    void setToMove(uint8_t index, long moveAmount){
       moves[index] += moveAmount;
    }

    /**
     * Calls moveTo with the stored possitions
     */
    void moveTo(){
      //Serial.println("move to " + String(curStepperIndex));//getting printed as m?
      long * posArr = getPos_resetMoves();
      /*
      for(int pos = 0; pos < curStepperIndex; pos++){
        Serial.println("here");
        Serial.println(posArr[pos]);
      }
      */
      MultiStepper::moveTo(posArr);
      MultiStepper::runSpeedToPosition();
      delete[] posArr;
      //delete[]: http://www.cplusplus.com/reference/new/operator%20new[]/
    }

    /**
     * Gets the positon array after finding the motor. Creates the motor if it doesn't
     * exist.
     */
    long getPosition(uint8_t shield, uint8_t stepperNumb){
      uint8_t index = getSavedStepperIndex(shield, stepperNumb);
      if(index > -1){
         return getPosition(index);
      }else{
        //Motor doesn't exist.
        Serial.println("motor doesn't exist");
      }
    }
    
    /**
     * gets the possition array that will be passed to moveTo after memcpy 
     */
     long getPosition(uint8_t index){
       if(index < 10){
          return stepperObjects[index]->currentPosition();//return a copy of this instead of the real thing.
       }else{
          //error, indexOutOfBounds
          Serial.println("invalid stepperObjects index");
       }
     }
 
  private:
  //Adafruit_MotorShield addresses are uint8_t
  //stepperNumb is uint8_T
    uint8_t steppersIndexes [10][2];//[[shield, stepper], [shield, stepper],...]
    //changing from uint8_t to boolean is pointless because they're both 8bits to the OS.
    uint8_t curStepperIndex;
    long moves [10] = {0};
    //you pass this to moveTo. Before you do, you have to resize it with memcpy in getPos_resetMoves
    AccelStepper *stepperObjects [10];//replace positions with this so you can have overlapping groups
        
    /**
     * This will add a long[] array to the free store (heap). You MUST delete it with _____.
     * The array this returns will be passed to moveTo
     */
    long * getPos_resetMoves(){
      long * posArr = new long [curStepperIndex + 1];// need to put on "free store"
      for (int i=0; i < curStepperIndex; i++){
        Serial.println(moves[i]);
        posArr[i] = stepperObjects[i]->currentPosition() + moves[i];
        moves[i] = 0;
      }
      return posArr;
    }
};


//********************************MAIN********************************************

/*
 * 151473214816 is the number of unique combinations the 64 possible stepper motors can be 
 * combined into sets of 10 motors for Steppers objects. (combinations 64 choose 10 
 * without reuses or repeats)
 * 
 * Because That number isn't realistic, I set the number of possible groups to 100
 * which is more than enough for 99% of projects.
 */
static Steppers *groups[16];

/**
   * parses char *message: 
   *      "^MSv2Steppers_[67]%x_move_[0-1]_[\-\+]%x%x%x_group_%x%x$" - total len = 35
   * For these arameters: [uint8_t stepperNumb, long moveAmount, int group]
   * 
   * Then uses these parameters to tell the Steppers object how much to move when an execute
   * is received.
   * 
 */
void setToMove(char *message, int shieldInt, String *toWrite){
  uint8_t group = substr2num(message, 34, 36);
  if(group < 16){//group isn't too large.
    uint8_t stepperNumb = message[21] - '0'; //conversion to number
    //Multiplying by 256 here because substr2num can only handle 2 digits at a time
    long moveAmount = (substr2num(message, 24, 25) * 256) + substr2num(message, 25, 27);
    //24 in the neg or positive.
    //' for char, " for strings!!!
    //https://stackoverflow.com/questions/7808405/comparing-a-char-to-a-const-char
    if(message[23] == '-')
      moveAmount *= -1;
    if(groups[group] == NULL){
      groups[group] = new Steppers();
    }
    groups[group]->addStepper(stepperNumb, shieldInt);
    //Adds the stepper if it doesn't exist.
    groups[group]->setToMove(shieldInt, stepperNumb, moveAmount);
    //Move all the stepper motors the required amount.
  }else{//group too large for the groups array
    toWrite->concat(": for memory purposes, only 16 groups allowed (0-15). ");
      //"An extra byte char is possible in the pattern for future expansion"
      //"but for now don't use it.");
    //return true;//kick out of this, because we have an error.
  }
}


/*
 * Motor shield Stepper signals are of the format 
 * "MSv2Stepper_shield number_then the command_parameters"
 * see the constants at the top for the commands
 * 
 *   
 *   Remember, you NEED to de-reference toWrite with this: https://stackoverflow.com/questions/2229498/passing-by-reference-in-c
 
*/
boolean checkMSv2Steppers(char *message, String *toWrite){
  MatchState ms;
  ms.Target(message);
  // converting to char array: https://www.arduino.cc/reference/en/language/variables/data-types/string/functions/tochararray/
  // regex from: https://github.com/nickgammon/Regexp also see the installed examples
  if(ms.Match(STEPPER_PATTERN_START) > 0){
    toWrite->concat(NAME);
    if(ms.Match(MOVE_PATTERN) > 0){
      uint8_t shieldInt = getMotorShield(message);
      if(shieldInt < 0){//error
        if(shieldInt == -1){
          //set toWrite to an error message saying this isn't a valid number
          toWrite->concat(": That isn't a valid shield address.");
        }else if(shieldInt == -2){
          toWrite->concat(": Shield not attached."); 
        }else{
          //unknown error
        }
      }else{//shield connected.
        setToMove(message, shieldInt, toWrite);
      }
    }else if(ms.Match(EXE_PATTERN) > 0){
      //call run
      groups[substr2num(message, 27, 29)]->moveTo();
      toWrite->concat(": Move success");
    }else{
      toWrite->concat(": No matching command found.");
    }
    return true;
  }else{
    if(ms.Match(API_PATTERN) > 0){
      //If this matches you still return false becasue it's not exclusively for this.
      if(toWrite->length() > 0){
        toWrite->concat("_");
      }
      toWrite->concat(NAME);
    }
    return false;
  }
}
