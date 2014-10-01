package com.kiwiwearables.app.models;

import java.lang.reflect.Field;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.annotations.SerializedName;

/**
 * Created by afzal on 2014-09-10.
 */
public class SensorReading implements Parcelable {

    public static final String TAG = SensorReading.class.getSimpleName();

    @SerializedName("device_id")
    public String mDeviceId;
    @SerializedName("device_type")
    public String mDeviceType;
    @SerializedName("spec")
    public String mApiVersion = "v0.1";
    public float mAccX, mAccY, mAccZ;
    public float mGyroX, mGyroY, mGyroZ;
    public float mMagX, mMagY, mMagZ;
    public float mQuatX, mQuatY, mQuatZ, mQuatW;
    public float mRoll, mPitch, mYaw;
    public float mBarometer, mTemperature;
    public float mEcgRaw, mEcgRri, mEcgLrLf;

    /**
     * Parcelable implementation
     */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mDeviceId);
        dest.writeString(mDeviceType);
        dest.writeFloat(mAccX);
        dest.writeFloat(mAccY);
        dest.writeFloat(mAccZ);
        dest.writeFloat(mGyroX);
        dest.writeFloat(mGyroY);
        dest.writeFloat(mGyroZ);
    }

    public SensorReading(Parcel src) {
        mDeviceId = src.readString();
        mDeviceType = src.readString();
        mAccX = src.readFloat();
        mAccY = src.readFloat();
        mAccZ = src.readFloat();
        mGyroX = src.readFloat();
        mGyroY = src.readFloat();
        mGyroZ = src.readFloat();
    }

    public static final Creator<SensorReading> CREATOR = new Creator<SensorReading>() {
        public SensorReading createFromParcel(Parcel pc) {
            return new SensorReading(pc);
        }

        @Override
        public SensorReading[] newArray(int size) {
            return new SensorReading[size];
        }
    };

    /**
     * Values from the Wear device come in floats
     * Accel readings need to be normalized for gravity
     *
     * @param values values coming from the Wear device
     * @param deviceType type of device
     * @param deviceId ID of the device
     */
    public SensorReading(float[] values, String deviceType, String deviceId) throws IllegalArgumentException {
        mDeviceType = deviceType;
        mDeviceId = deviceId;
        if (values.length != 6) {
            throw new IllegalArgumentException("float[] values should contain 6 items");
        }
        mAccX = values[0] / 9.81f;
        mAccY = values[1] / 9.81f;
        mAccZ = values[2] / 9.81f;
        mGyroX = values[3] / 0.0175f;
        mGyroY = values[4] / 0.0175f;
        mGyroZ = values[5] / 0.0175f;
    }

    /**
     * Values from the old Kiwi device come in Strings of shorts
     * They need to be converted to floats
     *
     * @param values values coming from the old Kiwi device
     * @param deviceType type of device
     * @param deviceId ID of the device
     */
    public SensorReading(String[] values, String deviceType, String deviceId) {
        mDeviceType = deviceType;
        mDeviceId = deviceId;
        if (values.length != 6) {
            throw new IllegalArgumentException("float[] values should contain 6 items");
        }
        mAccX = shortStringToAcc(values[0]);
        mAccY = shortStringToAcc(values[1]);
        mAccZ = shortStringToAcc(values[2]);
        mGyroX = getShort(values[3]);
        mGyroY = getShort(values[4]);
        mGyroZ = getShort(values[5]);
    }

    /**
     * Convert the String value to its float representation
     *
     * @param value String containing the short value
     * @return float representation of the accelerometer reading
     */
    private float shortStringToAcc(String value) {
        short shortVal = getShort(value);
        return shortVal * 9.81f / 4096;
    }

    private short getShort(String value) {
        return Short.parseShort(value.replace("\r", "").replace("\n",""));
    }

    /**
     * Naming strategy for Gson conversion
     */
    public static class NamingStrategy implements FieldNamingStrategy {

        @Override
        public String translateName(Field field) {
            String fieldName = FieldNamingPolicy.IDENTITY.translateName(field);
            if (fieldName.startsWith("m")) {
                fieldName = fieldName.toLowerCase()
                        .substring(1)
                        .replace("acc", "a")
                        .replace("gyro", "g")
                        .replace("mag", "m")
                        .replace("quat", "q")
                        .replace("barometer", "bar")
                        .replace("temperature", "temp_k")
                        .replace("ecg", "ecg_")
                        .replace("lrlf", "lr_lf");
            }
            return fieldName;
        }
    }
}
