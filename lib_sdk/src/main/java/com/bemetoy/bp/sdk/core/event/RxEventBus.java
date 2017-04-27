package com.bemetoy.bp.sdk.core.event;

import com.bemetoy.bp.sdk.tools.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by albieliang on 16/3/15.
 */
public class RxEventBus {

    private static final String TAG = "Event.RxEventBus";

    private static volatile RxEventBus sEventBus;

    private Subject<RxEvent, RxEvent> mBus;

    private Map<String, List<ObserverWrapper>> mMaps;

    private RxEventBus() {
        PublishSubject<RxEvent> ps = PublishSubject.create();
        mBus = new SerializedSubject<RxEvent, RxEvent>(ps);
        mMaps = new ConcurrentHashMap<String, List<ObserverWrapper>>();
    }

    public static RxEventBus getBus() {
        if (sEventBus == null) {
            synchronized (RxEventBus.class) {
                if (sEventBus == null) {
                    sEventBus = new RxEventBus();
                }
            }
        }
        return sEventBus;
    }

    /**
     * Publish a RxEvent to the RxEventBus, it will be
     * accurate dispatched to a EventObserver which was register before.
     *
     * @param event
     * @return
     *
     * @see #register(String, EventObserver)
     * @see #unregister(String, EventObserver)
     */
    public boolean publish(RxEvent event) {
        if (event == null) {
            Log.i(TAG, "publish event failed, event is null.");
            return false;
        }
        mBus.onNext(event);
        // callback to event
        if (event.callback != null) {
            event.callback.onCallback(event);
        }
        Log.i(TAG, "publish event(%s) successfully.", event);
        return true;
    }

    /**
     *
     * @return
     */
    protected Observable<RxEvent> getObservableHook() {
        return mBus.observeOn(AndroidSchedulers.mainThread()); //指定事件消费者所在线程。
    }

    /**
     * Register observer
     *
     * @param observer
     * @return
     *
     * @see #unregister(String, EventObserver)
     */
    public boolean register(final String action, final EventObserver observer) {
        if (observer == null) {
            Log.i(TAG, "register event observer failed, observer is null.");
            return false;
        }
        if (action == null || action.length() == 0) {
            Log.i(TAG, "register event observer failed, action is null.");
            return false;
        }
        ObserverWrapper ow = new ObserverWrapper(observer);
        List<ObserverWrapper> list = mMaps.get(action);
        if (list == null) {
            list = new LinkedList<ObserverWrapper>();
            mMaps.put(action, list);
        } else if (list.contains(ow)) {
            Log.i(TAG, "register event observer failed, the observer has been already register.");
            return false;
        }
        ow.subscription = createSubScription(action, observer);
        list.add(ow);
        Log.i(TAG, "register event(%s) observer(%s) successfully.", action, observer);
        return true;
    }

    /**
     * Unregister observer.
     *
     * @param observer
     *
     * @see #register(String, EventObserver)
     */
    public boolean unregister(final String action, final EventObserver observer) {
        if (action == null || action.length() == 0 || observer == null) {
            Log.w(TAG, "unregister event(%s) observer(%s) failed, action or observer is null.", action, observer);
            return false;
        }
        List<ObserverWrapper> list = mMaps.get(action);
        if (list == null) {
            Log.w(TAG, "unregister event observer failed, can not find the observer(%s) list by action(%s).", observer, action);
            return false;
        }
        ObserverWrapper ow = null;
        for (int i = 0; i < list.size(); i++) {
            if (observer.equals(list.get(i).observer)) {
                ow = list.remove(i);
                break;
            }
        }

        if (ow == null) {
            Log.w(TAG, "unregister event observer failed, can not find the observer(%s) in the list by action(%s).", observer, action);
            return false;
        }
        if (list.isEmpty()) {
            mMaps.remove(action);
        }
        Subscription subscription = ow.subscription;
        if (subscription == null) {
            Log.w(TAG, "unregister event(%s) observer(%s) successfully, but subscription is null.", action, observer);
            return true;
        }
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        Log.i(TAG, "unregister event(%s) observer(%s) successfully.", action, observer);
        return true;
    }

    /**
     *
     * @param action
     * @param observer
     * @return
     */
    private Subscription createSubScription(final String action, final EventObserver observer) {
        Subscription subscription = getObservableHook().subscribe(new Action1<RxEvent>() {
            @Override
            public void call(RxEvent event) {
                if (action.equals(event.getAction())) {
                    observer.onReceive(event);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e(TAG, throwable.getMessage());
            }
        });
        return subscription;
    }

    /**
     * Just a wrapper
     *
     */
    private static class ObserverWrapper {

        EventObserver observer;
        Subscription subscription;

        ObserverWrapper(EventObserver observer) {
            this.observer = observer;
        }

        ObserverWrapper(EventObserver observer, Subscription subscription) {
            this.observer = observer;
            this.subscription = subscription;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof ObserverWrapper)) {
                return false;
            }
            ObserverWrapper ow = (ObserverWrapper) o;
            return ow.observer == this.observer || (this.observer != null && this.observer.equals(ow.observer));
        }
    }
}
