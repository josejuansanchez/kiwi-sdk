package kiwihackathon.com.samplekiwiapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by afzal on 2014-09-29.
 */
public class ActionReceiver extends BroadcastReceiver {
    private static final String TAG = ActionReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String motionName = intent.getStringExtra("motionName");

        Log.d(TAG, "motion name: " + motionName);
    }
}
