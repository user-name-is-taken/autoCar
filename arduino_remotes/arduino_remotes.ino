/*
 * IRremote: IRrecvDemo - demonstrates receiving IR codes with IRrecv
 * An IR detector/demodulator must be connected to the input RECV_PIN.
 * Version 0.1 July, 2009
 * Copyright 2009 Ken Shirriff
 * http://arcfn.com
 */

#include <IRremote.h>

boolean breakBeamActive = false;
unsigned long breakBeamEnd = millis();// will hit an error if 
//breakBeamEnd happens after the 50 day mark
int activateBeam; // the break beam
unsigned long lastBBsignal = millis();

int takePic=0;

int RECV_PIN = 11;

IRrecv irrecv(RECV_PIN);

decode_results results;


void setup()
{
  Serial.begin(9600);
  // In case the interrupt driver crashes on setup, give a clue
  // to the user what's going on.
  Serial.println("Enabling IRin");
  irrecv.enableIRIn(); // Start the receiver
  Serial.println("Enabled IRin");
}
/*You need to actually run the break beam logic
*/
void loop() {
  if (irrecv.decode(&results)) {
    int k = results.value;
    if(k==takePic){
      Serial.println(results.value, HEX);
      //takes the pic
    }else if (k==activateBeam){
      startBreakBeam();
    }
    irrecv.resume(); // Receive the next value
  }else if(breakBeamActive){
    handleBreak();
  }
  if(breakBeamActive && breakBeamEnd<=millis()){
    breakBeamActive = false;//end break beam
  }
  //delay(100);
}

void handleBreak(){
  //handles when the beam is active and there's no signal
  if(millis()>=lastBBsignal+50){
    Serial.println(takePic, HEX);
  } 
}

void startBreakBeam(){
 /*does the break beam
 this should be threaded somehow
 instance var?*/
 if(breakBeamEnd+4000 > breakBeamEnd){
   breakBeamEnd += 2000; // will end in 2000 milliseconds. can change.
   breakBeamActive = true;
 }
}


