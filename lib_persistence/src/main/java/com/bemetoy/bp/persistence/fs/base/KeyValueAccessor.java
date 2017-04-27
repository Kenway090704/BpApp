package com.bemetoy.bp.persistence.fs.base;

/**
 * Created by albieliang on 16/4/2.
 */
public interface KeyValueAccessor<KeyType> {

    void set(KeyType key, Object value);

    Object get(KeyType key);

    Object get(KeyType key, Object def);

    void setInt(KeyType key, int val);

    int getInt(KeyType key, int def);

    void setLong(KeyType key, long val);

    long getLong(KeyType key, long def);

    void setFloat(KeyType key, float val);

    float getFloat(KeyType key, float def);

    void setDouble(KeyType key, double val);

    double getDouble(KeyType key, double def);

    void setString(KeyType key, String val);

    String getString(KeyType key, String def);
}
