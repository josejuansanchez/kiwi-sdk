package com.kiwiwearables.kiwilibsample;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by afzal on 15-02-19.
 */
public class WearListenerService extends WearableListenerService {

    private static final String TAG = WearListenerService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        String messageEventPath = messageEvent.getPath();
        Intent intent = new Intent(this, SensorService.class);

        if (messageEventPath.equals("START_SENSOR_REQUEST")) {
            startService(intent);
        } else if (messageEventPath.equals("STOP_SENSOR_REQUEST")) {
            stopService(intent);
        }
    }

    @Override
    public void onPeerDisconnected(Node peer) {
        Intent intent = new Intent(this, SensorService.class);
        stopService(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {

    }
}
