Kiwi SDK <a href="https://plus.google.com/communities/112305505734943177774"><img src="https://ssl.gstatic.com/images/icons/gplus-32.png" width="32" height="32" style="padding-left:12px; border: 0; vertical-align: bottom"></a>
========
*A motion recognition library for Android* 

<br>

Contact info@kiwiwearables.com if you are looking to integrate into a commercial product. Terms and Conditions apply.

**For new Android developers**: https://github.com/kiwiwearables/kiwi-sdk/wiki/Instructions-for-integrating

**Javadocs**: http://kiwiwearables.github.io/kiwi-sdk/javadoc/

Download
--------
Add our bintray repo to your repository list in gradle:
```groovy
repositories {
    jcenter()
    maven { url 'http://dl.bintray.com/kiwiwearables/Kiwi-lib' }
}
```

Grab the AAR via Gradle:
```groovy
compile 'com.kiwiwearables:kiwilib:0.3.1b'
```

Examples
--------
Check out the examples below that demonstrate how to use the library

**Initializing the library**

```java
Kiwi kiwiInstance = Kiwi.with(context);
```

**Authenticate your account**

```java
kiwiInstance.initUser("ENTER USERNAME", "ENTER PASSWORD", new KiwiCallback() {
    @Override
    public void onUserInit() {
        // fetch motions or set options
    }
```

**Fetching Motions**

```java
List<Motion> motions = kiwiInstance.getMotions();
```

**Enabling a subset of motions**

You can explicitly enable a subset of loaded motions by sending the Kiwi instance a list of motion IDs to enable. You can get motion IDs from the list of motions as mentioned above.

```java
List<String> motionIds = new ArrayList<String>();
motionIds.add("56be80d09aaf873552ae73a33b964278"); // bicep curl
kiwiInstance.setEnabledMotions(motionIds);
```

**Sending Data to Kiwi library**

Detection engine will run when the sensor data is coming in.

```java
// contains at least 6 values, 
// with the first three being accel x, y, z and the next three being gyro x, y, z
float[] values = ...
DataListener listener = kiwiInstance.mSensorDataListener.listener;
if (listener != null) {
    listener.onDataReceived(values);
}
```

Library options
---------------
```java
kiwiInstance.setSensorUnit(SensorUnits.MS2_AND_RPS);
kiwiInstance.setCallback(mMotionCallback);
kiwiInstance.setWebSocketOption(LoggingOptions.LOG_ONLY);
kiwiInstance.setDebugging(true);
```

**Sensor Units**

The Kiwi library expects you to specify what the units of your sensor data are. There are currently two options:
* Acceleration in ms^2 and Gyroscopic rotation in rad/s
* (**DEFAULT**) Acceleration in Gs and Gyroscopic rotation in deg/s

If it's the former, we will convert the units to Gs and deg/s for use in Kiwi.

```java
kiwiInstance.setSensorUnits(SensorUnits.MS2_AND_RPS);
```

**Detection callback**

The Kiwi library needs to know how to let the app know when it has detected a motion. For this reason, there is a DetectionInfo class containing the Motion object as well as the detection score (in float).

There is also onScoreAvailable, which provides a way to get scores as they are calculated by the library. You may or may not override this method as you see fit.

```java
DetectionCallback mMotionCallback = new DetectionCallback() {
            @Override
            public void onMotionDetected(DetectionInfo info) {
                Log.d(TAG, "Detection: " + info.motion.motionName);
            }
            
            @Override
            public void onScoreAvailable(DetectionInfo info) {
                Log.d(TAG, "Score: " + info.score + " for " + info.motion.motionName);
            }
        };

kiwiInstance.setCallback(mMotionCallback);
```


**Web socket logging**

You can also use our Developer panel to monitor the sensor data being sent to the library. There are three options: 
* LOG_ONLY: Log the data and not run the detection engine
* LOG_ENABLED: Log as well as run the detection engine
* LOG_DISABLED: Only run the detection engine

```java
kiwiInstance.setWebSocketOption(LoggingOptions.LOG_ONLY);
```

**Debugging**

Setting the debugging flag to true will print some debug log related to incoming data and its detection. It will also send a buffer of data to our servers every time a motion is successfully detected. This is to allow us to troubleshoot your specific issues swiftly.

```java
kiwiInstance.setDebugging(true);
```
