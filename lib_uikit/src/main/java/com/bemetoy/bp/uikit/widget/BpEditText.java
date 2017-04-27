package com.bemetoy.bp.uikit.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bemetoy.bp.sdk.utils.KeyBoardUtil;
import com.bemetoy.bp.uikit.R;

/**
 * Created by Tom on 2016/5/12.
 */
public class BpEditText extends RelativeLayout {

    private static final String TAG = "UiKit.BpEditText";

    private ImageView mImageView = null;
    private EditText mEditText;
    private boolean mHasFocus;

    public static int INPUT_TYPE_TEXT = 1;
    public static int INPUT_TYPE_PASSWORD = 2;
    public static int INPUT_TYPE_NUMBER = 3;


    public BpEditText(Context context) {
        this(context, null);
    }

    public BpEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BpEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(final Context context, AttributeSet attrs, int defStyleAttr) {
        View view = LayoutInflater.from(context).inflate(R.layout.bp_edit_text, this, true);
        mImageView = (ImageView) view.findViewById(R.id.ed_im);
        mEditText = (EditText) view.findViewById(R.id.actual_ed);
        final View splitView = view.findViewById(R.id.split_view);

        setFocusable(true);
        setClickable(true);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BpTextEdit, defStyleAttr, 0);
        final Drawable focusDrawable = a.getDrawable(R.styleable.BpTextEdit_drawable_focus);
        final Drawable unFocusDrawable  = a.getDrawable(R.styleable.BpTextEdit_drawable_un_focus);
        final Drawable unFocusBgDrawable = a.getDrawable(R.styleable.BpTextEdit_bg_un_focus);
        final Drawable focusBgDrawable = a.getDrawable(R.styleable.BpTextEdit_bg_focus);
        final int splitViewColor = a.getColor(R.styleable.BpTextEdit_split_view_color, Color.GRAY);
        final int splitViewColorUnfocus = a.getColor(R.styleable.BpTextEdit_split_view_color_unfocus, Color.GRAY);
        final String hint = a.getString(R.styleable.BpTextEdit_hint);
        final int maxLength = a.getInt(R.styleable.BpTextEdit_maxLength, 20);

        final int inputType = a.getInt(R.styleable.BpTextEdit_inputType, INPUT_TYPE_TEXT);
        a.recycle();

        mImageView.setImageDrawable(unFocusDrawable);
        setBackgroundDrawable(unFocusBgDrawable);
        splitView.setBackgroundColor(splitViewColorUnfocus);
        mEditText.setHint(hint);
        mEditText.setSingleLine();
        mEditText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});

        if(inputType == INPUT_TYPE_PASSWORD) {
            mEditText.setInputType(EditorInfo.TYPE_CLASS_TEXT);
            mEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        } else if(inputType == INPUT_TYPE_NUMBER) {
            mEditText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        }

        mEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mHasFocus = hasFocus;
                if(hasFocus) {
                    mImageView.setImageDrawable(focusDrawable);
                    setBackgroundDrawable(focusBgDrawable);
                    splitView.setBackgroundColor(splitViewColor);
                } else {
                    mImageView.setImageDrawable(unFocusDrawable);
                    setBackgroundDrawable(unFocusBgDrawable);
                    splitView.setBackgroundColor(splitViewColorUnfocus);
                }
            }
        });

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditText.requestFocus();
                mEditText.setSelection(getText().length());
                KeyBoardUtil.show(context, mEditText);
            }
        });
        if(getChildCount() >= 2) {
            throw new IllegalStateException("only one action view is allowed in the BpEditText");
        }
    }

    /**
     * Get the text in the EditText
     * @return
     */
    public final CharSequence getText() {
        return mEditText.getText().toString();
    }

    public void setText(int strId) {
        mEditText.setText(strId);
    }

    public void setText(String text) {
        mEditText.setText(text);
    }

    public void setTransformationMethod(TransformationMethod method) {
        mEditText.setTransformationMethod(method);
        mEditText.setSelection(getText().length());
    }

    public void addTextChangedListener(TextWatcher watcher) {
        mEditText.addTextChangedListener(watcher);
    }


    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.childrenStates = new SparseArray();
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).saveHierarchyState(ss.childrenStates);
        }
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).restoreHierarchyState(ss.childrenStates);
        }
    }

    @Override
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        dispatchFreezeSelfOnly(container);
    }

    @Override
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        dispatchThawSelfOnly(container);
    }

    static class SavedState extends BaseSavedState {
        SparseArray childrenStates;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in, ClassLoader classLoader) {
            super(in);
            childrenStates = in.readSparseArray(classLoader);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeSparseArray(childrenStates);
        }

        public static final ClassLoaderCreator<SavedState> CREATOR
                = new ClassLoaderCreator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel source, ClassLoader loader) {
                return new SavedState(source, loader);
            }

            @Override
            public SavedState createFromParcel(Parcel source) {
                return createFromParcel(null);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }


}
