# Tibber - Samsung SmartThings thermostat integration
Tibber has a "double integration" with Samsung SmartThings;
1. Tibber SmartApp - this is described below. This integration connects thermostats in SmartThings to Tibber. The SmartThings thermostats will show up as "bubbles" in the Tibber app, and you can enable "smart heating" capabilities in Tibber.
2. [Tibber DeviceHandler in SmartThings](https://github.com/tibbercom/tibber-smartthings-app/tree/master/src/Device%20handlers). This DeviceHandler will show your live energy prices as a device/sensor in SmartThings.

# How to install
Read an follow the steps below **very carefully**
1. Create a new SmartApp in Samsung SmartThings developer portal (https://graph.api.smartthings.com/ide/apps).
1a. Copy the code from git repository, copy the contents of src/smartapp.groovy and paste it under ”from code” in your New SmartApp and click create.
1b. Go to App Settings and enable OAuth, then update your SmartApp.
2. Install the SmartApp in your SmartThings app.
2a. Press + sign in the Smartthings app and choose your SmartApp.
2b. First click Which devices? and choose which thermostats you would like to add to Tibber.
2c. Second, click Access Token and copy the token. Keep it copied for the next step.
3. Go to the Tibber App -> Power Ups -> Samsung SmartThings -> Connect. Paste the token and connect.

# Images
<img src="Images/new-smartthings-app-1.png" border="10" />
<img src="Images/new-smartthings-app-1.png" border="10" />
<img src="Images/token.png" border="10" />
