import socket
import sys
import select
import serial
import cv2
import time
import threading, Queue
import detectPeople

CLOSE = b'CLOSEOUT'
HOST = '54.152.236.7'
PORT = 8083

global sock
#sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
global open
open = False
global autoOn
autoOn = False

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
        self.cap = cv2.VideoCapture(1)
        #self.tries = 0
        #self.directions = ("Left", "Right")
        #self.direction = 1
        self.MQ = MQ
        self.out_Q = out_Q
        self.IQ = IQ
        self.IQ.put('Stop')
        #self.autoStart = autoStart
        if autoStart:
            print 'AutoStart'
            self.IQ.put('Start') 
        global autoOn
        autoOn = autoStart
        self.alive = True

    def run(self):
        global autoOn
        error = False
        while self.alive:
            if not self.IQ.empty(): #If there's a stop command                
                autoOn = False #Pause
                #print 'Clearing STOP\n'
                self.IQ.get() #Clear stop command
                #print 'AFTER STOP\n'
                #if self.IQ.empty():
                #    print 'Paused AI\n'
                if not self.alive:
                    break
                self.IQ.get() #Wait for continue command
                autoOn = True
            if not self.alive:
                break
            suc, img = self.cap.read()
            #self.gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
            #self.tries = drive(gray, self.tries, self.directions, self.direction)
            #print 'Looking for people'
            people = []
            try:
                people = detectPeople.analyze(img)
            except:
                if not error:
                    print 'Error with video feed'
                    error = True
            #print 'Done looking'
            if(len(people)):
                error = False
                print 'Saw %d people' % len(people)
                self.out_Q.put('NOTIFY:Saw %s people' % len(people))
            #print 'Active AI\n'
            #break

    def join(self, timeout=False):
        self.alive = False
        self.cap.release()
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
