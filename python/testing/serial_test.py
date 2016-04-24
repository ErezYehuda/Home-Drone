import serial
import time

ser = serial.Serial('/dev/ttyACM0',115200)
ser.timeout = 0;
s = [0]
while True:
	read_serial = "";
	#s[0] = str(int (ser.readline(),16))
	ser.write("Forward".encode());
	time.sleep(1)
	ser.write("Stop".encode());
	#while((read_serial.strip() == "") ):
	time.sleep(1);
        
	
