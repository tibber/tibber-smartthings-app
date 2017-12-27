# Tibber SmartThings integration
Tibber has a "double integration" with Samsung SmartThings:
1. Tibber SmartApp - this is described below
2. [Tibber DeviceHandler in SmartThings](https://github.com/tibbercom/tibber-smartthings-app/tree/master/src/Device%20handlers)  

# How to install
Create a new smartapp in smartthings developer portal and make sure to enable OAuth.
From this git repository, copy the contents of src/smartapp. 
Open the smartapp in smartthings developer portal and paste contents to code section.
Install in simulator to retreive the API token. Copy and keep this API token. This will be used by Tibber Mobile Application.
Unistall app from simulator.

# Publish
* Publish the app for your self
* Install the app

# Pair in tibber mobile app
* Login to Tibber mobile application and pair with smartthings
* Enter the API token

# Images
<img src="Images/new-smartthings-app-1.png" border="10" />
<img src="Images/new-smartthings-app-1.png" border="10" />
<img src="Images/token.png" border="10" />
