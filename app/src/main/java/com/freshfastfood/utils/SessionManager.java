package com.freshfastfood.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.freshfastfood.model.Address;
import com.freshfastfood.model.User;
import com.google.gson.Gson;

public class SessionManager {
    private final SharedPreferences mPrefs;
    SharedPreferences.Editor mEditor;
    public static String login = "login";
    public static String isopen = "isopen";
    public static String userdata = "Userdata";
    public static String address1 = "address";
    public static boolean iscart =false;
    public static String area = "area";
    public static String currncy = "currncy";
    public static String privacy = "privacy_policy";
    public static String tremcodition = "tremcodition";
    public static String aboutUs = "about_us";
    public static String contactUs = "contact_us";
    public static String oMin = "o_min";
    public static String razKey = "raz_key";
    public static String tax = "tax";
    public static String CURRUNCY = "currncy";
    public static String COUPON = "coupon";
    public static String COUPONID = "couponid";
    public static String WALLET = "wallet";
    public SessionManager(Context context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        mEditor = mPrefs.edit();
    }

    public void setStringData(String key, String val) {
        mEditor.putString(key, val);
        mEditor.commit();
    }

    public String getStringData(String key) {
        return mPrefs.getString(key, "");
    }
    public void setFloatData(String key, float val) {
        mEditor.putFloat(key, val);
        mEditor.commit();
    }

    public float getFloatData(String key) {
        return mPrefs.getFloat(key, 0);
    }

    public void setBooleanData(String key, Boolean val) {
        mEditor.putBoolean(key, val);
        mEditor.commit();
    }

    public boolean getBooleanData(String key) {
        return mPrefs.getBoolean(key, false);
    }

    public void setIntData(String key, int val) {
        mEditor.putInt(key, val);
        mEditor.commit();
    }

    public int getIntData(String key) {
        return mPrefs.getInt(key, 0);
    }

    public void setUserDetails(String key, User val) {
        mEditor.putString(userdata, new Gson().toJson(val));
        mEditor.commit();
    }

    public User getUserDetails(String key) {
        return new Gson().fromJson(mPrefs.getString(userdata, ""), User.class);
    }

    public void setAddress(String key, Address val) {
        mEditor.putString(address1, new Gson().toJson(val));
        mEditor.commit();
    }

    public Address getAddress(String key) {
        return new Gson().fromJson(mPrefs.getString(address1, ""), Address.class);
    }
    public void logoutUser() {
        mEditor.clear();
        mEditor.commit();
    }
}
