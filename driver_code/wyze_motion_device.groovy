/**
*  Wyze Child Device
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

metadata {
	definition (name: "Wyze Motion Device", namespace: "kk3844", author: "Kelly Koehn") {
		capability "Motion Sensor"
        capability "Battery"
        attribute "signal", "string"
	}
}

def installed() {
	initialize()
}

def updated() {
	initialize()
}

def initialize() {
}
