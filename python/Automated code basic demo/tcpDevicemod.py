import socket
import sys
import select
import serial
import cv2
import time

cap = cv2.VideoCapture(1)
face_cascade = cv2.CascadeClassifier('haarcascade_frontalface_alt.xml')
ser = serial.Serial('/dev/ttyACM0',115200)
ser.timeout = 0;
tries = 0
directions = ("Left", "Right")
direction = 1
isAuto = False

F = 'Forward'.encode()
R = 'Right'.encode()
B = 'Backward'.encode()
L = 'Left'.encode()
S = 'Stop'.encode()
RS = 'RightStop'.encode()

def drive(gray, tries, directions, direction):
	faces = face_cascade.detectMultiScale(gray, 1.3, 5)
	img = gray
	text=''
	if len(faces) > 0:
                print "Marker Detected"
		tries = 0
		(x,y,w,h) = faces[0]
		
		cv2.rectangle(img, (x,y),(x+w,y+h),(0255,0),2)
		if h > 300:#target reached sitance
                    print 'NEXT MARKER:%d > 320 ->R'% h    
                    ser.write(R)#
                    cv2.putText(img, "Reached: " + text, (5 , 50), cv2.FONT_HERSHEY_PLAIN, 1, (0,255,0))
                    time.sleep(0.25)
                    ser.write(S)
		if x+w/2 > 640 * 2/3:
                    print 'x+w/2:%d > %d ->R' % ((x+w/2), (640*2/3))
                    ser.write(R)#
                    cv2.putText(img, "RIGHT: " + text, (5 , 50), cv2.FONT_HERSHEY_PLAIN, 1, (0,255,0))
                    time.sleep(0.25)
                    ser.write(S)
		if x+w/2 < 640 * 1/3:
                    print 'x+w/2:%d > %d ->L' % ((x+w/2), (640*1/3))
                    ser.write(L)#
                    cv2.putText(img, "LEFT: " + text, (5 , 50), cv2.FONT_HERSHEY_PLAIN, 1, (0,255,0))
                    time.sleep(0.25)
                    ser.write(S)
		if h <= 320 and x+w/2 <= 640 * 2/3 and x+w/2 >= 640 * 1/3:
                    print 'FACES ELSE ->F'
                    ser.write(F)#
                    cv2.putText(img, "FORWARD: " + text, (5 , 50), cv2.FONT_HERSHEY_PLAIN, 1, (0,255,0))
                    time.sleep(0.25)
                    ser.write(S)
	else :
            print 'Lost'
            ser.write(RS)
	cv2.imshow('img',img)
	cv2.waitKey(1)
	time.sleep(0.5)
	#ser.write(S)
	return tries

def auto_mode():
    global tries
    #print "Marker Detected:",str(suc)
    suc, img = cap.read()
    suc, img = cap.read()
    suc, img = cap.read()
    suc, img = cap.read()
    suc, img = cap.read()
    suc, img = cap.read()
    suc, img = cap.read()
    suc, img = cap.read()
    suc, img = cap.read()
    suc, img = cap.read()
    suc, img = cap.read()
    #gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    suc, img = cap.read()
    tries = drive(img, tries, directions, direction)

def send_tcp(message):
    global isAuto
    HOST = '54.152.236.7'
    H = HTTP = 'HTTP'
    #192.168.42.1
    # Create a TCP/IP socket
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    # Connect the socket to the port where the server is listening
    #server_address = ('localhost', 8083)
    #print(sys.argv[1])
    server_address = (HOST, 8083)
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
			ser.write(S)
		elif isAuto == False:
			print ('wrote to serial')
			ser.write(data.encode())
            if isAuto == True:
		auto_mode();
	    data = ""
	    #print str(ser.read())
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
