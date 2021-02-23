/**
 *  Tibber Thermostat
 *
 *  Copyright 2017 Tibber
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
 */
definition(
    name: "Tibber price app example",
    namespace: "Tibber",
    author: "Tibberdev",
    description: "Use price to control something.....",
    category: "Convenience",
    iconUrl: "https://images.ctfassets.net/zq85bj8o2ot3/2uYwJJ7x6D4liUfA09Bgkb/8e411caa571e29b9b265298ad23206af/Tibber_Brandicon_-_Square.svg",
    iconX2Url: "https://images.ctfassets.net/zq85bj8o2ot3/2uYwJJ7x6D4liUfA09Bgkb/8e411caa571e29b9b265298ad23206af/Tibber_Brandicon_-_Square.svg",
    iconX3Url: "https://images.ctfassets.net/zq85bj8o2ot3/2uYwJJ7x6D4liUfA09Bgkb/8e411caa571e29b9b265298ad23206af/Tibber_Brandicon_-_Square.svg")

preferences {
  section ("Allow external service to control these things...") {
    input "priceSensor", "capability.sensor", required: true
    input "switch1", "capability.switch", required: false
  }
}
def installed() {
	log.debug "Installed with settings: ${settings}"
	initialize()
}
def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	initialize()
}
def initialize() {
	subscribe(priceSensor, "price", priceHandler)
    def priceValue = priceSensor.currentValue("price");
    def currencyValue = priceSensor.currentValue("currency");
	handlePriceUpdate(priceValue)
}
def priceHandler(evt) {
    def priceValue = priceSensor.currentValue("price");
    def currencyValue = priceSensor.currentValue("currency");
	handlePriceUpdate(priceValue)
}
def handlePriceUpdate(price){
    if(price > 50){ //do something when price is above some value
    	switch1.off()
    }else{
    	switch1.on()
    }
}