package com.bemetoy.stub.ui;

import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.bemetoy.bp.autogen.events.AccountUpdateEvent;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.sdk.core.event.AccurateEventObserver;
import com.bemetoy.bp.sdk.core.event.EventObserver;
import com.bemetoy.bp.sdk.core.event.RxEventBus;
import com.bemetoy.bp.uikit.BaseDataBindingActivity;
import com.bemetoy.stub.account.AccountLogic;

/**
 * Created by Tom on 2016/7/4.
 */
public abstract class AccountChangeActivity<T extends ViewDataBinding> extends BaseDataBindingActivity<T> {

    private EventObserver eventObserver = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventObserver = new AccurateEventObserver<AccountUpdateEvent>() {
            @Override
            public void onReceiveEvent(AccountUpdateEvent event) {
                onAccountUpdate(AccountLogic.getAccountInfo());
            }
        };
        RxEventBus.getBus().register(AccountUpdateEvent.ID, eventObserver);
    }

    @Override
    protected void onDestroy() {
        RxEventBus.getBus().unregister(AccountUpdateEvent.ID, eventObserver);
        eventObserver = null;
        super.onDestroy();
    }

    public abstract void onAccountUpdate(Racecar.AccountInfo accountInfo);
}
