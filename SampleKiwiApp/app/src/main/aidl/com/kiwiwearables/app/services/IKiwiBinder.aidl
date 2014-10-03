// IKiwiBinder.aidl
package com.kiwiwearables.app.services;

// Declare any non-default types here with import statements
import com.kiwiwearables.app.models.SensorReading;

interface IKiwiBinder {
    void sendData(in SensorReading reading);
}
