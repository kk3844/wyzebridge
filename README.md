# Wyze Bridge

Hubitat Community Post: https://community.hubitat.com/t/release-alpha-wyzesense-integration/30009

## Setting up your Pi

1. Install Raspbian and make any required setup changes.  This was developed on an original Model B running Raspbian Buster.
1. Set your PI up with a static IP!  You can use a host name if you'd like, but sticking to IP will keep communications near instant.
1. In /home/pi, download WyzeSensePy: `wget https://github.com/HclX/WyzeSensePy/archive/master.zip`
1. Unpack: `unzip master.zip`
1. Install the required modules: `sudo apt install python-setuptools python-future python-six python-docopt`
1. Install WyzeSensePy: `sudo python setup.py install`
1. Install telnetd: `sudo apt install telnetd`
1. Enable telnetd at boot: `sudo systemctl enable openbsd-inetd`
1. Copy `hubitat.py` to `/home/pi/WyzeSensePy-master`.  This is the default location of WyzeSensePy and due to application dependencies, I am only able to get it to run from this location.  This is essentially a slimmed down version of sample.py that only outputs the sensor data.  It is what the Wyze Telnet Link watches for sensor readings.
1. Go to Hubitat and create a new Wyze Telnet Link device.  You can call it whatever you'd like, for the sake of this demo I'll keep the default name.
1. Enter in the following information:
	1. IP address - this is the IP address of your Raspberry Pi
	1. Telnet Port - leave this at the default of 23 unless you've changed it
	1. Telnet Username - this is the username of the user that runs WyzeSensePy, by default it is 'pi'
	1. Telnet Password - this is the password of the user that runs WyzeSensePy
	1. Path to hubitat.py - set this to `/home/pi/WyzeSensePy-master` unless you've modified the code.
1. Click Save Preferences.

## Connecting the WyzeSense USB Hub

1. Start a telnet session by clicking Connect Telnet and wait until TelnetSession is `Connected` on the right.
1. Next, click the Start Bridge button.  Wait until WyzeHubUSB is `Connected` on the right.  

Sometimes the Start Bridge portion gives weird errors -- simply try it again.  I've yet to figure out why Python fails to start the application once in a while.  You'll see the hub serial, mac, etc in the logs if all is well.

## Setting up the bridge

Setting up your bridge is pretty simple.  Simply plug in the USB device to your Pi and it should show up as /dev/hidraw0.  If you have other hidraw devices, it may show up as hidraw1/2/3/etc.  You should have an orange solid light on the bridge.

## Managing your devices

Unfortunately you must still pair/unpair the devices through the console.  This feature may be added in a future date but a lack in good documentation for Hubitat and my inexperience in device handlers makes this limitation a reality!

### To pair a device:
1. Ensure the Wyze Telnet Link device has been stopped.  To do this, click the Disconnect Telnet button on the device page.  You should see TelnetSession and WyzeHubUSB turn to Disconnected.
1. SSH to your Pi and go to the /home/pi/WyzeSensePy-master directory.
1. Run sample.py: sudo python sample.py (yes, you must use sudo to access the hidraw device)
1. When the list of options appears, press P and then Enter.
1. Press and hold the reset pin on your device until the red light on the sensor flashes.  You should see it appear in the output on the screen as `sensor found`.  Once paired, you'll be sent back to the menu.  You may have to trip the sensor once to see it register.
1. Add more devices at required.  Once done, press X, then Enter to quit.
1. Go back to the Wyze Telnet Link device and click the Connect Telnet button and wait for TelnetSession to read Connected.
1. Once the telnet session is open, click Start Bridge and wait until WyzeHubUSB is Connected.
1. Once the first reading comes across, the Wyze Telnet Link driver will create a new child device for you.  You'll find it listed under the Wyze Telnet Link as a child device.

### To unpair a device:
Perform the same steps for pairing but choose the option to Unpair a device.  When you unpair, you'll enter the device address with it (such as `U 77787F77`) and it will be removed.  Once unpaired, simply remove the child device from Hubitat.  They system will no longer see the device address and try to read the values.

## Credits
A big thanks goes out to HclX for the [WyzeSensePy](https://github.com/HclX/WyzeSensePy) project.  Without it, the Hubitat driver probably wouldn't have been possible!  Okay, maybe it would have, but it sure made it easier!
