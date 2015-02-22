package com.kiwiwearables.kiwilibsample;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Build;

/**
 * Created by afzal on 15-02-19.
 */
public class SensorListener implements SensorEventListener {

    private static final String TAG = SensorListener.class.getSimpleName();

    private float[] mRawPoints;
    private int mNumPoints;
    private DataListener mDataReceivedListener;

    public interface DataListener {
        public void onDataReceived(float[] points);
    }


    public SensorListener() {
        mRawPoints = new float[9];
        mNumPoints = 0;
    }

    public void setDataListener(DataListener listener) {
        mDataReceivedListener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER
                && mNumPoints == 0) {
            mRawPoints[0] = event.values[0];
            mRawPoints[1] = event.values[1];
            mRawPoints[2] = event.values[2];
            mNumPoints += 3;
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE
                && mNumPoints == 3) {
            if ("Moto 360".equals(Build.MODEL)) {
                mRawPoints[3] = event.values[0] / 4;
                mRawPoints[4] = event.values[1] / 4;
                mRawPoints[5] = event.values[2] / 4;
            } else {
                mRawPoints[3] = event.values[0];
                mRawPoints[4] = event.values[1];
                mRawPoints[5] = event.values[2];
            }
            mNumPoints += 3;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD
                && mNumPoints == 6) {
            mRawPoints[6] = event.values[0];
            mRawPoints[7] = event.values[1];
            mRawPoints[8] = event.values[2];
            mNumPoints += 3;
        }

        // if 9 data points are gathered, send data
        if (mNumPoints == 9 || ("Moto 360".equals(Build.MODEL) && mNumPoints == 6)) {
//            Log.d(TAG, "(" + mRawPoints[1] + ") Sending message to KiwiService at " + Calendar.getInstance().getTimeInMillis());


            mDataReceivedListener.onDataReceived(mRawPoints);
            mNumPoints = 0;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
