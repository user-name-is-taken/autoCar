import serial
import time
read
class ser(object):
    def __init__(self):
        """this works"""
        self.pilite=serial.Serial("/dev/ttyS0",baudrate=2000,timeout=.01)
        #pilite.open()
        self.pilite.setRTS(1)
        #http://www.arcelect.com/rs232.htm
#https://pyserial.readthedocs.io/en/latest/tools.html
#http://stackoverflow.com/questions/2438848/set-serial-port-pin-high-using-python#4697687
    
#https://electrosome.com/uart-raspberry-pi-python/
    """set and get timeout,"""
    def loop_write(self,message,steps, tim=.1):
        for i in range(steps):
            time.sleep(tim)#sleeps
            self.pilite.write(bytes(message))#sends
            print(self.pilite.read(1),i)

