package com.bemetoy.bp.test.base;

/**
 * Created by albieliang on 16/3/15.
 */
public interface BaseTestCase {

    void prepare();

    void doTest();

    void doEnd();
}
