import socket
import sys
import serial

ser = serial.Serial('/dev/ttyACM0',115200)
ser.timeout = 0;

def send_tcp(message):
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
	data = sock.recv(8)
	while data is not str.encode("CLOSEOUT"):			         
	    if data:
		print('Response: "{}"'.format(data))
		ser.write(data.encode())
            data = sock.recv(8)

    except Exception:
        print('Exception in device on ',sys.argv[1])        
                
    finally:
        #input('FINISH?')
        sock.sendall(b"CLOSEOUT")
        sock.close()
        print('closing socket')
        
send_tcp(b"Testing")
