package com.bemetoy.bp.sdk.core.event;

/**
 * Created by albieliang on 16/3/15.
 */
public interface EventObserver {
    void onReceive(RxEvent event);
}
