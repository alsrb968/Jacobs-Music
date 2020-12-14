package com.heartsafety.jacobsmusic.activity.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.heartsafety.jacobsmusic.R;
import com.heartsafety.jacobsmusic.databinding.RowMusicKeywordBinding;
import com.heartsafety.jacobsmusic.util.MusicUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class KeywordAdapter extends RecyclerView.Adapter<KeywordAdapter.ViewHolder> {
    private RowMusicKeywordBinding mBinding;
    private Context mContext;
    private LayoutInflater mInflater;

    private static LinkedHashMap<String, Integer> mMapIndex = new LinkedHashMap<>();
    private static String[] mKeywords;

    private OnKeywordEvent mOnKeywordEvent;

    public interface OnKeywordEvent {
        void onPosition(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final RowMusicKeywordBinding binding;
        public ViewHolder(final RowMusicKeywordBinding itemBinging) {
            super(itemBinging.getRoot());
            this.binding = itemBinging;
        }
    }

    public KeywordAdapter(Context context, ArrayList<String> keywords, OnKeywordEvent onKeywordEvent) {
        mContext = context;
        mKeywords = setKeywordList(keywords);
        mOnKeywordEvent = onKeywordEvent;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(parent.getContext());
        }
        mBinding = DataBindingUtil.inflate(mInflater, R.layout.row_music_keyword, parent, false);

        return new ViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setKeyword(mKeywords[position]);
        holder.binding.tvKeyWord.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mOnKeywordEvent != null) {
                        mOnKeywordEvent.onPosition(getPositionForSection(holder.getAdapterPosition()));
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        if (mKeywords == null)
            return 0;
        return mKeywords.length;
    }

    public String[] setKeywordList(ArrayList<String> keywordList) {
        mMapIndex.clear();

        for (int i = 0; i < keywordList.size(); i++) {
            String item = keywordList.get(i);
            String index = item.substring(0, 1);

            char c = index.charAt(0);
            if (MusicUtils.OrderByComparator.isKorean(c)) {
                index = String.valueOf(MusicUtils.KoreanChar.getCompatChoseong(c));
            }

            if (mMapIndex.get(index) == null)
                mMapIndex.put(index, i);
        }

        ArrayList<String> indexList = new ArrayList<>(mMapIndex.keySet());
//        mKeywords = new ArrayList<>();/*new String[indexList.size()];*/
        String[] keywords = new String[indexList.size()];
        //skhero.kang 2020-02-04 sections 에
        indexList.toArray(keywords);

        indexList.clear();
        indexList.trimToSize();

        return keywords;
    }

    public int getPositionForSection(int section) {
        String letter = mKeywords[section];
        return mMapIndex.get(letter);
    }
}
