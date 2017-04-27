package com.bemetoy.bp.plugin.mall;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.plugin.mall.model.MallDataSource;
import com.bemetoy.bp.protocols.ProtocolConstants;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by tomliu on 16-9-5.
 */
public class MallPresent implements MallContract.Present {

    private MallDataSource dataSource;
    private MallContract.View view;

    private int currentFilter = ProtocolConstants.PART_TYPE.NONE;

    public MallPresent(MallDataSource dataSource, MallContract.View view) {
        Assert.assertNotNull(dataSource);
        Assert.assertNotNull(view);
        this.dataSource = dataSource;
        this.view = view;
    }

    @Override
    public void loadAllPart(boolean showLoading) {
        if(view.isActive() && showLoading) {
           view.showLoading(true);
        }
        this.dataSource.loadAllParts(new MallDataSource.LoadPartsCallback() {
            @Override
            public void onPartsLoaded(List<Racecar.GoodsListResponse.Item> items) {
                if(view.isActive()) {
                    List<Racecar.GoodsListResponse.Item> datas = new ArrayList(items);
                    Iterator<Racecar.GoodsListResponse.Item> iterator = datas.iterator();
                    while(iterator.hasNext()) {
                        Racecar.GoodsListResponse.Item item = iterator.next();
                        switch (currentFilter) {
                            case ProtocolConstants.PART_TYPE.RECOMMEND :
                                if(!item.getRecommend()) {
                                    iterator.remove();
                                }
                                break;
                        }
                    }

                    if(currentFilter == ProtocolConstants.PART_TYPE.RECOMMEND) {
//                        Collections.sort(datas, new Comparator<Racecar.GoodsListResponse.Item>() {
//                            @Override
//                            public int compare(Racecar.GoodsListResponse.Item o1, Racecar.GoodsListResponse.Item o2) {
//                                return o2.getRecommendNum() - o1.getRecommendNum();
//                            }
//                        });
                    }
                    view.loadAddParts(datas);
                    view.showLoading(false);
                }
            }

            @Override
            public void onLoadFailed(int errorCode) {
                if(!view.isActive()) {
                    return;
                }
                view.showLoading(false);
                if(errorCode == ProtocolConstants.ErrorType.NETWORK_SVR_ERROR) {
                    view.showNetworkConnectionError();
                } else if(errorCode == ProtocolConstants.ErrorType.NETWORK_LOCAL_ERROR) {
                    view.showLocalNetworkError();
                }
            }
        });
    }

    @Override
    public void setFilter(int filter) {
        this.currentFilter = filter;
    }

    @Override
    public void start() {
        loadAllPart(true);
    }

    @Override
    public void stop() {
        dataSource.clean();
    }
}
