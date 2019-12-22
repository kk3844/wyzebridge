#!/usr/bin/env python

"""Hubitat linking script for WyzeSense USB bridge.

**Usage:** ::
  hubitat.py [options]

**Options:**

    -d, --debug     output debug log messages to stderr
    -v, --verbose   print and log more information
    --device PATH   USB device path [default: /dev/hidraw0]

**Examples:** ::

  hubitat.py --device /dev/hidraw0   # Using WyzeSense USB bridge /dev/hidraw0

"""
from __future__ import print_function

from builtins import input

import os
import re
import sys
import logging
import errno
import binascii
import wyzesense

def on_event(ws, e):
    s = "[%s][%s]" % (e.Timestamp.strftime("%Y-%m-%d %H:%M:%S"), e.MAC)
    if e.Type == 'state':
        s += "StateEvent: sensor_type=%s, state=%s, battery=%d, signal=%d" % e.Data
    else:
        s += "RawEvent: type=%s, data=%r" % (e.Type, e.Data)
    print(s)

def main(args):

    device = args['--device']
    print("Opening wyzesense gateway [%r]" % device)
    try:
        ws = wyzesense.Open(device, on_event)
        if not ws:
            print("Open wyzesense gateway failed")
            return 1
        print("Gateway info:")
        print("\tMAC:%s" % ws.MAC)
        print("\tVER:%s" % ws.Version)
        print("\tENR:%s" % binascii.hexlify(ws.ENR))
    except IOError:
        print("No device found on path %r" % device)
        return 2
    
    def HandleCmd():
        return True

    try:
        while HandleCmd():
            pass
    finally:
        ws.Stop()

    return 0

if __name__ == '__main__':
    logging.basicConfig(format='%(levelname)s %(asctime)s %(message)s')

    try:
        from docopt import docopt
    except ImportError:
        sys.exit("the 'docopt' module is needed to execute this program")

    # remove restructured text formatting before input to docopt
    usage = re.sub(r'(?<=\n)\*\*(\w+:)\*\*.*\n', r'\1', __doc__)
    sys.exit(main(docopt(usage)))
