import socket
import sys
import select
import serial
import cv2
import time
import threading, Queue
import detectPeople

CLOSE = b'CLOSEOUT'
####RP variables
HOST = '54.152.236.7'
PORT = 8083

global sock
global open
open = False
global autoOn
autoOn = False
####Automation variables
F = 'Forward'.encode()
R = 'Right'.encode()
B = 'Backward'.encode()
L = 'Left'.encode()
FS = 'ForwardStop'.encode()
RS = 'RightStop'.encode()
BS = 'BackwardStop'.encode()
LS = 'LeftStop'.encode()
S = 'Stop'.encode()

FA = 'ForwardFast'.encode()
LA = 'LeftAuto'.encode()
RA = 'RightAuto'.encode()

global correct
correct = False
global lastStep
lastStep = S

global CurrentImg
CurrentImg = None
global CurrentMove
CurrentMove = S
####

class AutoMove(threading.Thread):
    def __init__(self):
        super(AutoMove,self).__init__()
        #self.MQ = MQ
        self.alive = True
        self.ser = serial.Serial('/dev/ttyACM0',115200)
        self.ser.timeout = None;

    def run(self):
        while self.alive:
            command = CurrentMove#self.MQ.get()
            if command is CLOSE:
                print 'AutoMove -> CLOSE'                
                self.alive = False                
                break;
            ##else:
            elif command:
                self.ser.write(command)
                self.ser.read()
            #print command

    def join(self, timeout=None):        
        self.alive = False
        CurrentMove = CLOSE#self.MQ.put(CLOSE)
        self.ser.write(S)
        #print 'CLOSING AutoMove'
        print 'Closing AutoMove'
        super(AutoMove,self).join(timeout)
######################################
class Video(threading.Thread):
    def __init__(self):
        super(Video,self).__init__()
        self.alive = True
        self.cap = cv2.VideoCapture(1)

    def run(self):
        global CurrentImg        
        while self.alive:            
            succ, CurrentImg = self.cap.read()

    def join(self,timeout=0):
        self.alive = False
        CurrentImg = CLOSE
        print 'Closing Video'
        super(Video,self).join(timeout)
######################################        
class Analyze(threading.Thread):
    def __init__(self, NQ):
        super(Analyze,self).__init__()
        self.NQ = NQ
        self.alive = True        
        self.face_cascade = cv2.CascadeClassifier('haarcascade_frontalface_alt.xml')
        self.video = Video()
        self.video.start()

    def run(self):
        global CurrentImg
        while self.alive:
            getImg = CurrentImg
            if getImg is not None and getImg is not CLOSE:
                #time.sleep(0.35)
                #suc, img = self.cap.read()
                faces = self.face_cascade.detectMultiScale(getImg, 1.3, 5)            
                self.NQ.put((faces,getImg))
               # print '%d faces' % (len(faces),)

    def join(self, timeout=None):
        self.alive = False
        self.video.join()
        #self.cap.close()
        print 'Closing Analyze'
        super(Analyze,self).join(timeout)
######################################
class Navigate(threading.Thread):
    def __init__(self,NQ):
        super(Navigate,self).__init__()
        self.alive = True
        self.NQ = NQ
        print 'NAV STARTED'

    def run(self):
        global CurrentMove
        global correct
        global lastStep
        text = ''
        while self.alive:
            faces,img = self.NQ.get()
            if faces is CLOSE:
                break
            if len(faces):
                correct = True
                #print "Marker Detected"
                tries = 0
                (x,y,w,h) = faces[0]                
                #cv2.rectangle(img, (x,y),(x+w,y+h),(0255,0),2)
                if h > 300:#target reached sitance
                    print 'NEXT MARKER:%d > 320 ->R'% h    
                    CurrentMove = RA#MQ.put(RS)# ser.write(R)#                    
                    ##cv2.putText(img, "Reached: " + text, (5 , 50), cv2.FONT_HERSHEY_PLAIN, 1, (0,255,0))
                    #time.sleep(0.25)
                    #MQ.put(S)#ser.write(S)
                    lastStep = RA
                if x+w/2 > 640 * 2/3:
                    print 'x+w/2:%d > %d ->R' % ((x+w/2), (640*2/3))
                    CurrentMove = FA#MQ.put(RS)#ser.write(R)#
                    ##cv2.putText(img, "RIGHT: " + text, (5 , 50), cv2.FONT_HERSHEY_PLAIN, 1, (0,255,0))
                    #time.sleep(0.25)
                    #MQ.put(S)#ser.write(S)
                    lastStep = LA
                if x+w/2 < 640 * 1/3:
                    print 'x+w/2:%d > %d ->L' % ((x+w/2), (640*1/3))
                    CurrentMove = FA#MQ.put(LS)#ser.write(L)#
                    ##cv2.putText(img, "LEFT: " + text, (5 , 50), cv2.FONT_HERSHEY_PLAIN, 1, (0,255,0))
                    #time.sleep(0.25)
                    #MQ.put(S)#ser.write(S)
                    lastStep = RA
                if h <= 320 and x+w/2 <= 640 * 2/3 and x+w/2 >= 640 * 1/3:
                    print 'x+w/2:%d > %d ->F' % ((x+w/2), (640*1/3))
                    CurrentMove = FA#MQ.put(FS)#ser.write(F)#
                    ##cv2.putText(img, "FORWARD: " + text, (5 , 50), cv2.FONT_HERSHEY_PLAIN, 1, (0,255,0))
                    #time.sleep(0.25)
                    #MQ.put(S)#ser.write(S)
                    lastStep = LA
            else:
                if correct:
                    correct = False
                    CurrentMove = lastStep
                else:
                    CurrentMove = RA#MQ.put(RS)#ser.write(RS)
                print 'Lost: ' + CurrentMove
            ##cv2.imshow('img',img)
            ##cv2.waitKey(1)

    def join(self,timeout=None):
        self.alive = False
        self.NQ.put((CLOSE,None))
        CurrentMove = CLOSE#self.NQ.put(CLOSE)
        print 'Closing Navigate'
        super(Navigate,self).join(timeout)
######################################

class Move(threading.Thread):
    def __init__(self, MQ):
        super(Move,self).__init__()
        self.MQ = MQ
        self.alive = True
        self.ser = serial.Serial('/dev/ttyACM0',115200)
        self.ser.timeout = 0;

    def run(self):
        while self.alive:
            command = self.MQ.get()
            if not command or command is CLOSE:
                self.alive = False
            else:
                print '%s -> serial' % command
                self.ser.write(command.encode())
            #print str(command)

    def join(self, timeout=None):        
        self.MQ.put(CLOSE)
        #print 'CLOSING MOVE'
        super(Move,self).join(timeout)

class TCP_OUT(threading.Thread):
    def __init__(self, out_Q):
        super(TCP_OUT,self).__init__()
        self.out_Q = out_Q
        self.alive = True

    def run(self):
        global sock
        global open
        while self.alive:
            #print 'TCP_OUT RUN\n'
            out = self.out_Q.get()
            if out is CLOSE:
                print 'TCP_OUT CLOSEOUT'
            else:
                print out
                if open:
                    sock.sendall(out)
            #break

    def join(self, timeout=None):
        print 'CLOSING TCP_OUT\n'
        self.alive = False
        self.out_Q.put(CLOSE)
        super(TCP_OUT, self).join(timeout)

class TCP_IN(threading.Thread):
    def __init__(self, MQ, out_Q, AIQ):
        super(TCP_IN,self).__init__()
        self.MQ = MQ
        self.out_Q = out_Q
        self.AIQ = AIQ
        self.alive = True
        self.out_thread = None

    def run(self):
        global sock
        global open
        global autoOn
        error = False
        printConnected = True
        while self.alive:
            try:
                if not open:
                    if printConnected:
                        print 'Not open, connecting\n'
                        printConnected = False
                    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                    sock.connect((HOST,PORT))
                    open = True
                    
                    print 'Connected to',(HOST,PORT)
                    printConnected = True
                if self.alive and self.out_thread is None:
                    self.out_thread = TCP_OUT(self.out_Q)
                    self.out_thread.start()
                data = sock.recv(8)
                if data:
                    #print 'Response: "%s"\n' % data
                    if data is CLOSE:
                        self.alive = False
                        #print Str(CLOSE)+'\n'
                    if data == 'AUTOON' and not autoOn:
                        print '!AUTOON!'
                        self.AIQ.put('Start')
                    elif data == 'AUTOOFF' and autoOn:
                        print '!AUTOOFF!'
                        self.AIQ.put('Stop')
                    elif not autoOn:
                        self.MQ.put(data)
                error = False
            except:
                if not error:                
                    print 'Some error with received command'            
                    error = True                    
                open = False
                if self.out_thread is not None:
                    self.out_thread.join()
                    self.out_thread = None
        print '-EO TCP_IN'

    def join(self, timeout=None):
        print 'CLOSING TCP_IN\n'
        self.alive = False
        global sock
        global open
        open = False
        sock.shutdown(socket.SHUT_RDWR)
        sock.close()
        self.out_thread.join()
        self.out_thread = None
        super(TCP_IN, self).join(timeout)

class AI(threading.Thread):
    def __init__(self, IQ, MQ, out_Q, autoStart = False):
        super(AI,self).__init__()
        #self.cap = cv2.VideoCapture(1)
        self.MQ = MQ
        self.out_Q = out_Q
        self.IQ = IQ
        self.IQ.put('Stop')
        self.nav_Q = Queue.Queue()
        self.move = None
        self.analyze = None
        self.navigate = None
        if autoStart:
            print 'AutoStart'
            self.IQ.put('Start') 
        global autoOn
        autoOn = autoStart
        self.alive = True

    def run(self):
        global CurrentImg
        global autoOn
        error = False
        while self.alive:
            if not self.IQ.empty(): #If there's a stop command     
                if autoOn:
                    if self.move:
                        self.move.join()
                        self.move = None
                    if self.analyze:
                        self.analyze.join()
                        self.analyze = None 
                    if self.navigate:
                        self.navigate.join()
                        self.navigate = None
                autoOn = False #Pause
                self.IQ.get() #Clear stop command
                if not self.alive:
                    break
                self.IQ.get() #Wait for continue command
                if not autoOn and self.alive:
                    if not self.move:
                        self.move = AutoMove()
                        self.move.start()
                    while not self.nav_Q.empty():
                        self.nav_Q.get()
                    if not self.analyze:
                        self.analyze = Analyze(self.nav_Q)
                        self.analyze.start()
                    if not self.navigate:
                        self.navigate = Navigate(self.nav_Q)
                        self.navigate.start()                        
                autoOn = True
            if not self.alive:
                break
            #suc, img = self.cap.read()
            img = CurrentImg
            people = []
            try:
                people = detectPeople.analyze(img)
            except:
                if not error:
                    print 'Error with video feed'
                    error = True
            if(len(people)):
                error = False
                print 'Saw %d people' % len(people)
                self.out_Q.put('NOTIFY:Saw %s people' % len(people))

    def join(self, timeout=False):
        self.alive = False
        #self.cap.release()
        self.IQ.put('Stop')
        self.IQ.put('Start')
        super(AI,self).join(timeout)

def main():     
    TOQ = Queue.Queue() #TCP_OUT queue
    MQ = Queue.Queue() #Move queue
    AIQ = Queue.Queue() #AI queue

    move = Move(MQ) #Only reads from MQ
    t_in = TCP_IN(MQ,TOQ,AIQ) #Writes to MQ,
    #TCP_IN creates TCP_OUT that reads from TOQ (for connection synchronization)
    ai = AI(AIQ, MQ, TOQ) #Reads from AIQ, writes to MQ and TOQ

    move.start()
    t_in.start()
    ai.start()

    raw_input('Press Enter to close\n')
    
    move.join()
    t_in.join()
    ai.join()       

main()
