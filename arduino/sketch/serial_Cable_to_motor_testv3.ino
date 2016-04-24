//char dataString[100] = {0};
char dataBuffer[128] = {0};
String dataString = "";
int a =0; 

int dirA = 12;
int dirB = 13;  
int speedA = 3;
int speedB = 11; 
int slow = 200;
int fast = 300;
int faster = 400;
int turn = 200;

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
  if(Serial.available() ){

    dataString = "";
    dataString = Serial.readString();
  
   digitalWrite(speedA, LOW);
   digitalWrite(speedB, LOW);
   const char *payload; 
   payload = dataString.c_str();
   if(strcmp(payload, "Forward") == 0 ){

          // move the motor A to one direction increasing speed
          digitalWrite (dirA, HIGH);
          digitalWrite (dirB, HIGH);
          
          analogWrite (speedA, 255);
          analogWrite (speedB, 255);
                   
     }else if(strcmp(payload, "Backward") == 0 ){
         //backward
        digitalWrite (dirA, LOW);
        digitalWrite (dirB, LOW);
        
        analogWrite (speedA, 255);
        analogWrite (speedB, 255);
      
       
    }else if(strcmp(payload, "Left") == 0 ){
       //Left
      
      digitalWrite (dirA, LOW);
      digitalWrite (dirB, HIGH);
    
      analogWrite (speedA, 255);
      analogWrite (speedB, 255);
            
    }else if(strcmp(payload, "Right") == 0 ){
         //Right
        digitalWrite (dirA, HIGH);
        digitalWrite (dirB, LOW);
      
        analogWrite (speedA, 255);
        analogWrite (speedB, 255);
      
          
    }else if(strcmp(payload, "Stop") == 0 ){
        digitalWrite(speedA, LOW);
        digitalWrite(speedB, LOW);
        
    }else if(strcmp(payload, "ForwardStop") == 0){
        // move the motor A to one direction increasing speed
          digitalWrite (dirA, HIGH);
          digitalWrite (dirB, HIGH);
          
          analogWrite (speedA, 255);
          analogWrite (speedB, 255);
          delay (fast);
            // stop the motor
         digitalWrite(speedA, LOW);
         digitalWrite(speedB, LOW);

         
    }else if(strcmp(payload, "BackwardStop") == 0){
        // move the motor A to one direction increasing speed
          digitalWrite (dirA, LOW);
          digitalWrite (dirB, LOW);
          
          analogWrite (speedA, 255);
          analogWrite (speedB, 255);
          delay (fast);
          
          // stop the motor
          digitalWrite(speedA, LOW);
          digitalWrite(speedB, LOW);
         
    }else if(strcmp(payload, "LeftStop") == 0){
         //Left
      
      digitalWrite (dirA, LOW);
      digitalWrite (dirB, HIGH);
    
      analogWrite (speedA, 255);
      analogWrite (speedB, 255);
      delay (fast);
      
      // stop the motor
      digitalWrite(speedA, LOW);
      digitalWrite(speedB, LOW);
      
    }else if(strcmp(payload, "RightStop") == 0){
        //Right
        digitalWrite (dirA, HIGH);
        digitalWrite (dirB, LOW);
      
        analogWrite (speedA, 255);
        analogWrite (speedB, 255);
        
        delay (fast);
      
         // stop the motor
        digitalWrite(speedA, LOW);
        digitalWrite(speedB, LOW);
      
    }else if(strcmp(payload, "ForwardFast") == 0){
        // move the motor A to one direction increasing speed
          digitalWrite (dirA, HIGH);
          digitalWrite (dirB, HIGH);
          
          analogWrite (speedA, 255);
          analogWrite (speedB, 255);
          delay (faster);
            // stop the motor
         digitalWrite(speedA, LOW);
         digitalWrite(speedB, LOW);
    }else if(strcmp(payload, "LeftAuto") == 0){
          
      digitalWrite (dirA, LOW);
      digitalWrite (dirB, HIGH);
    
      analogWrite (speedA, 255);
      analogWrite (speedB, 255);
      delay (turn);
      
      // stop the motor
      digitalWrite(speedA, LOW);
      digitalWrite(speedB, LOW);
    }else if(strcmp(payload, "RightAuto") == 0){
        //Right
        digitalWrite (dirA, HIGH);
        digitalWrite (dirB, LOW);
      
        analogWrite (speedA, 255);
        analogWrite (speedB, 255);
        
        delay (turn);
      
         // stop the motor
        digitalWrite(speedA, LOW);
        digitalWrite(speedB, LOW);
    }else{
      //error input
    }
    Serial.write('g');
  }//end of if 
}
