package kiwihackathon.com.samplekiwiapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

/**
 * Created by afzal on 2014-10-03.
 */
public class Motion {

    private String mId;
    private String mName;
    private boolean mEnabled;
    private int mThresholdMultiplier;
    private int mTrigger;
    private float mAccWeight;
    private float mGyroWeight;

    public static Motion fromCursor(Cursor cursor) {
        Motion motion = new Motion();
        motion.setId(cursor.getString(cursor.getColumnIndex("_id")));
        motion.setName(cursor.getString(cursor.getColumnIndex("name")));
        motion.setEnabled(cursor.getInt(cursor.getColumnIndex("enabled")) == 1);
        motion.setThresholdMultiplier(cursor.getInt(cursor.getColumnIndex("thresholdMultiplier")));
        motion.setTrigger(cursor.getInt(cursor.getColumnIndex("trigger")));
        motion.setAccWeight(cursor.getFloat(cursor.getColumnIndex("accWeight")));
        motion.setGyroWeight(cursor.getFloat(cursor.getColumnIndex("gyroWeight")));

        return motion;
    }

    public void sendMotion(Context context) {
        Intent intent = new Intent();
        intent.setAction("com.kiwiwearables.MotionChange");
        intent.putExtra("motionId", getId());
        intent.putExtra("enabled", isEnabled());
        intent.putExtra("trigger", getTrigger());
        intent.putExtra("thresholdMultiplier", getThresholdMultiplier());
        intent.putExtra("accWeight", getAccWeight());
        intent.putExtra("gyroWeight", getGyroWeight());
        context.sendBroadcast(intent);
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    public int getThresholdMultiplier() {
        return mThresholdMultiplier;
    }

    public void setThresholdMultiplier(int thresholdMultiplier) {
        mThresholdMultiplier = thresholdMultiplier;
    }

    public int getTrigger() {
        return mTrigger;
    }

    public void setTrigger(int trigger) {
        mTrigger = trigger;
    }

    public float getAccWeight() {
        return mAccWeight;
    }

    public void setAccWeight(float accWeight) {
        mAccWeight = accWeight;
    }

    public float getGyroWeight() {
        return mGyroWeight;
    }

    public void setGyroWeight(float gyroWeight) {
        mGyroWeight = gyroWeight;
    }
}
