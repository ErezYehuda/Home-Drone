import socket
import sys
import select
import serial
import cv2
import time
import detectPeople

cap = cv2.VideoCapture(1)
face_cascade = cv2.CascadeClassifier('haarcascade_frontalface_alt.xml')
ser = serial.Serial('/dev/ttyACM0',115200)
ser.timeout = 0.5;
tries = 0
directions = ("Left", "Right")
direction = 1
isAuto = False

def drive(gray, tries, directions, direction):
	faces = face_cascade.detectMultiScale(gray, 1.3, 5)
	ser.write("Stop".encode())
	if len(faces) > 0:
		tries = 0
		(x,y,w,h) = faces[0]
		if h > 320:#target reached sitance
                    ser.write("Right".encode())#cv2.putText(img, "Reached: " + text, (5 , 50), cv2.FONT_HERSHEY_PLAIN, 1, (0,255,0))
		elif x+w/2 > 640 * 2/3:
                    ser.write("Right".encode())#cv2.putText(img, "RIGHT: " + text, (5 , 50), cv2.FONT_HERSHEY_PLAIN, 1, (0,255,0))
		elif x+w/2 < 640 * 1/3:
                    ser.write("Left".encode())#cv2.putText(img, "LEFT: " + text, (5 , 50), cv2.FONT_HERSHEY_PLAIN, 1, (0,255,0))
		else:
                    ser.write("Forward".encode())#cv2.putText(img, "FORWARD: " + text, (5 , 50), cv2.FONT_HERSHEY_PLAIN, 1, (0,255,0))
	elif tries < 10:
            ser.write("Stop".encode())
	    tries = tries + 1
	else :
		tries = tries + 1
		if tries > 14:
		    tries = 8
		ser.write(directions[direction].encode())
	
	#time.sleep(0.5)
	
	return tries

def auto_mode():
    global tries
    suc, img = cap.read()
    print "capture is " + str(suc)
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    tries = drive(gray, tries, directions, direction)
    people = detectPeople.analyze(img)
    if(len(people)):
        print 'Saw %d people' % len(people)
    return len(people)

def send_tcp(message):
    global isAuto
    HOST = '54.152.236.7'
    H = HTTP = 'HTTP'
    #192.168.42.1
    # Create a TCP/IP socket
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    # Connect the socket to the port where the server is listening
    #server_address = ('localhost', 8083)
    print(sys.argv[1])
    server_address = (HOST, int(sys.argv[1]))
    #server_address = ('52.201.232.234', 80)
    print('connecting to %s port %s' % server_address)
    sock.connect(server_address)
    try:
        
        # Send data
        #message = b'This is the message.  It will be repeated.'
        print('---> "%s"' % message)
        sock.sendall(message)
        #sock.sendall('')

        # Look for the response        
	#data = sock.recv(8)
	data = ""
	while data is not str.encode("CLOSEOUT"):
            
	    is_readable = [sock]
            is_writable = []
            is_error = []
            r, w, e = select.select(is_readable, is_writable, is_error, 0.1)
	    
	    if r:
                #tries = drive(gray, tries, directions, direction)
		print ('we have data in R')
		data = sock.recv(8)			       
            if data:
		print ('Response: "{}"'.format(data))
		if data == "AUTOON":
			isAuto = True
		elif data == "AUTOOFF":
			isAuto = False
		elif isAuto == False:
			print ('wrote to serial')
			ser.write(data.encode())
            if isAuto == True:
                people = auto_mode()
                if people:
                    sock.sendall(str.encode('NOTIFY:Detected %d people' % people))
	    data = ""
	    print str(ser.read())
	    #else:
		#data = sock.recv(8)
		
    #except Exception:
    #    print('Exception in device on ',sys.argv[1])        
                
    finally:
        #input('FINISH?')
        sock.sendall(b"CLOSEOUT")
        sock.close()
        print('closing socket')
        
send_tcp(b"Testing")
