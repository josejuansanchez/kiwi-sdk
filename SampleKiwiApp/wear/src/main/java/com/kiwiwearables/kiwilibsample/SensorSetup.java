package com.kiwiwearables.kiwilibsample;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;

import com.kiwiwearables.kiwilibsample.SensorListener.DataListener;


/**
 * Created by afzal on 15-02-19.
 */
public class SensorSetup {

    private static final String TAG = SensorSetup.class.getSimpleName();

    private final SensorManager mSensorManager;
    private final SensorListener mSensorListener;
    private Sensor mAccelSensor;
    private Sensor mGyroSensor;
    private Sensor mMagSensor;

    public SensorSetup(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensorListener = new SensorListener();
    }

    public void initSensors() {
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            mAccelSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            Log.d(TAG, "initialized Accel sensor");
        }

        if (mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
            mGyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            Log.d(TAG, "initialized Gyro sensor");
        }

        if (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            mMagSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            Log.d(TAG, "initialized Mag sensor");
        }
    }

    public void unregisterSensors() {
        mSensorManager.unregisterListener(mSensorListener);
    }

    public void registerSensors() {
        mSensorManager.registerListener(mSensorListener, mAccelSensor, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(mSensorListener, mGyroSensor, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(mSensorListener, mMagSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void setDataListener(DataListener listener) {
        mSensorListener.setDataListener(listener);
    }
}
