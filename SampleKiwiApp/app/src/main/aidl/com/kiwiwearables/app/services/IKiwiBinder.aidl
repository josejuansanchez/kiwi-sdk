// IKiwiBinder.aidl
package com.kiwiwearables.app.services;

import com.kiwiwearables.app.models.SensorReading;

// Declare any non-default types here with import statements

interface IKiwiBinder {

    void sendData(in SensorReading reading);
}
