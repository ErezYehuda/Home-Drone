import serial
import time

ser = serial.Serial('/dev/ttyACM0',9600)
ser.timeout = 5;
s = [0]
while True:
	read_serial = "";
	#s[0] = str(int (ser.readline(),16))
	ser.write("Forward".encode());
	time.sleep(2)
	ser.write("Stop".encode());
	time.sleep(2);
	ser.write("Left".encode());
        time.sleep(2);
	ser.write("Stop".encode());
        time.sleep(2);
	ser.write("Backward".encode());
        time.sleep(2);
	ser.write("Stop".encode());
        time.sleep(2);


	#while((read_serial.strip() == "") ):
	#	read_serial=ser.readline()
	#print read_serial
	#ser.write("Forward".encode());
	
