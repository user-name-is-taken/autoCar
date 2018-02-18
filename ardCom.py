"""https://stackoverflow.com/questions/26946337/reading-serial-data-from-arduino-project-pyserial#26946917
https://stackoverflow.com/questions/17553543/pyserial-non-blocking-read-loop#17564557

-take pic code (arduino to pi)
-tweet pic code (arduino to pi)

"""
import serial
import sys
import time

port = "/dev/serial/by-id/usb-FTDI_FT232R_USB_UART_AE01J6GZ-if00-port0"
myPort = "/dev/serial/by-id/usb-1a86_USB2.0-Serial-if00-port0"
port = myPort
baudrate = 9600

if len(sys.argv) == 3:
    ser = serial.Serial(sys.argv[1], sys.argv[2])
else:
    print ("# Please specify a port and a baudrate")
    print ("# using hard coded defaults " + port + " " + str(baudrate))
    ser = serial.Serial(port, baudrate)

# enforce a reset before we really start
#ser.setDTR(1)
#time.sleep(0.25)
#ser.setDTR(0)

while ser.isOpen():
    if (ser.inWaiting()>0):
        k=ser.readline()
        #sys.stdout.write(ser.readline())
        #sys.stdout.flush()
        print(k)
        #if k == takePic: takePic

def takePic():
    pass

def sendPicToServer():
    pass
