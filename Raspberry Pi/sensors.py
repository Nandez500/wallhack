import sys
import smbus2
from smbus2 import SMBus
import time
bus = smbus2.SMBus(1)

#REGISTER DECLARATIONS
ACCEL = 0x6b
WHO = 0x0f
ACC_CTRL = 0x20
STAT = 0x27
accX_L = 0x28
accX_H = 0x29
accY_L = 0x2a
accY_H = 0x2b
accZ_L = 0x2c
accZ_H = 0x2d

#variable declarations
x_acc = 0
y_acc = 0
z_acc = 0
x = 0
y = 0
z = 0

#GET DATA FROM IMU DEVICE
def getAccData():
	#data collected from high/low registers
	x_low = bus.read_byte_data(ACCEL,accX_L)
	x_hi  = bus.read_byte_data(ACCEL,accX_H)
	x_acc = ( x_hi << 8 ) + x_low
	y_low = bus.read_byte_data(ACCEL,accY_L)
	y_hi  = bus.read_byte_data(ACCEL,accY_H)
	y_acc = ( y_hi << 8 ) + y_low
	z_low = bus.read_byte_data(ACCEL,accZ_L)
	z_hi  = bus.read_byte_data(ACCEL,accZ_H)
	z_acc = ( z_hi << 8 ) + z_low
	x = twosComp(x_acc)
	y = twosComp(y_acc)
	z = twosComp(z_acc)
	print( bin(bus.read_byte_data(ACCEL,STAT)), x, y )

def twosComp(b):
	if(b & 0x80):
		return (-1)*((b^0xffff)+1)
	return b

#LOAD SETTINGS ONTO IMU IF PRESENT
def accEnable():
	try:
		if(bus.read_byte_data(ACCEL,WHO)==0x68):
			acc_check = True
		print("IMU BOOT SEQUENCE START...", end="")
		bus.write_byte_data(ACCEL,ACC_CTRL,0xd8)	#load setting to control register
		if(bus.read_byte_data(ACCEL,ACC_CTRL)==0xd8):
			print("		[ DONE ]")
			acc_check = True
		else:
			print("		[ FAILED ]")
	except OSError as err:
		print("IMU NOT FOUND... {0}".format(err))

### MAIN BELOW ###
if __name__ == "__main__":
	print("Beginning sensor setup...\n")
	accEnable()
	time.sleep(1)
	while True:
		getAccData()
		time.sleep(.5)
