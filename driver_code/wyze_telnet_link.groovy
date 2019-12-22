/**
*  Wyze Telnet Link
*
*
*  Copyright 2019 Kelly Koehn
*
*  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License. You may obtain a copy of the License at:
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
*  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
*  for the specific language governing permissions and limitations under the License.
*
*  12/18/2019 - Initial Release - Kelly Koehn
*/

preferences {
		input "ip", "text", title: "IP Address", description: "", required: true, displayDuringSetup: true
		input "port", "text", title: "Telnet Port (23)", description: "", required: true, displayDuringSetup: true
		input "username", "text", title: "Telnet Username", description: "", required: true, displayDuringSetup: true
		input "password", "password", title: "Telnet Password", description: "", required: true, displayDuringSetup: true
        input "wyzeSensePyScriptPath", "text", title: "Path to hubitat.py script (no trailing slash!)", description: "", required: true, displayDuringSetup: true 
}

metadata {
	definition (name: "Wyze Telnet Link", namespace: "kk", author: "Kelly Koehn") {
		capability "Telnet"
        attribute "TelnetSession", "string"
        attribute "WyzeHubUSB", "string"
        attribute "WyzeHubMAC", "string"
        attribute "WyzeHubVer", "string"
        command "disconnectTelnet"
        command "connectTelnet"
        command "startBridge"
	}
}

def installed() {
	initialize()
}

def updated() {
	initialize()
}

def initialize() {
    sendEvent(name: "TelnetSession", value: "Disconnected")
    sendEvent(name: "WyzeHubUSB", value: "Disconnected")
}

def telnetStatus(String status) {
	log.info "telnetStatus - error: ${status}"
	if (status == "receive error: Stream is closed") {
		log.error "Telnet connection dropped..."
		sendEvent(name: "TelnetSession", value: "Disconnected")
		initialize()
	} else {
		sendEvent(name: "TelnetSession", value: "Connected")
	}
}

def parse(String msg) {
    
    //log.debug "Telnet Response = ${msg}"
    if (msg == "permitted by applicable law.") {
        sendEvent(name: "TelnetSession", value: "Connected"); 
        log.debug "Telnet session opened!";
    }
    if (msg.startsWith("\tMAC:")) {
        def macMsgArray = msg.split(":");
        sendEvent(name: "WyzeHubMAC", value: macMsgArray[1]);
        log.debug "Hub MAC: " + macMsgArray[1];
    }
    if (msg.startsWith("\tVER:")) {
        def verMsgArray = msg.split(":");
        sendEvent(name: "WyzeHubVer", value: verMsgArray[1]);
        log.debug "Hub Ver: " + verMsgArray[1];
    }
    if (msg.startsWith("\tENR:")) {
        sendEvent(name:"WyzeHubUSB", value: "Connected");
        log.debug "Wyze Hub connected!";
    }
    if (msg.startsWith("[")) {
        // Message received, parse it!
        
        def parsedMsg = msg
        def parsedMsgArray = parsedMsg.split("]");
        def msgDate = parsedMsgArray[0].substring(1);
        def msgAddress = parsedMsgArray[1].substring(1);
        def msgData = parsedMsgArray[2];
        
        //log.debug msgData;
        
        def msgDataArray = msgData.split(":");
        def sensorType = "";
        def sensorState = "";
        def sensorBattery = "";
        def sensorSignal = "";
        if (msgDataArray[0] == "StateEvent") {
            def sensorDataArray = msgDataArray[1].split(",");
            def sensorTypeArray = sensorDataArray[0].split("=");
            def sensorStateArray = sensorDataArray[1].split("=");
            def sensorBatteryArray = sensorDataArray[2].split("=");
            def sensorSignalArray = sensorDataArray[3].split("=");
            sensorType = sensorTypeArray[1];
            sensorState = sensorStateArray[1];
            sensorBattery = sensorBatteryArray[1];
            sensorSignal = sensorSignalArray[1];

            log.debug "Sensor (" + msgAddress + "): " + msgDate + " - Type: " + sensorType + ", State: " + sensorState + ", Battery: " + sensorBattery + ", Signal: " + sensorSignal;
        
            // Update child device
            def childDevice = getChildDevice(msgAddress);
            
            // Check if child device exists, if not, create it
            if (childDevice == null) {
                log.debug "Child device for " + msgAddress + " doesn't exist -- creating."
                createChildDevice(msgAddress, sensorType);
                childDevice = getChildDevice(msgAddress);
            }
            childDevice.sendEvent(name: "battery",value: sensorBattery);
            childDevice.sendEvent(name: "signal", value: sensorSignal);
            if (sensorType == "motion") {
                childDevice.sendEvent(name: "motion", value: sensorState);
            }
            if (sensorType == "switch") {
                if (sensorState == "close") {
                    sensorState == "closed";
                }
                childDevice.sendEvent(name: "contact", value: sensorState);
            }    
        }
    }
}

def disconnectTelnet() {
    log.debug "Resetting telnet session / initializing";
    telnetClose();
    initialize();
}

def connectTelnet() {
    //log.debug "Connecting to telnet - IP = ${ip}, Port = ${port.toInteger()}, Username = ${username}, Password = ${password}"
	telnetConnect([terminalType: 'VT100'], ip, port.toInteger(), username, password)
}

def startBridge() {
    def msg = "sudo reset && sudo sh -c ${wyzeSensePyScriptPath}/hubitat.py";
    log.debug "Starting Wyze Hub software...";
    sendMsg(msg);
}

def createChildDevice(String deviceId, String deviceType) {
    log.debug "Creating child device: " + deviceId
    
    if (deviceType == "motion") {
        deviceType = "Motion";
    }
    if (deviceType == "switch") {
        deviceType = "Contact";
    }
    
    addChildDevice("kk3844", "Wyze " + deviceType + " Device", "${deviceId}", [label: "Wyze " + deviceType + " Device - " + deviceId]);
}


def sendMsg(String msg) {
    log.debug "Sending msg = ${msg}"
    return new hubitat.device.HubAction(msg, hubitat.device.Protocol.TELNET)
}
