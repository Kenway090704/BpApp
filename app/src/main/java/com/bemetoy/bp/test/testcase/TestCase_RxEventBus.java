package com.bemetoy.bp.test.testcase;

import com.bemetoy.bp.autogen.events.SampleEvent;
import com.bemetoy.bp.sdk.core.ThreadPool;
import com.bemetoy.bp.sdk.core.event.AccurateEventObserver;
import com.bemetoy.bp.sdk.core.event.EventObserver;
import com.bemetoy.bp.sdk.core.event.RxEvent;
import com.bemetoy.bp.sdk.core.event.RxEventBus;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.test.base.BaseTestCase;

/**
 * Created by albieliang on 16/3/15.
 */
public class TestCase_RxEventBus implements BaseTestCase {

    public static final String TAG = "Test.TestCase_RxEventBus";

    private EventObserver observer1;
    private EventObserver observer2;
    private EventObserver observer3;
    private EventObserver observer4;


    @Override
    public void prepare() {

        observer1 = new EventObserver() {
            @Override
            public void onReceive(RxEvent event) {
                Log.i(TAG, "on receive event(1, %s)", event);
            }
        };

        RxEventBus.getBus().register("TestEvent", observer1);

        observer2 = new EventObserver() {
            @Override
            public void onReceive(RxEvent event) {
                Log.i(TAG, "on receive event(2, %s)", event);
            }
        };

        RxEventBus.getBus().register("MagicalEvent", observer2);

        observer3 = new EventObserver() {
            @Override
            public void onReceive(RxEvent event) {
                Log.i(TAG, "on receive event(3, %s)", event);
            }
        };

        observer4 = new AccurateEventObserver<SampleEvent>() {
            @Override
            public void onReceiveEvent(SampleEvent event) {
                Log.i(TAG, "on receive event(%s). register by accurate event observer.", event);
            }
        };
        RxEventBus.getBus().register(SampleEvent.ID, observer3);

        RxEventBus.getBus().register(SampleEvent.ID, observer4);
    }

    @Override
    public void doTest() {
        RxEvent event = new RxEvent("TestEvent") {
            @Override
            public String toString() {
                return "toString, I am a test event";
            }
        };

        RxEventBus.getBus().publish(event);

        event = new RxEvent("MagicalEvent") {
            @Override
            public String toString() {
                return "toString, I am a magical event";
            }
        };

        RxEventBus.getBus().publish(event);

        RxEventBus.getBus().publish(new SampleEvent(new RxEvent.Callback() {
            @Override
            public void onCallback(RxEvent event) {
                Log.i(TAG, "I am a sample event.");
            }
        }));
    }

    @Override
    public void doEnd() {
        ThreadPool.postDelay(new Runnable() {
            @Override
            public void run() {
                RxEventBus.getBus().unregister("TestEvent", observer1);
                RxEventBus.getBus().unregister("MagicalEvent", observer2);
                RxEventBus.getBus().unregister(SampleEvent.ID, observer3);
                RxEventBus.getBus().unregister(SampleEvent.ID, observer4);
            }
        }, 1000);
    }
}
