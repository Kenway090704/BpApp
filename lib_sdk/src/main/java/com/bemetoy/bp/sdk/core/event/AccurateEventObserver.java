package com.bemetoy.bp.sdk.core.event;

import com.bemetoy.bp.sdk.tools.Log;

/**
 * Created by albieliang on 16/3/31.
 */
public abstract class AccurateEventObserver<EventType extends RxEvent> implements EventObserver {

    private static final String TAG = "Event.AccurateEventObserver";

    @Override
    public final void onReceive(RxEvent rxEvent) {
        EventType event = null;
        try {
            event = (EventType) rxEvent;
        } catch (Exception e) {
            Log.v(TAG, "cast RxEvent failed. %s", e);
            return;
        }
        if (event != null) {
            onReceiveEvent(event);
        }
    }

    /**
     *
     * @param event
     */
    public abstract void onReceiveEvent(EventType event);
}
