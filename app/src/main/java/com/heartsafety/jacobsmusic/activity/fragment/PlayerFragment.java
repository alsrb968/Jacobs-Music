package com.heartsafety.jacobsmusic.activity.fragment;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.heartsafety.jacobsmusic.R;
import com.heartsafety.jacobsmusic.activity.model.MusicDto;
import com.heartsafety.jacobsmusic.databinding.FragmentPlayerBinding;
import com.heartsafety.jacobsmusic.util.Log;
import com.heartsafety.jacobsmusic.util.MusicUtils;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerFragment extends BaseFragment {
    private static final String ARG_POSITION = "position";

    private FragmentPlayerBinding mBinding;

    private int mPosition;

    public PlayerFragment() {
        // Required empty public constructor
    }

    public static PlayerFragment newInstance(int position) {
        PlayerFragment fragment = new PlayerFragment();
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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_player, container, false);
        mBinding.setOnClickHandler(new OnClickHandler());
        mBinding.setOnSeekBarHandler(new OnSeekBarHandler());

        int position = mActivity.getPosition();
        if (position == mPosition) {
            onMusicCurrentInfo(mActivity.getMusicCurrentInfo());
            onTotalTime(mActivity.getTotalTime());
        } else {
            start(mPosition);
        }
        Log.i("");
        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("");
    }

    @Override
    public void onBackPressed() {
        Log.i("");
        mActivity.switchFragment(MusicUtils.Page.LIST, mPosition);
    }

    public class OnClickHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v.equals(mBinding.play)) {
                play();
            } else if (v.equals(mBinding.pause)) {
                pause();
            } else if (v.equals(mBinding.pre)) {
                mBinding.setPlayTime(0);
                prev();
            } else if (v.equals(mBinding.next)) {
                mBinding.setPlayTime(0);
                next();
            } else if (v.equals(mBinding.backBtn)) {
                onBackPressed();
            }
        }
    }

    public class OnSeekBarHandler implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (seekBar.equals(mBinding.seekbar)) {
                mBinding.playTime.setText(MusicUtils.getTimeString(progress));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            if (seekBar.equals(mBinding.seekbar)) {
                pause();
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (seekBar.equals(mBinding.seekbar)) {
                seekTo(seekBar.getProgress());
                play();
            }
        }
    }

    @Override
    public void onPlayState(int state) {
        mBinding.setPlayState(state);
    }

    @Override
    public void onPlayTime(int time) {
        mBinding.setPlayTime(time);
    }

    @Override
    public void onTotalTime(int time) {
        mBinding.setTotalTime(time);
    }

    @Override
    public void onPosition(int position) {

    }

    @Override
    public void onMusicListInfo(ArrayList<MusicDto> list) {

    }

    @Override
    public void onMusicCurrentInfo(MusicDto info) {
        Log.i("");
        mBinding.setMusicDto(info);

        new Handler().postDelayed(() -> mBinding.title.setSelected(true), 1000);

        Bitmap bitmap = MusicUtils.getAlbumImage(mActivity, info.getAlbumId());
        Glide.with(this)
                .load(bitmap)
                .placeholder(R.drawable.me_bg_default_nor)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                .into(mBinding.album);
        Glide.with(this)
                .load(bitmap)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 2)))
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        resource.setAlpha(80);
                        mBinding.bgImage.setBackground(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        mBinding.bgImage.setBackground(mActivity.getDrawable(R.drawable.co_bg));
                    }
                });
    }
}