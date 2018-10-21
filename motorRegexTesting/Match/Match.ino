#include <Regexp.h>

static const char DIR_PATTERN [] = "^MSv2_[67]%x_direction_[1-4]_[0-2]$";

void setup ()
{
  Serial.begin (9600);

  // match state object
  MatchState ms;
  String message = "MSv2_60_direction_1_1";
  // what we are searching (the target)
  char buf [message.length() + 1];
  message.toCharArray(buf, message.length() + 1);
  ms.Target (buf);  // set its address
  ms.Match("random");
  Serial.println (buf);

  //char result = );
  
  if (ms.Match (DIR_PATTERN) > 0)
    {
    Serial.print ("Found match at: ");
    Serial.println (ms.MatchStart);        // 16 in this case     
    Serial.print ("Match length: ");
    Serial.println (ms.MatchLength);       // 3 in this case
    }
  else
    Serial.println ("No match.");
    
}  // end of setup  

void loop () {}
