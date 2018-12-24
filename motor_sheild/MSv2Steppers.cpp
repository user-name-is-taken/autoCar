#include "MSv2Common.h"
#include <Wire.h> 
// the serial library?
#include <Regexp.h>
#include <Adafruit_MotorShield.h>
#include "MSv2Steppers.h"

#include <AccelStepper.h>
#include <MultiStepper.h>

//general pattern: MSv2Steppers_(shield I2C address)_(command)_(stepper motor number)_(parameter)

//maybe add a group of motors? You can only have 10 at a time.

static const char STEPPER_PATTERN_START [] = "^MSv2Steppers_";
static const char MOVE_PATTERN [] = "^MSv2Steppers_[67]%x_move_[0-1]_-?\\d{1,19}$";
//The end matches longs https://stackoverflow.com/questions/11243204/how-to-match-a-long-with-java-regex

//move to an absolute possition
//static const char ABS_MOVE_PATTERN [] = "^MSv2Steppers_[67]%x_absMove_[0-1]_-?\\d{1,19}$";

static const char EXE_PATTERN [] = "^MSv2Steppers_execute$";

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
      }else if(stepperNumb != 0 and stepperNumb!=1){
         //Invalid Stepper motor
      }else if(!shieldConnected(shield)){
         // shield not connected.
      }else if(getSavedStepperIndex(shield, stepperNumb) != -1){
        //error, shield already added
      }else{  
        Adafruit_MotorShield AFMS = *shields[shield];//parsed out by getMotorShield?
        static Adafruit_StepperMotor *myStep = AFMS.getStepper(steps_per_rev, stepperNumb);
        
        MyAccelStepper curStepper(myStep);// create a MyAccelStepper named curStepper
        MultiStepper::addStepper(curStepper);//super class's method
        curStepperIndex +=1;
        steppersIndexes[curStepperIndex][0] = shield;
        steppersIndexes[curStepperIndex][1] = stepperNumb;
      }
    }

    /**
     * Finds the index of stepper motor in the class's array that's on this shield,
     * and is this stepper motor. 
     * 
     * Returns: the index in the array if the motor is in the array, otherwise it returns -1
    */
    uint8_t getSavedStepperIndex(uint8_t shield, uint8_t stepperNumb){
      for(int index = 0; index <= curStepperIndex; index++){
        if(steppersIndexes[index][0] == shield 
          && steppersIndexes[index][1] == stepperNumb){
          return index; 
        }
      }
      return -1;
    }

    /**
     * Tells the motor to move moveAmount ticks relative to the position it's in.
     */
    void setToMove(uint8_t shield, uint8_t stepperNumb, long moveAmount){
       uint8_t index = getSavedStepperIndex(shield, stepperNumb);
       long curPos = getPosition(index);
       curPos += moveAmount;// returned by reference?
       //setPosition(index, curPos); //not necessary if returned by ref
    }

    /**
     * Tells the motor to move moveAmount ticks relative to the position it's in.
     */
    void setToMove(uint8_t index, long moveAmount){
       long curPos = getPosition(index);
       curPos += moveAmount;// returned by reference?
       //setPosition(index, curPos); //not necessary if returned by ref
    }

    /**
     * Calls moveTo with the stored possitions
     */
    void moveTo(){
      long * posArr = getPosArr();
      MultiStepper::moveTo(posArr);
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
      }
    }
    
    /**
     * gets the possition array that will be passed to moveTo after memcpy 
     */
     long getPosition(uint8_t index){
       if(index < 10){
          return positions[index];//return a copy of this instead of the real thing.
       }else{
          //error, indexOutOfBounds
       }
     }


     
  private:
  //Adafruit_MotorShield addresses are uint8_t
  //stepperNumb is uint8_T
    uint8_t steppersIndexes [10][2];//[[shield, stepper], [shield, stepper],...]
    unsigned char curStepperIndex;
    long positions [10];//you pass this to moveTo. Before you do, you have to resize it with memcpy
    Adafruit_StepperMotor stepperObjects [10];//replace positions with this so you can have overlapping groups


    /**
     * Sets the positon array after finding the motor. Creates the motor if it doesn't
     * exist.
     */
    void setPosition(uint8_t shield, uint8_t stepperNumb, long pos){
      uint8_t index = getSavedStepperIndex(shield, stepperNumb);
      if(index > -1){
         setPosition(index, pos);
      }else{
        //Motor doesn't exist. Creating the stepperMotor, then set its pos.
        index = curStepperIndex;
        addStepper(stepperNumb, shield);
        setPosition(index, pos);
      }
    }
    
    /**
     * sets the possition array that will be passed to moveTo after memcpy 
     */
     void setPosition(uint8_t index, long pos){
       if(index < 10){
          positions[index] = pos;        
       }else{
          //error, indexOutOfBounds
       }
     }
    
    /**
     * This will add a long[] array to the free store (heap). You MUST delete it with _____.
     * The array this returns will be passed to moveTo
     */
    long * getPosArr(){
      long * posArr = new long [curStepperIndex + 1];// need to put on "free store"
      //memcpy: https://stackoverflow.com/questions/19439715/using-memcpy-in-c
      // https://stackoverflow.com/questions/4643713/c-returning-reference-to-local-variable
      //new: http://www.cplusplus.com/reference/new/operator%20new[]/
      memcpy(posArr, steppersIndexes, sizeof *positions);
      return posArr;
    }
};


//********************************MAIN********************************************

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
boolean checkMSv2Steppers(char *message, String *toWrite){
  MatchState ms;
  ms.Target(message);
  Serial.println(message);
  char isForStepper = ms.Match(STEPPER_PATTERN_START);//check if the message is for the shield
  // converting to char array: https://www.arduino.cc/reference/en/language/variables/data-types/string/functions/tochararray/
  // regex from: https://github.com/nickgammon/Regexp also see the installed examples
  if(isForStepper > 0){
    //parse out which shield, set it as a variable
    Serial.println("match");//only works on the first one?
    
    
    if(ms.Match(MOVE_PATTERN) > 0){
      int shieldInt = getMotorShield(message);
      //parse out params
      if(shieldInt < 0){
        if(shieldInt == -1){
          //set toWrite to an error message saying this isn't a valid number
          *toWrite = String(NAME + ": That isn't a valid shield address.");
        }else if(shieldInt == -2){
          *toWrite = String(NAME + ": Shield not attached."); 
        }
      }
    }else if(ms.Match(EXE_PATTERN) > 0){
      //call run
    }else{
      *toWrite = String(NAME + ": No matching command found.");
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
