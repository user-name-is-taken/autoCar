#include <Regexp.h>

static const char DIR_PATTERN [] = "^MSv2_[67]%x_direction_[1-4]_[0-2]$";
static const char SHIELD_PATTERN_START [] = "^MSv2_[67]%x_";

void setup ()
{
  Serial.begin (9600);
  String message = "MSv2_60_direction_2_1";
  Serial.println("hi");

  Serial.println(message.substring(20,21));
}
void loop () {}
