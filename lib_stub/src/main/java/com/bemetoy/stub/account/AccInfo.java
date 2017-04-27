package com.bemetoy.stub.account;

/**
 * Created by albieliang on 16/4/1.
 */
public class AccInfo {

    private long id;
    private byte[] sessionKey;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public byte[] getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(byte[] sessionKey) {
        this.sessionKey = sessionKey;
    }

    public void set(long id, byte[] sessionKey) {
        this.id = id;
        this.sessionKey = sessionKey;
    }

    public void reset() {
        id = 0;
        sessionKey = null;
    }
}
