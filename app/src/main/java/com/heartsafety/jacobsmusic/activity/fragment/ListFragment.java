package com.heartsafety.jacobsmusic.activity.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.heartsafety.jacobsmusic.R;
import com.heartsafety.jacobsmusic.activity.MusicRecyclerAdapter;
import com.heartsafety.jacobsmusic.activity.model.MusicDto;
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
    private ArrayList<MusicDto> mList;

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
        ArrayList<MusicDto> list = mActivity.getMusicListInfo();
        if (list != null) {
            Log.d("size: " + list.size());
//            for (MusicDto musicDto : mList) {
//                Log.d(musicDto.getTitle());
//            }
            MusicRecyclerAdapter adapter = new MusicRecyclerAdapter(getContext(), list);
            mBinding.recyclerView.setAdapter(adapter);
            mList = list;
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
        mActivity.finish();
    }

    @Override
    public void onPlayState(int state) {

    }

    @Override
    public void onPlayTime(int time) {

    }

    @Override
    public void onTotalTime(int time) {

    }

    @Override
    public void onMusicListInfo(ArrayList<MusicDto> list) {
        mList = list;
        MusicRecyclerAdapter adapter = new MusicRecyclerAdapter(getContext(), mList);
        mBinding.recyclerView.setAdapter(adapter);
    }

    @Override
    public void onMusicCurrentInfo(MusicDto info) {

    }
}