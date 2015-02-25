package com.kiwiwearables.kiwilibsample;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import android.util.Log;

import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.kiwiwearables.kiwilib.Kiwi;

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
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        String messageEventPath = messageEvent.getPath();

        if (messageEventPath.equals("SENSOR_DATA_MESSAGE")) {
            // send data to kiwi lib
            Log.d(TAG, "received message from wear");

            ByteBuffer byteBuffer = ByteBuffer.wrap(messageEvent.getData());

            FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
            floatBuffer.rewind();
            float[] values = new float[floatBuffer.limit()];
            floatBuffer.get(values);

            Kiwi kiwi = Kiwi.with(this);
            kiwi.sendData(values);

        }

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {

    }
}
