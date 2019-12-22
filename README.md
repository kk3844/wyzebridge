# Wyze Bridge

## Setting up your Pi

1. Install Raspbian and make any required setup changes.  This was developed on an original Model B running Raspbian Buster.
1. Set your PI up with a static IP!  You can use a host name if you'd like, but sticking to IP will keep communications near instant.
1. In /home/pi, download WyzeSensePy: wget https://github.com/HclX/WyzeSensePy/archive/master.zip
1. Unpack: unzip master.zip
1. Install the required modules: sudo apt install python-setuptools python-builtins python-future python-six python-docopt
1. Install WyzeSensePy: sudo python setup.py install
1. Install telnetd: sudo apt install telnetd
1. Enable telnetd at boot: sudo systemctl enable openbsd-inetd
1. Copy hubitat.py to /home/pi/WyzeSensePy-master.  This is the default location of WyzeSensePy and due to application dependencies, I am only able to get it to run from this location.  This is essentially a slimmed down version of sample.py that only outputs the sensor data.  It is what the Wyze Telnet Link watches for sensor readings.

## Setting up the bridge

Setting up your bridge is pretty simple.  Simply plug in the USB device to your Pi and it should show up as /dev/hidraw0.  If you have other hidraw devices, it may show up as hidraw1/2/3/etc.  You should have an orange solid light on the bridge.

## Managing your devices

Unfortunately you must still pair the devices through the console.  This feature may be added in a future date but a lack in good documentation for Hubitat and my inexperience in device handlers makes this limitation a reality!

### To add a device:
1. Ensure the Wyze Telnet Link device has been stopped.  To do this, click the Disconnect Telnet button on the device page.  You should see TelnetSession and WyzeHubUSB turn to Disconnected.
1. SSH to your Pi and go to the /home/pi/WyzeSensePy-master directory.
1. Run sample.py: sudo python sample.py (yes, you must use sudo to access the hidraw device)
1. When the list of options appears, press P and then Enter.
1. Press and hold the reset pin on your device until the light turns solid.  You should see it appear in the output on the screen.  Once paired, you'll be sent back to the menu.  You may have to trip the sensor once to see it register.
1. Add more devices at required.  Once done, press X, then Enter to quit.
1. Go back to the Wyze Telnet Link device and click the Connect Telnet button and wait for TelnetSession to read Connected.
1. Once the telnet session is open, click Start Bridge and wait until WyzeHubUSB is Connected.
1. More steps here -- not documented yet.

### To delete a device:
Perform the same steps for adding but choose the option to Unpair a device.  Once unpaired, simply remove the child device from Hubitat.  They system will no longer see the device address and try to read the values.


