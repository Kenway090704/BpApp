package com.bemetoy.stub.app.base;

/**
 * Created by tomliu on 16-9-5.
 */
public interface BaseView {
    boolean isActive();

    void showLoading(boolean showLoading);

    void showNetworkConnectionError();

    void showLocalNetworkError();
}
