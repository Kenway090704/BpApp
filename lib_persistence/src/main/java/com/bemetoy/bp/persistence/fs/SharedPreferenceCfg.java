package com.bemetoy.bp.persistence.fs;

import android.content.Context;
import android.content.SharedPreferences;

import com.bemetoy.bp.persistence.fs.base.KeyValueAccessor;

/**
 * Created by tomliu on 16-9-27.
 */
public class SharedPreferenceCfg implements KeyValueAccessor<String> {


    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;

    public SharedPreferenceCfg(Context context) {
        sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }


    @Override
    public void set(String key, Object value) {

    }

    @Override
    public Object get(String key) {
        return null;
    }

    @Override
    public Object get(String key, Object def) {
        return null;
    }

    @Override
    public void setInt(String key, int val) {
        editor.putInt(key, val);
    }

    @Override
    public int getInt(String key, int def) {
        return sharedPreferences.getInt(key, def);
    }

    @Override
    public void setLong(String key, long val) {

    }

    @Override
    public long getLong(String key, long def) {
        return 0;
    }

    @Override
    public void setFloat(String key, float val) {

    }

    @Override
    public float getFloat(String key, float def) {
        return 0;
    }

    @Override
    public void setDouble(String key, double val) {

    }

    @Override
    public double getDouble(String key, double def) {
        return 0;
    }

    @Override
    public void setString(String key, String val) {
        editor.putString(key, val);
    }

    @Override
    public String getString(String key, String def) {
        return sharedPreferences.getString(key, def);
    }
}
