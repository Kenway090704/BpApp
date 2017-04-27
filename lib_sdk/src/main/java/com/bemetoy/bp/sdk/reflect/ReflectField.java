package com.bemetoy.bp.sdk.reflect;

import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.Util;

import java.lang.reflect.Field;

/**
 * Created by Tom on 2016/4/20.
 */
public class ReflectField<FieldType> {

    private static final String TAG = "SDK.ReflectField";
    private Class<?> mClazz;
    private String mFieldName;

    private boolean mInited;
    private Field mField;
    private Object mObj;

    public ReflectField(Class<?> clazz, Object obj, String fieldName) {

        if (clazz == null || Util.isNullOrNil(fieldName) || obj == null) {
            throw new IllegalArgumentException("none of invoker,fieldName and obj can not be null or nil.");
        }
        this.mClazz = clazz;
        this.mFieldName = fieldName;
        this.mObj = obj;
    }

    private synchronized void prepare() {
        if (mInited) {
            return;
        }
        Class<?> clazz = mClazz;
        while (clazz != null) {
            try {
                Field f = clazz.getDeclaredField(mFieldName);
                f.setAccessible(true);
                mField = f;
                break;
            } catch (Exception e) {
            }
            clazz = clazz.getSuperclass();
        }
        mInited = true;
    }

    public synchronized FieldType get() throws NoSuchFieldException, IllegalAccessException,
            IllegalArgumentException {
        return get(false);
    }


    @SuppressWarnings("unchecked")
    public synchronized FieldType get(boolean ignoreFieldNoExist)
            throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException {
        prepare();
        if (mField == null) {
            if (!ignoreFieldNoExist) {
                throw new NoSuchFieldException();
            }
            Log.w(TAG, "Field %s is no exists.", mFieldName);
            return null;
        }
        FieldType fieldVal = null;
        try {
            fieldVal = (FieldType) mField.get(mObj);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("unable to cast object");
        }
        return fieldVal;
    }

    public synchronized FieldType getWithoutThrow() {
        FieldType fieldVal = null;
        try {
            fieldVal = get(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return fieldVal;
    }

}
