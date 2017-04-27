package com.bemetoy.bp.auth;

import android.support.test.runner.AndroidJUnit4;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.ui.AccountRegisterFragment;
import com.bemetoy.stub.network.NetSceneResponseCallback;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;

/**
 * Created by tomliu on 16-10-12.
 */

@RunWith(AndroidJUnit4.class)
public class AuthTest {

    private CountDownLatch latch = null;

    @Before
    public void setUp() {
        latch = new CountDownLatch(1);
    }

    @Test
    public void testRegister() throws InterruptedException {
        String name = "tom444";
        AccountRegisterFragment.CheckAccountNetScene netScene = new AccountRegisterFragment.
                CheckAccountNetScene(name, new NetSceneResponseCallback<Racecar.CheckAccountResponse>() {
                    @Override
                    public void onResponse(IRequest request, Racecar.CheckAccountResponse result) {
                        Assert.assertNotEquals(0, result.getPrimaryResp().getResult());
                        latch.countDown();
                    }
                });
        netScene.doScene();
        latch.await();
    }
}
