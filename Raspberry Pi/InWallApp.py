
from __future__ import print_function # WalabotAPI works on both Python 2 an 3.
import time
from sys import platform
from os import system
from imp import load_source
from os.path import join
from bluetooth import *

if platform == 'win32':
	modulePath = join('C:/', 'Program Files', 'Walabot', 'WalabotSDK',
		'python', 'WalabotAPI.py')
elif platform.startswith('linux'):
    modulePath = join('/usr', 'share', 'walabot', 'python', 'WalabotAPI.py')     

wlbt = load_source('WalabotAPI', modulePath)
input = raw_input("Use bluetooth? ")
if input == 'y':
	server_sock=BluetoothSocket(RFCOMM)
	server_sock.bind(("",PORT_ANY))
	server_sock.listen(1)
	port = server_sock.getsockname()[1]
	uuid = "88e2a5aa-1fb9-474a-a915-87457661899f"
	advertise_service( server_sock, "WallHackServer",
		   service_id = uuid,
		   service_classes = [ uuid, SERIAL_PORT_CLASS ],
		   profiles = [ SERIAL_PORT_PROFILE ]
		 )
	print ("Waiting for connection on RFCOMM channel {}".format(port))
	client_sock, client_info = server_sock.accept()
	print ("Accepted connection from ", client_info)
wlbt.Init()


def PrintSensorTargets(targets):
    system('cls' if platform == 'win32' else 'clear')
    if targets:
        for i, target in enumerate(targets):
	    type = "Metal" if (target.amplitude > 3.0) else  "Wood" if (target.amplitude < 1.6) and (target.amplitude > 0.5) else "Wire/PVC" if (target.amplitude > 1.6) and (target.amplitude < 3.0) else "Uknown"
            str = 'Target #{}:\r\ntype: {}\r\nangleDeg: {}\r\nx: {}\r\ny: {}\r\nz: {}\r\nwidth: {}\r\namplitude: {}\r\n'.format(i + 1, type, target.angleDeg, target.xPosCm, target.yPosCm, target.zPosCm, target.widthCm, target.amplitude)
	    print (str)
	    if input == 'y':
		try:
		    client_sock.send(str)
		    time.sleep(1)
	        except IOError:
	            pass
    else:
        print('No Target Detected')

def InWallApp():
    # wlbt.SetArenaX - input parameters
    xArenaMin, xArenaMax, xArenaRes = -1.5, 2, 0.25
    # wlbt.SetArenaY - input parameters
    yArenaMin, yArenaMax, yArenaRes = -3, 2, 0.25
    # wlbt.SetArenaZ - input parameters
    zArenaMin, zArenaMax, zArenaRes = 2, 6, 0.25
    # Configure Walabot database install location (for windows)
    wlbt.SetSettingsFolder()
    # 1) Connects: Establish communication with walabot.
    wlbt.ConnectAny()
    # 2) Configure: Set scan profile and arena
    # Set Profile - to Short-range.
    wlbt.SetProfile(wlbt.PROF_SHORT_RANGE_IMAGING)
    # Set arena by Cartesian coordinates, with arena resolution
    wlbt.SetArenaX(xArenaMin, xArenaMax, xArenaRes)
    wlbt.SetArenaY(yArenaMin, yArenaMax, yArenaRes)
    wlbt.SetArenaZ(zArenaMin, zArenaMax, zArenaRes)
    # Walabot filtering disable
    wlbt.SetDynamicImageFilter(wlbt.FILTER_TYPE_MTI)
    # 3) Start: Start the system in preparation for scanning.
    wlbt.Start()
    # calibrates scanning to ignore or reduce the signals
    wlbt.StartCalibration()
    while wlbt.GetStatus()[0] == wlbt.STATUS_CALIBRATING:
        wlbt.Trigger()
    while True:
        appStatus, calibrationProcess = wlbt.GetStatus()
        # 5) Trigger: Scan (sense) according to profile and record signals
        # to be available for processing and retrieval.
        wlbt.Trigger()
        # 6) Get action: retrieve the last completed triggered recording
       	targets = wlbt.GetImagingTargets()
       #rasterImage, _, _, sliceDepth, power = wlbt.GetRawImageSlice()
        PrintSensorTargets(targets)
        PrintSensorTargets(targets)
    # 7) Stop and Disconnect.
    wlbt.Stop()
    wlbt.Disconnect()
  # client_sock.close()
  # server_sock.close()
    print('Terminate successfully')

if __name__ == '__main__':
    InWallApp()
