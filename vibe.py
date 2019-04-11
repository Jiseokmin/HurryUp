import socket
import subprocess
import pyfirmata

serverPort = 8888

PORT = '/dev/ttyACM0'
board = pyfirmata.Arduino(PORT)
pin6 = board.get_pin('d:6:p')

serverSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
serverSocket.bind(('',serverPort))
serverSocket.listen(1)

print("The server is ready to receive on port", serverPort)

while True:
    try:
        (connectionSocket, clientAddress) = serverSocket.accept()
        message = connectionSocket.recv(2048)
        r_message = message.decode()
        f_message = float(r_message)
        f_message = (f_message+3)/10
        if f_message > 1.0:
            f_message = 1.0
        print(f_message)
        
        if not r_message:
            replyMessage = 'Error'
            break
        elif(r_message == 'on'):
            subprocess.call('', shell=True)   #function for vibe on
            board.digital[6].write(1)
            print('vibe on')
        elif(r_message == 'off'):
            subprocess.call('', shell=True)   #function for vibe off
            board.digital[6].write(0)
            print('vibe off')      
        else:
            pin6.write(f_message)
      
        #connectionSocket.send(replyMessage.encode())
        connectionSocket.close()
        
    except KeyboardInterrupt:
        print('Server has terminated');
        connectionSocket.close()
        break
        
serverSocket.close()


