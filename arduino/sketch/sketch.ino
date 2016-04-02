//char dataString[100] = {0};
char dataBuffer[100] = {0};
String dataString = "";
int a =0; 

int dirA = 12;
int dirB = 13;  
int speedA = 3;
int speedB = 11; 


void setup() {
    Serial.begin(115200);              //Starting serial communication
    
     while (!Serial) {
      ; // wait for serial port to connect. Needed for native USB port only
     }
    pinMode (dirA, OUTPUT);
    pinMode (dirB, OUTPUT);
    pinMode (speedA, OUTPUT);
    pinMode (speedB, OUTPUT);
    dataString = "";
}
  
void loop() {

  //sprintf(dataString,"%s",a); // convert a value to hexa 
  
  //Serial.println("Ready");
  if(Serial.available() ){
    dataString = "";
    dataString = Serial.readString();
  
   digitalWrite(speedA, LOW);
   digitalWrite(speedB, LOW);
  
   if(strcmp(dataString.c_str(), "Forward") == 0 ){

          // move the motor A to one direction increasing speed
          digitalWrite (dirA, HIGH);
          digitalWrite (dirB, HIGH);
          
          analogWrite (speedA, 255);
          analogWrite (speedB, 255);
          //delay (1000);
            // stop the motor
         //digitalWrite(speedA, LOW);
         //digitalWrite(speedB, LOW);
         
          //delay(500); // keep the motor rolling for one second
          Serial.println("forwarding...");          
      }else if(strcmp(dataString.c_str(), "Backward") == 0 ){
         //backward
        digitalWrite (dirA, LOW);
        digitalWrite (dirB, LOW);
        
        analogWrite (speedA, 255);
        analogWrite (speedB, 255);
      
        delay(500);
        // stop the motor
        digitalWrite(speedA, LOW);
        digitalWrite(speedB, LOW);
        //delay(500); // keep the motor rolling for one second
        Serial.println("Backing...");          
    }else if(strcmp(dataString.c_str(), "Left") == 0 ){
       //Left
      
      digitalWrite (dirA, LOW);
      digitalWrite (dirB, HIGH);
    
      analogWrite (speedA, 255);
      analogWrite (speedB, 255);
    
      delay(500);
      // stop the motor
      digitalWrite(speedA, LOW);
      digitalWrite(speedB, LOW);
      //delay(500); // keep the motor rolling for one second  
      Serial.println("left...");          
    }else if(strcmp(dataString.c_str(), "Right") == 0 ){
         //Right
        digitalWrite (dirA, HIGH);
        digitalWrite (dirB, LOW);
      
        analogWrite (speedA, 255);
        analogWrite (speedB, 255);
      
        delay(500);
        // stop the motor
        digitalWrite(speedA, LOW);
        digitalWrite(speedB, LOW);
        //delay(500); // keep the motor rolling for one second
        Serial.println("right..");          
    }else if(strcmp(dataString.c_str(), "Stop") == 0 ){
        digitalWrite(speedA, LOW);
        digitalWrite(speedB, LOW);
        Serial.println("stopped");
    }else{
      Serial.println("error input: " + dataString);
    }
    
  }//end of if   

}
