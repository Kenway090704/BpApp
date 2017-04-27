package com.bemetoy.bp.plugin.mall;

import com.bemetoy.bp.plugin.mall.model.MallDataSource;
import com.bemetoy.bp.plugin.mall.model.MallDataSourceRemote;

/**
 * Created by tomliu on 16-9-5.
 */
public class Injection {
    public static MallDataSource provideMallDataSource() {
        return MallDataSourceRemote.getInstance();
    }
}
