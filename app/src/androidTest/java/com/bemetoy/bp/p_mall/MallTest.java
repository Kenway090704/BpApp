package com.bemetoy.bp.p_mall;

import android.support.test.runner.AndroidJUnit4;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.plugin.mall.MallContract;
import com.bemetoy.bp.plugin.mall.MallPresent;
import com.bemetoy.bp.plugin.mall.model.MallDataSourceRemote;
import com.bemetoy.bp.protocols.ProtocolConstants;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by tomliu on 16-10-8.
 */

@RunWith(AndroidJUnit4.class)
public class MallTest {

    MallPresent present = null;
    CountDownLatch latch = new CountDownLatch(1);

    @Before
    public void createLogHistory() {
        present = new MallPresent(MallDataSourceRemote.getInstance(), new MallContract.View() {
            @Override
            public void loadAddParts(List<Racecar.GoodsListResponse.Item> partList) {
                Assert.assertNotNull(partList);
                latch.countDown();
            }

            @Override
            public void showLoading(boolean showLoading) {

            }

            @Override
            public void showNetworkConnectionError() {

            }

            @Override
            public void showLocalNetworkError() {

            }

            @Override
            public boolean isActive() {
                return true;
            }
        });
    }

    @Test
    public void testLoadItem() throws Exception {
        present.loadAllPart(false);
        latch.await();
    }

    @Test
    public void testLoadRecommendItem() throws Exception {
        present.setFilter(ProtocolConstants.PART_TYPE.RECOMMEND);
        present.loadAllPart(false);
        latch.await();
    }


}
