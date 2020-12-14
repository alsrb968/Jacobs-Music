package com.heartsafety.jacobsmusic.activity.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.heartsafety.jacobsmusic.R;
import com.heartsafety.jacobsmusic.activity.adapter.KeywordAdapter;
import com.heartsafety.jacobsmusic.activity.adapter.MusicRecyclerAdapter;
import com.heartsafety.jacobsmusic.activity.model.MusicInfo;
import com.heartsafety.jacobsmusic.databinding.FragmentListBinding;
import com.heartsafety.jacobsmusic.util.Log;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends BaseFragment {
    private static final String ARG_POSITION = "position";

    private FragmentListBinding mBinding;
    private ArrayList<MusicInfo> mList;

    private MusicRecyclerAdapter mMusicAdapter;
    private KeywordAdapter mKeywordAdapter;
    private int mPosition;

    public ListFragment() {
        // Required empty public constructor
    }

    public static ListFragment newInstance(int position) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("");
        Bundle args = getArguments();
        if (args != null) {
            mPosition = args.getInt(ARG_POSITION);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false);

        Log.i("");
        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("");
        ArrayList<MusicInfo> list = mActivity.getMusicListInfo();
        if (list != null) {
            Log.d("size: " + list.size());
//            for (MusicDto musicDto : mList) {
//                Log.d(musicDto.getTitle());
//            }
            mMusicAdapter = new MusicRecyclerAdapter(getContext(), list);
            mBinding.recyclerView.setAdapter(mMusicAdapter);
            mList = list;

            onPosition(mPosition);
        }

        mBinding.recyclerView.scrollToPosition(mPosition);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("");
    }

    @Override
    public void onBackPressed() {
        Log.i("");
        mActivity.moveTaskToBack(true);
    }

    private final KeywordAdapter.OnKeywordEvent mOnKeywordEvent = position -> {
        ((LinearLayoutManager) mBinding.recyclerView.getLayoutManager()).scrollToPositionWithOffset(position, 0);
    };

    @Override
    public void onPlayState(int state) {

    }

    @Override
    public void onPlayTime(int time) {

    }

    @Override
    public void onPosition(int position) {
        Log.d(String.valueOf(position));
        mMusicAdapter.setSelectedPosition(position);
    }

    @Override
    public void onMusicListInfo(ArrayList<MusicInfo> list) {
        Log.i(list.get(mPosition).toString());
        mList = list;
        mMusicAdapter = new MusicRecyclerAdapter(getContext(), mList);
        mBinding.recyclerView.setAdapter(mMusicAdapter);
        onPosition(mPosition);

        ArrayList<String> keywords = new ArrayList<>();
        for (MusicInfo info : list) {
            keywords.add(info.getTitle());
        }

        mKeywordAdapter = new KeywordAdapter(getContext(), keywords, mOnKeywordEvent);
        mBinding.recyclerViewKeyword.setAdapter(mKeywordAdapter);
    }

    @Override
    public void onMusicCurrentInfo(MusicInfo info) {

    }
}