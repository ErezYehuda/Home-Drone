from http.server import BaseHTTPRequestHandler, HTTPServer
#import urllib.request
import socket
import sys
import os, time
import threading, queue
import json
import traceback

#HOST = 'localhost'
HOST = '0.0.0.0'
H = HTTP = 'HTTP'
H_PORT = 8080
A = ANDROID = 'ANDROID'
A_PORT = 8082
R = RP = 'RASPBERRY PI'
R_PORT = 8083
CLOSEOUT = str.encode('CLOSEOUT')
global HTTP_Forward_Target
HTTP_Forward_Target = None
#global HTTP_Should_Serve
#HTTP_Should_Serve = True

class Device:
    def __init__(self, name, port, connection=None, open=False):
        #super(TCP_TRACK).__init__() #Realized this doesn't inherit from anything to super-init
        self.name = name
        self.port = port
        self.open = open
        self.connection = connection

class HTTPForwarder(BaseHTTPRequestHandler):        
    #def serve_forever(self):
    #    print('SERVE FOREVER START')
    #    global HTTP_Should_Serve
    #    print('HTTP_Should_Serve : {}'.format(HTTP_Should_Serve))
    #    while(HTTP_Should_Serve):
    #        self.handle_request()
    
    #def stop_serving_http(self):
        #self.should_serve = False
        #http.client.HTTPConnection('localhost:8080')

    def do_GET(self):
        self.send_response(200)
        self.send_header("Content-type", "text/plain")
        self.end_headers()
        self.wfile.write(bytes("Get request received", "utf-8"))
        return
        
    def do_POST(self):
        length = int(self.headers['Content-Length'])
        data = self.rfile.read(length).decode('utf-8')
        #print(data)
        data = json.loads(data)
        print(data)
        print(data["COMMAND"])
               
        global HTTP_Forward_Target
        if HTTP_Forward_Target is not None:
            HTTP_Forward_Target.put(str.encode(data["COMMAND"]))
        #sendTCP.send_tcp(str.encode(data["x"]))
        
        self.send_response(200)
        self.send_header("Content-type", "text/plain")
        self.end_headers()        
        self.wfile.write(bytes("TESTING POST","utf-8"))
        return
        
class TCP_OUT(threading.Thread):
    def __init__(self, device, own_q):        
        super(TCP_OUT,self).__init__()
        self.stoprequest = threading.Event()
        self.device = device
        self.own_q = own_q
        #print('init',self.device.name)
        
    def run(self):
        #print('run',self.device.name)
        while not self.stoprequest.isSet() and self.device.open:
            try:
                pass_message = self.own_q.get()
                print(pass_message)
                if pass_message == CLOSEOUT:
                    print('\n---Received kill command for EX-{}---\n'.format(self.device.name))
                else:
                    print('{} FORWARD--->{}'.format(self.device.name, pass_message))
                    #self.device.connection.sendall(pass_message)
                    #self.device.connection.sendall(str.encode('SENDING TO {}'.format(self.device.name)))
                    print('SENDING TO {}'.format(self.device.name))
                    self.device.connection.sendall(pass_message)
            except Exception:
                print('Exception in passing ->{}'.format(self.device.name)) 
                              
        print('EO-',self.device.name)
                
    def join(self, timeout=None):
        self.stoprequest.set()        
        self.own_q.put(CLOSEOUT)        
                
class TCP_IN(threading.Thread):

    def __init__(self, device, own_q, opposite_q):
        super(TCP_IN,self).__init__()
        self.stoprequest = threading.Event()
        self.device = device
        #self.port = port
        self.opposite_q = opposite_q
        self.own_q = own_q
        #self.connection = None
        self.closed = False
        
    def run(self):
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        server_address = (HOST, self.device.port)
        print('Starting TCP entrance for {} on {}:{}'.format(self.device.name, HOST, self.device.port))
        sock.bind(server_address)
        #listening = True
        sock.listen(1)     
        
        #while listening:        
        while not self.stoprequest.isSet():            
            print("Waiting for connection to {}".format(self.device.name))
            if self.device.connection is None or not self.device.open:
                #Establish connection
                (self.device.connection), client_address = sock.accept()
                #Mark connection established
                self.device.open = True
                #Make this device available to receive TCP forwards
                self.forward_to = TCP_OUT(self.device, self.own_q)
                self.forward_to.start()
            
            try:
                print("Connection from", client_address)
                while True:
                    #print(self.device.connection)
                    data = self.device.connection.recv(32)
                    if data:
                        if data == CLOSEOUT:
                            print('\n---Received kill command for IN-{}---\n'.format(self.device.name))
                            self.device.open = False
                            self.forward_to.join()
                            break
                        else:
                            print("<--- {}".format(data))
                            #self.device.connection.sendall(str.encode("Got your message"))
                            self.opposite_q.put(data)
                    else:
                        print("No data from", client_address)
                        break
            except Exception:
                print('Some sort of exception...')                
            finally:
                self.device.connection.close()
                self.device.open = False   
                self.forward_to.join()
                #listening = False
                
    def join(self, timeout=None):
        self.stoprequest.set()
        
        #Attempt to clear out any listening sockets so they can reach the end of their listen loop
        try:
            sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            server_address = (HOST, self.device.port)
            sock.connect(server_address)
            sock.sendall(CLOSEOUT)
            
        finally:
            sock.close()
            self.device.open = False
            print('Closing socket for',self.device.name)
        
        
        super(TCP_IN, self).join(timeout)

class HTTP_TCP(threading.Thread):
    def __init__(self, RP_Q):
        super(HTTP_TCP,self).__init__()
        self.RP_Q = RP_Q
        self.server = HTTPServer((HOST,H_PORT), HTTPForwarder)
        global HTTP_Forward_Target
        HTTP_Forward_Target = self.RP_Q        
        #self.server.serve_forever()
        
    def run(self):
        print('Running HTTP Thread')
        self.server.serve_forever()
        print('---Received kill command for HTTP---')
        
    def join(self):
        #global HTTP_Should_Serve
        #HTTP_Should_Serve = False
        self.server.shutdown()
        #http.client.HTTPConnection('localhost:8080')
        #urllib.request.urlopen('http://localhost:8080').read()
        
def main():
    AQ = queue.Queue() #Queue -> Android device
    RQ = queue.Queue() #Queue -> Raspberry Pi
    
    AD = Device(A, A_PORT)
    RD = Device(R, R_PORT)
    
    A_Listen = TCP_IN(AD, AQ, RQ)
    R_Listener = TCP_IN(RD, RQ, AQ)
    HTTP_to_R = HTTP_TCP(RQ)
    
    A_Listen.start()
    R_Listener.start()
    HTTP_to_R.start()    
    
    input("Press Enter to initiate CLOSEOUT...\n")
    
    A_Listen.join()
    R_Listener.join()
    HTTP_to_R.join()
    
main()
