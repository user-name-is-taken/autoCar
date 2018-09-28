import RPi.GPIO as GPIO
import time
GPIO.setmode(GPIO.BCM)
A=17#UART
GPIO.setup(A,GPIO.IN)
B=4

GPIO.setup(B,GPIO.IN)
#freq=5000
#p= GPIO.PWM(A, freq)
#p.start(50)
def x(k):
    print("pinA")

def y(k):
    print("pinB")

def cleanup():
    GPIO.cleanup()
if __name__=="__main__":
    GPIO.add_event_detect(B,GPIO.FALLING,callback=y,bouncetime=300)
    GPIO.add_event_detect(A,GPIO.FALLING,callback=x,bouncetime=300)
    input("enter anything to stop")
    cleanup()
