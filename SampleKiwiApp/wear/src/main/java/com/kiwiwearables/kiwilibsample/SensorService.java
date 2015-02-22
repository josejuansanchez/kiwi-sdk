package com.kiwiwearables.kiwilibsample;

import java.nio.ByteBuffer;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi.SendMessageResult;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi.GetConnectedNodesResult;
import com.google.android.gms.wearable.Wearable;
import com.kiwiwearables.kiwilibsample.SensorListener.DataListener;

/**
 * Created by afzal on 15-02-20.
 */
public class SensorService extends Service implements ConnectionCallbacks {
    private static final String TAG = SensorService.class.getSimpleName();

    private SensorSetup mSensorSetup;
    private GoogleApiClient mClient;
    private Node mNode;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "SensorService onStartCommand");

        mSensorSetup = new SensorSetup(this);
        mClient = new Builder(this)
                .addConnectionCallbacks(this)
                .addApi(Wearable.API).build();

        // onConnected starts the sensor stuff
        mClient.connect();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if (mClient != null) {
            mClient.disconnect();
            mClient.unregisterConnectionCallbacks(this);
        }

        if (mSensorSetup != null) {
            mSensorSetup.unregisterSensors();
        }
    }

    byte[] bytes = new byte[9 * 4];
    ByteBuffer mByteBuffer = ByteBuffer.wrap(bytes);

    private DataListener mListener = new DataListener() {
        @Override
        public void onDataReceived(float[] points) {
            mByteBuffer.asFloatBuffer().put(points);
            sendMotionMessage(bytes);
        }
    };

    private void sendMotionMessage(byte[] data) {
        if (null == mNode) {
            return;
        }

        Wearable.MessageApi.sendMessage(mClient, mNode.getId(), "SENSOR_DATA_MESSAGE", data).setResultCallback(mMessageResultResultCallback);
    }

    private ResultCallback<SendMessageResult> mMessageResultResultCallback = new ResultCallback<SendMessageResult>() {
        @Override
        public void onResult(SendMessageResult sendMessageResult) {
            Log.i(TAG, "WEAR Result " + sendMessageResult.getStatus());
        }
    };

    private void getConnectedNodes() {
        Wearable.NodeApi.getConnectedNodes(mClient).setResultCallback(new ResultCallback<GetConnectedNodesResult>() {
            @Override
            public void onResult(GetConnectedNodesResult getConnectedNodesResult) {
                if (getConnectedNodesResult.getNodes().size() > 0) {
                    mNode = getConnectedNodesResult.getNodes().get(0);
                }
            }
        });
    }

    @Override
    public void onConnected(Bundle bundle) {
        getConnectedNodes();
        // sendMotionMessage has a node null check so if node is null
        // it doesn't send the data
        mSensorSetup.setDataListener(mListener);
        mSensorSetup.initSensors();
        mSensorSetup.registerSensors();
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (mSensorSetup != null) {
            mSensorSetup.unregisterSensors();
        }
        mNode = null;
        stopSelf();
    }
}
