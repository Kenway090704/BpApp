package com.bemetoy.bp.test.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bemetoy.bp.R;
import com.bemetoy.bp.databinding.UiTestCenterBinding;
import com.bemetoy.bp.test.TestCenter;
import com.bemetoy.bp.test.base.BaseTestCase;
import com.bemetoy.bp.uikit.BaseDataBindingActivity;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by albieliang on 16/4/8.
 */
public class TestCenterUI extends BaseDataBindingActivity<UiTestCenterBinding> {

    private DefaultAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.ui_test_center;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("TestCenter");
        mAdapter = new DefaultAdapter(this);
        mAdapter.setData(getDataItems());

        mBinding.cellGv.setAdapter(mAdapter);
        mBinding.cellGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DataItem item = (DataItem) parent.getAdapter().getItem(position);
                item.testCase.prepare();
                item.testCase.doTest();
                item.testCase.doEnd();
            }
        });
    }

    protected List<DataItem> getDataItems() {
        List<BaseTestCase> list = TestCenter.getImpl().getTestCases();
        List<DataItem> items = new LinkedList<>();
        for (BaseTestCase testCase : list) {
            DataItem item = new DataItem();
            item.testCase = testCase;
            item.title = testCase.getClass().getSimpleName();
            items.add(item);
        }
        return items;
    }

    private static class DataItem {
        String title;
        BaseTestCase testCase;
    }

    private static class DefaultAdapter extends BaseAdapter {

        private List<DataItem> mDataItems;
        private LayoutInflater mInflater;

        public DefaultAdapter(Context context) {
            mDataItems = new LinkedList<>();
            mInflater = LayoutInflater.from(context);
        }

        public void setData(List<DataItem> dataItems) {
            mDataItems.clear();
            if (dataItems == null || dataItems.isEmpty()) {
                return;
            }
            mDataItems.addAll(dataItems);
        }

        public void appendData(List<DataItem> dataItems) {
            if (dataItems == null || dataItems.isEmpty()) {
                return;
            }
            mDataItems.addAll(dataItems);
        }

        @Override
        public int getCount() {
            return mDataItems.size();
        }

        @Override
        public DataItem getItem(int position) {
            return mDataItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            DataItem item = getItem(position);
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.test_center_test_case_item, parent, false);
                TextView tv = (TextView) convertView.findViewById(R.id.title_tv);

                holder = new ViewHolder();
                holder.titleTv = tv;

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.titleTv.setText(item.title);
            return convertView;
        }

        private static class ViewHolder {
            TextView titleTv;
        }
    }
}
