# tibber-device-handler
Using tibber api to retreive price and use it in smart app

# How to install
* Retreive your personal access token from https://developer.tibber.com/settings/accesstoken
* Create the device handler from code using tibber-price-device-handler.gy. Insert the access token where it is marked in code.
* Publish the handler.
* Create new device and name it i.e Tibber price. Make sure to use the newly created handler as type.

# Sample Smart-app
In the file tibber-price-example-smart-app.gy you will find a small simple app that reads and listenes to price updates. Based on price it either turns off or on a switch. This app is only meant as basis for understanding how to subscribe the newly created price sensor for price updates.
