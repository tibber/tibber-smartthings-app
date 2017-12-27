# Tibber Samsung SmartThings integration
Tibber has a "double integration" with Samsung SmartThings:
A. Tibber SmartApp - this is described below
B. [Tibber DeviceHandler in SmartThings](https://github.com/tibbercom/tibber-smartthings-app/tree/master/src/Device%20handlers)  

# How to install
Read an follow the steps below **very carefully**
1. Create a new SmartApp in Samsung SmartThings developer portal and make sure to enable OAuth.
2. From this git repository, copy the contents of src/smartapp. 
3. Open the SmartApp in SmartThings developer portal and paste contents to code section.
4. **Install in simulator** (NB: this is the crucial part) to retreive the API tokn. 
5. Copy and keep this API token and past it into the Tibber app -> Power-ups -> Samsung SmartThings -> Connect
6. Unistall SmartApp from simulator.

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
