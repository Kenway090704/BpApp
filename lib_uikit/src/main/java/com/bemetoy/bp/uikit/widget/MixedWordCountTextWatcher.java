package com.bemetoy.bp.uikit.widget;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.Util;

/**
 * Created by Tom on 2016/7/4.
 */
public class MixedWordCountTextWatcher implements TextWatcher {

    private static final String TAG = "UiKit.MixedWordCountTextWatcher";

    private EditText contentET;
    private TextView wordcountTV;
    private final int limit;
    private InextBtnEnabled iSetNextBtnEnabled=null;

    public void setInextBtnEnabled(InextBtnEnabled listener){
        iSetNextBtnEnabled = listener;
    }

    public MixedWordCountTextWatcher(final EditText et, final TextView wc, final int limit) {
        this.contentET = et;
        this.wordcountTV = wc;
        this.limit = limit;
    }

    private boolean isError = false;
    @Override
    public void afterTextChanged(Editable s) {
        int count = 0;
        String str = s.toString();
        String newStr = "";
        for (int i = 0; i < str.length(); i++) {
            if (Util.isChinese(str.charAt(i))) {
                count += 2;
            } else {
                count += 1;
            }
            if (count <= limit) {
                newStr += str.charAt(i);
            } else {
                break;
            }
        }
        if (count > limit) {
            try {
                contentET.setText(newStr);
                // 走鋼絲的操作, 修改務必注意 :
                // setText 後 , afterTextChanged再進入一次, 但不再 setText .
                // 不引入標誌位增加複雜度.
                if (!isError) {
                    int ilen = contentET.getText().toString().length();
                    contentET.setSelection(ilen);
                } else {
                    contentET.setSelection(0);
                }
                isError = false;
            } catch (Exception e) {
                isError = true;
                String errmsg = e == null ? "" : e.getMessage();
                Log.e(TAG, "error " + errmsg);
                contentET.setText(newStr);
                contentET.setSelection(0);
            }
        }
        int left = limit - count;
        if (left < 0) {
            left = 0;
        }
        if (wordcountTV != null) {
            wordcountTV.setText("" + (left / 2));
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(iSetNextBtnEnabled!=null)
            iSetNextBtnEnabled.setNextBtnEnable();
    }

    public static  interface InextBtnEnabled{
        void setNextBtnEnable();
    }
}

