/**
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
 *  Tibber price
 *
 *  Author: tibberdev
 *
 *  Date: 2018-01-11
 *
 *  Revision notes: price is now given in currency/100. For NOK. the unit is now in Øre
 *  Revision notes: presenting price as energymeter, user can get price through energy attribute
 */
metadata {
	definition (name: "Tibber price device handler", namespace: "Tibber", author: "tibberdev") {
		capability "Sensor"
		capability "Energy Meter"
		attribute "price", "number"
		attribute "priceNextHour", "number"
		attribute "priceNextHourLabel", "string"
		attribute "pricePlus2Hour", "number"
		attribute "pricePlus2HourLabel", "string"
		attribute "priceMaxDay", "number"
		attribute "priceMaxDayLabel", "string"
		attribute "priceMinDay", "number"
		attribute "priceMinDayLabel", "string"
		attribute "currency", "string"
	}
	// tile definitions
	tiles(scale: 2) {
        multiAttributeTile(name:"valueTile", type:"generic", width:6, height:4, backgroundColor:"#00a0dc") {
            tileAttribute("device.price", key: "PRIMARY_CONTROL") {
                attributeState "price", label:'${currentValue}', defaultState: true,
                backgroundColors:backgroundColors()
            }
            tileAttribute("device.currency", key: "SECONDARY_CONTROL") {
                attributeState "currency", label:'${currentValue}', defaultState: true
            }
        }
        
        valueTile("priceNextHourLabelTile", "device.priceNextHourLabel", decoration: "flat", width: 4, height: 1) {
            state "priceNextHourLabel", label:'${currentValue}'
        }
        valueTile("priceNextHourTile", "device.priceNextHour", decoration: "flat", width: 2, height: 1) {
            state "priceNextHour", label:'${currentValue}',                        
            backgroundColors:backgroundColors()
        }
        valueTile("pricePlus2HourLabelTile", "device.pricePlus2HourLabel", decoration: "flat", width: 4, height: 1) {
            state "pricePlus2HourLabel", label:'${currentValue}'
        }
        valueTile("pricePlus2HourTile", "device.pricePlus2Hour", decoration: "flat", width: 2, height: 1) {
            state "pricePlus2Hour", label:'${currentValue}',
            backgroundColors:backgroundColors()
        }
        valueTile("priceMaxDayLabelTile", "device.priceMaxDayLabel", decoration: "flat", width: 4, height: 1) {
            state "priceMaxDayLabel", label:'${currentValue}'
        }
        valueTile("priceMaxDayTile", "device.priceMaxDay", decoration: "flat", width: 2, height: 1) {
            state "priceMaxDay", label:'${currentValue}',
            backgroundColors:backgroundColors()
        }
        valueTile("priceMinDayLabelTile", "device.priceMinDayLabel", decoration: "flat", width: 4, height: 1) {
            state "priceMinDayLabel", label:'${currentValue}'
        }        
        valueTile("priceMinDayTile", "device.priceMinDay", decoration: "flat", width: 2, height: 1) {
            state "priceMinDay", label:'${currentValue}',
            backgroundColors:backgroundColors()
        }
        main (["valueTile"])
        details(["valueTile", "PriceEnergyTile", "priceNextHourLabelTile", "priceNextHourTile", "pricePlus2HourLabelTile", "pricePlus2HourTile","priceMaxDayLabelTile", "priceMaxDayTile","priceMinDayLabelTile", "priceMinDayTile"])
	}

    preferences {
        input (
		title: "Tibber API key",
		description: "Tap to find the API key",
		url: "https://developer.tibber.com/settings/accesstoken",
		type: "href",
		element: "href"
	)
        input (
            name: "tibber_apikey",
            type: "password",
            title: "API Key",
            description: "Enter the Tibber API key",
            required: true,
            displayDuringSetup: true
        )
    }
}

def initialize() {
	state.price = 100;
	log.debug("init")
    getPrice()
    schedule("0 2 * * * ?", getPrice)
}

def installed() {
	log.debug "Installed"
    initialize()
}

def updated() {
	log.debug "Updated"
    initialize()
}

def getPrice() {
	log.debug("getPrice")
    if(tibber_apikey == null){
        log.error("API key is not set. Please set it in the settings.")
    } else {
        def params = [
            uri: "https://api.tibber.com/v1-beta/gql",
            headers: ["Content-Type": "application/json;charset=UTF-8" , "Authorization": "Bearer $tibber_apikey"],
            body: graphQLApiQuery()
        ]
        try {
            httpPostJson(params) { resp ->
                if(resp.status == 200){
                    def today = resp.data.data.viewer.homes[0].currentSubscription.priceInfo.today
                    def tomorrow = resp.data.data.viewer.homes[0].currentSubscription.priceInfo.tomorrow

                    def price = Math.round(resp.data.data.viewer.homes[0].currentSubscription.priceInfo.current.total * 100)
                    def priceMaxDay = Math.round(MaxValue(today) *100)
                    def priceMaxDayLabel = "Max price @ ${MaxValueTimestamp(today)}"
                    def priceMinDay = Math.round(MinValue(today) *100)
                    def priceMinDayLabel = "Min price @ ${MinValueTimestamp(today)}"

                    def priceList = today
                    tomorrow.each{
                        priceList << it
                    }
                    def priceNextHours = PriceNextHours(priceList)
                    def priceNextHour = Math.round(priceNextHours[0] *100)
                    def priceNextHourLabel = "@ ${priceNextHours[2]}"
                    def pricePlus2Hour = Math.round(priceNextHours[1] *100)
                    def pricePlus2HourLabel = "@ ${priceNextHours[3]}"
                    def currency = resp.data.data.viewer.homes[0].currentSubscription.priceInfo.current.currency

                    currency = "${currency}: ${currencyToMinor(currency)}/kWh"

                    state.currency = currency
                    state.price = price
                    state.priceNextHour = priceNextHour
                    state.priceNextHourLabel = priceNextHourLabel
                    state.pricePlus2Hour = pricePlus2Hour
                    state.pricePlus2HourLabel = pricePlus2HourLabel
                    state.priceMaxDay = priceMaxDay
                    state.priceMaxDayLabel = priceMaxDayLabel
                    state.priceMinDay = priceMinDay
                    state.priceMinDayLabel = priceMinDayLabel

                    sendEvent(name: "energy", value: price, unit: currency)
                    sendEvent(name: "price", value: state.price, unit: currency)
                    sendEvent(name: "priceNextHour", value: state.priceNextHour, unit: currency)
                    sendEvent(name: "pricePlus2Hour", value: state.pricePlus2Hour, unit: currency)
                    sendEvent(name: "priceMaxDay", value: state.priceMaxDay, unit: currency)
                    sendEvent(name: "priceMinDay", value: state.priceMinDay, unit: currency)

                    sendEvent(name: "priceNextHourLabel", value: state.priceNextHourLabel)
                    sendEvent(name: "pricePlus2HourLabel", value: state.pricePlus2HourLabel)
                    sendEvent(name: "priceMaxDayLabel", value: state.priceMaxDayLabel)
                    sendEvent(name: "priceMinDayLabel", value: state.priceMinDayLabel)

                    sendEvent(name: "currency", value: state.currency)
                }
            }
        } catch (e) {
            log.debug "something went wrong: $e"
        }
    }
}
def parse(String description) {
    log.debug "parse description: ${description}"
    def eventMap = [
        createEvent(name: "energy", value: state.price, unit: state.currency)
        ,createEvent(name: "price", value: state.price, unit: state.currency)
        ,createEvent(name: "priceNextHour", value: state.priceNextHour, unit: state.currency)
        ,createEvent(name: "pricePlus2Hour", value: state.pricePlus2Hour, unit: state.currency)
        ,createEvent(name: "priceMaxDay", value: state.priceMaxDay, unit: state.currency)
        ,createEvent(name: "priceMinDay", value: state.priceMinDay, unit: state.currency)
        ,createEvent(name: "priceNextHourLabel", value: state.priceNextHourLabel)
        ,createEvent(name: "pricePlus2HourLabel", value: state.pricePlus2HourLabel)
        ,createEvent(name: "priceMaxDayLabel", value: state.priceMaxDayLabel)
        ,createEvent(name: "priceMinDayLabel", value: state.priceMinDayLabel)    
        ,createEvent(name: "currencyLabel", value: state.currency, unit: state.currency)   
    ]
    log.debug "Parse returned ${description}"
    return eventMap
}

def currencyToMinor(String currency){
	def currencyUnit = "";
	switch(currency){
    	case "NOK":currencyUnit = "Øre";break;
        case "SEK":currencyUnit = "Øre";break;
        case "USD":currencyUnit = "Penny";break;
        default: currencyUnit = "";break;
    }
    return currencyUnit;
    
}
def backgroundColors(){
    return [
		[value: 20, color: "#02A701"],
      	[value: 39, color: "#6CCD00"],
      	[value: 59, color: "#ECD400"],
      	[value: 74, color: "#FD6700"],
      	[value: 95, color: "#FE3500"]
    ]
}

def graphQLApiQuery(){
	return '{"query": "{viewer {homes {currentSubscription {priceInfo { current {total currency} today{ total startsAt } tomorrow{ total startsAt }}}}}}", "variables": null, "operationName": null}';
}

def MaxValueTimestamp(List values){
	def max = 0
    def maxTimestamp = ""
	values.each{
    	def timestamp = it.startsAt
        def total = it.total
        if(total>max){
        	max = it.total
            maxTimestamp = timestamp
        }
    }
    return maxTimestamp.substring(11,13)
}
def MaxValue(List values){
	def max = 0
    def maxTimestamp = ""
	values.each{
    	def timestamp = it.startsAt
        def total = it.total
        if(total>max){
        	max = it.total
            maxTimestamp = timestamp
        }
    }
    return max
}

def MinValueTimestamp(List values){
	def min = 1000
    def minTimestamp = ""
	values.each{
    	def timestamp = it.startsAt
        def total = it.total   
        if(it.total<min){
        	min = it.total
            minTimestamp = timestamp
        }
    }
    return minTimestamp.substring(11,13)
}
def MinValue(List values){
	def min = 1000
    def minTimestamp = ""
	values.each{
    	def timestamp = it.startsAt
        def total = it.total   
        if(it.total<min){
        	min = it.total
            minTimestamp = timestamp
        }
    }
    return min
}


def PriceNextHours(List values){
	def priceNowTimestamp = 0
    def priceNextHour = -1;
    def priceNextNextHour = -1;
    def i=0
    values.each{
        Calendar cal=Calendar.getInstance();
        def hourNowUtc = cal.get(Calendar.HOUR_OF_DAY) + 1
        def dayNowUtc = cal.get(Calendar.DAY_OF_MONTH)    
        def startsAt = it.startsAt
        def total = it.total        
        int hourNow = startsAt.substring(11,13) as int
        int dayNow = startsAt.substring(8,10) as int
        int hourOffset = startsAt.substring(20,22) as int
        def timeZoneOperator = startsAt.substring(19,20)
        if(timeZoneOperator=="+"){
            hourNowUtc = hourNowUtc + hourOffset
        }
        if(timeZoneOperator=="-"){
            hourNowUtc = hourNowUtc - hourOffset
        }
        if(hourNowUtc<0){
        	hourNowUtc = hourNowUtc+24 //wrap
            dayNowUtc = dayNowUtc-1
        }
        if(hourNowUtc>23){
        	hourNowUtc = hourNowUtc-24 //wrap
            dayNowUtc = dayNowUtc+1
        }
        if(hourNow == hourNowUtc && dayNow == dayNowUtc ){
        	priceNextHour = it.total
            priceNextNextHour = values[i+1].total   
            priceNowTimestamp = hourNowUtc
        }
    	i++

    }
    
    def priceNextTimestamp = 0
    if(priceNowTimestamp<23)
    	priceNextTimestamp = priceNowTimestamp + 1
    return [priceNextHour, priceNextNextHour, fromToTimestamp(priceNowTimestamp), fromToTimestamp(priceNextTimestamp)]
}
def fromToTimestamp(def timestamp){
	def from = timestamp
    def to = timestamp + 1
    if(to>23){
    	to = 0
    }
    return "${formatTimestamp(from)} - ${formatTimestamp(to)} "
}
def formatTimestamp(def timestamp){
	if(timestamp < 9)
    	return "0${timestamp}"
    return timestamp
}
