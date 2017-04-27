package com.bemetoy.bp.test;

import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.test.base.BaseTestCase;
import com.bemetoy.bp.test.testcase.TestCase_PluginStubBus;
import com.bemetoy.bp.test.testcase.TestCase_RxEventBus;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by albieliang on 16/3/15.
 */
public class TestCenter {

    private static final String TAG = "Test.TestCenter";

    private static TestCenter sTestCenter;

    private List<BaseTestCase> mTestCases;

    public static TestCenter getImpl() {
        if (sTestCenter == null) {
            synchronized (TestCenter.class) {
                if (sTestCenter == null) {
                    sTestCenter = new TestCenter();
                }
            }
        }
        return sTestCenter;
    }

    private TestCenter() {
        mTestCases = new LinkedList<BaseTestCase>();
    }

    public void initialize() {
        mTestCases.add(new TestCase_RxEventBus());
        mTestCases.add(new TestCase_PluginStubBus());
        // Add more test case here

    }

    public void start() {
        Log.i(TAG, "start test center and begin test all test case.");
        doPrepare();
        doTest();
        doEnd();
    }

    public List<BaseTestCase> getTestCases() {
        return mTestCases;
    }

    private void doPrepare() {
        for (BaseTestCase tc : mTestCases) {
            tc.prepare();
        }
    }

    private void doTest() {
        for (BaseTestCase tc : mTestCases) {
            tc.doTest();
        }
    }

    private void doEnd() {
        for (BaseTestCase tc : mTestCases) {
            tc.doEnd();
        }
    }
}
