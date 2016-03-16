//this script tests that the motor r3 shield works
int dirA = 12;
int dirB = 13;  // not used in this example
int speedA = 3;
int speedB = 11; // not used in this example

void setup()
{
  pinMode (dirA, OUTPUT);
  pinMode (dirB, OUTPUT);
  pinMode (speedA, OUTPUT);
  pinMode (speedB, OUTPUT);
}

void loop()
{
  // move the motor A to one direction increasing speed
  //forward  
  digitalWrite (dirA, HIGH);
  digitalWrite (dirB, HIGH);
  
  analogWrite (speedA, 250);
  analogWrite (speedB, 250);
  delay (1000);
  
  // stop the motor
  digitalWrite(speedA, LOW);
  digitalWrite(speedB, LOW);
  delay(1000); // keep the motor rolling for one second
  


  //backward
  digitalWrite (dirA, LOW);
  digitalWrite (dirB, LOW);

  analogWrite (speedA, 250);
  analogWrite (speedB, 250);

  delay(1000);
  // stop the motor
  digitalWrite(speedA, LOW);
  digitalWrite(speedB, LOW);
  delay(1000); // keep the motor rolling for one second



  //Left
  digitalWrite (dirA, LOW);
  digitalWrite (dirB, HIGH);

  analogWrite (speedA, 250);
  analogWrite (speedB, 250);

  delay(1000);
  // stop the motor
  digitalWrite(speedA, LOW);
  digitalWrite(speedB, LOW);
  delay(1000); // keep the motor rolling for one second



  //Right
  digitalWrite (dirA, HIGH);
  digitalWrite (dirB, LOW);

  analogWrite (speedA, 250);
  analogWrite (speedB, 250);

  delay(1000);
  // stop the motor
  digitalWrite(speedA, LOW);
  digitalWrite(speedB, LOW);
  delay(1000); // keep the motor rolling for one second



  
}
