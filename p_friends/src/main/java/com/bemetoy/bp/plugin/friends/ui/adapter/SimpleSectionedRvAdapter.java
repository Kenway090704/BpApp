package com.bemetoy.bp.plugin.friends.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.ResourceTool;
import com.bemetoy.bp.uikit.widget.recyclerview.BaseRecyclerViewAdapter;
import com.bemetoy.bp.uikit.widget.recyclerview.ExtRecyclerViewAdapter;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by Tom on 2016/4/28.
 */
public class SimpleSectionedRvAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG  = "Friends.SimpleSectionedRvAdapter";

    private final Context mContext;
    public static final int SECTION_TYPE = 0;

    private boolean mValid = true;
    private int mSectionResourceId;
    private int mTextResourceId;
    private LayoutInflater mLayoutInflater;
    private Context context;
    private ExtRecyclerViewAdapter<T> mBaseAdapter;
    private SparseArray<Section> mSections = new SparseArray<Section>();


    public SimpleSectionedRvAdapter(Context context, int sectionResourceId, int textResourceId,
                                    ExtRecyclerViewAdapter<T> baseAdapter) {
        this.context = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSectionResourceId = sectionResourceId;
        mTextResourceId = textResourceId;
        mBaseAdapter = baseAdapter;
        mContext = context;

        mBaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                mValid = mBaseAdapter.getItemCount() > 0;
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                mValid = mBaseAdapter.getItemCount() > 0;
                notifyItemRangeChanged(positionStart, itemCount);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mValid = mBaseAdapter.getItemCount() > 0;
                notifyItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                mValid = mBaseAdapter.getItemCount() > 0;
                notifyItemRangeRemoved(positionStart, itemCount);
            }
        });
    }


    public static class SectionViewHolder extends ExtRecyclerViewAdapter.BaseViewHolder {

        public ImageView imageView;
        public SectionViewHolder(View view, int viewType, int mTextResourceid) {
            super(view, viewType);
            imageView = (ImageView) view.findViewById(mTextResourceid);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int typeView) {
        if (typeView == SECTION_TYPE) {
            final View view = LayoutInflater.from(mContext).inflate(mSectionResourceId, parent, false);
            return new SectionViewHolder(view, typeView, mTextResourceId);
        } else {
            return mBaseAdapter.onCreateViewHolder(parent, typeView - 1);
        }
    }


    public void onBindViewHolder(RecyclerView.ViewHolder sectionViewHolder, int position) {
        if (isSectionHeaderPosition(position)) {
            int resId = ResourceTool.getCharDrawable(context,String.valueOf(mSections.get(position).title));
            if(resId != 0) {
                ((SectionViewHolder) sectionViewHolder).imageView.setImageResource(resId);
            }
        } else {
            mBaseAdapter.onBindViewHolder((BaseRecyclerViewAdapter.BaseViewHolder) sectionViewHolder, sectionedPositionToPosition(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return isSectionHeaderPosition(position)
                ? SECTION_TYPE
                : mBaseAdapter.getItemViewType(sectionedPositionToPosition(position)) + 1;
    }

    /**
     * Get the item in the adapter based on the position.
     * **NOTE**: if the item in that position, the method will return null.
     *
     * @param position
     * @return
     */
    public T getItem(int position) {
        if(isSectionHeaderPosition(position)) {
            Log.d(TAG, "item at the position is section type");
            return null;
        }
        int sectionSizeBeforePosition = 0;
        for(int i = 0; i < mSections.size(); i++) {
            int key = mSections.keyAt(i);
            if (position > mSections.get(key).sectionedPosition) {
                sectionSizeBeforePosition++;
            }
            
            if (position < mSections.get(key).sectionedPosition) {
                break;
            }
        }

        Log.v(TAG, "sectionSizeBeforePosition is %d", sectionSizeBeforePosition);
        final int realPosition = position - sectionSizeBeforePosition;
        Log.v(TAG, "the realPosition is %d", realPosition);
        if(realPosition < 0 || realPosition >= mBaseAdapter.getItemCount()) {
            return null;
        }

        return mBaseAdapter.getItem(realPosition);
    }


    /**
     * Remove the item.
     * @param position
     * @return whether remove successfully.
     */
    public boolean remove(int position) {
        if(isSectionHeaderPosition(position)) {
            Log.d(TAG, "can remove the section.");
            return false;
        }

        int sectionSizeBeforePosition = 0;
        for(int i = 0; i < mSections.size(); i++) {
            int key = mSections.keyAt(i);
            if (position > mSections.get(key).sectionedPosition) {
                sectionSizeBeforePosition++;
            }

            if (position < mSections.get(key).sectionedPosition) {
                break;
            }
        }

        Log.v(TAG, "sectionSizeBeforePosition is %d", sectionSizeBeforePosition);
        final int realPosition = position - sectionSizeBeforePosition;
        Log.v(TAG, "the realPosition is %d", realPosition);
        if(realPosition < 0 || realPosition >= mBaseAdapter.getItemCount()) {
            return false;
        }
        mBaseAdapter.getItems().remove(realPosition);

        /**
         * if the item above this one is section and (the item is the last one or the item below this one is section)
         * that means the section contain the item is empty should be removed.
         */
        if(isSectionHeaderPosition(position - 1) && ( position == getItemCount() - 1 ||isSectionHeaderPosition(position + 1))) {
             mSections.delete(position - 1);
        }
        return true;
    }





    public static class Section {
        int firstPosition;
        int sectionedPosition;
        CharSequence title;

        public Section(int firstPosition, CharSequence title) {
            this.firstPosition = firstPosition;
            this.title = title;
        }

        public CharSequence getTitle() {
            return title;
        }
    }


    public void setSections(Section[] sections) {
        mSections.clear();

        Arrays.sort(sections, new Comparator<Section>() {
            @Override
            public int compare(Section o, Section o1) {
                return (o.firstPosition == o1.firstPosition)
                        ? 0
                        : ((o.firstPosition < o1.firstPosition) ? -1 : 1);
            }
        });

        int offset = 0; // offset positions for the headers we're adding
        for (Section section : sections) {
            section.sectionedPosition = section.firstPosition + offset;
            mSections.append(section.sectionedPosition, section);
            ++offset;
        }

        notifyDataSetChanged();
    }

    public int getSectionSize() {
        return mSections.size();
    }


    public int positionToSectionedPosition(int position) {
        int offset = 0;
        for (int i = 0; i < mSections.size(); i++) {
            if (mSections.valueAt(i).firstPosition > position) {
                break;
            }
            ++offset;
        }
        return position + offset;
    }

    public int sectionedPositionToPosition(int sectionedPosition) {
        if (isSectionHeaderPosition(sectionedPosition)) {
            return RecyclerView.NO_POSITION;
        }

        int offset = 0;
        for (int i = 0; i < mSections.size(); i++) {
            if (mSections.valueAt(i).sectionedPosition > sectionedPosition) {
                break;
            }
            --offset;
        }
        return sectionedPosition + offset;
    }

    public boolean isSectionHeaderPosition(int position) {
        return mSections.get(position) != null;
    }


    @Override
    public long getItemId(int position) {
        return isSectionHeaderPosition(position)
                ? Integer.MAX_VALUE - mSections.indexOfKey(position)
                : mBaseAdapter.getItemId(sectionedPositionToPosition(position));
    }

    @Override
    public int getItemCount() {
        return (mValid ? mBaseAdapter.getItemCount() + mSections.size() : 0);
    }

}
