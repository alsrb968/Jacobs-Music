package com.heartsafety.jacobsmusic;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.heartsafety.jacobsmusic.databinding.ActivityMusicBinding;
import com.heartsafety.util.MusicUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class MusicActivity extends AppCompatActivity {

    private ActivityMusicBinding mBinding;
    private ArrayList<MusicDto> list;
    private MediaPlayer mediaPlayer;

    boolean isPlaying = true;
    private ProgressUpdate progressUpdate;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_music);
        Intent intent = getIntent();
        mediaPlayer = new MediaPlayer();

        position = intent.getIntExtra("position",0);
        list = (ArrayList<MusicDto>) intent.getSerializableExtra("playlist");

        mBinding.setOnClickHandler(new OnClickHandler());

        playMusic(list.get(position));
        progressUpdate = new ProgressUpdate();
        progressUpdate.start();

        mBinding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mediaPlayer.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
                if(seekBar.getProgress()>0 && mBinding.play.getVisibility()==View.GONE){
                    mediaPlayer.start();
                }
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(position+1<list.size()) {
                    position++;
                    playMusic(list.get(position));
                }
            }
        });
    }

    public void playMusic(MusicDto musicDto) {

        try {
            mBinding.seekbar.setProgress(0);
            mBinding.title.setText(musicDto.getTitle());
            mBinding.artist.setText(musicDto.getArtist());
            Uri musicURI = Uri.withAppendedPath(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, ""+musicDto.getId());
            mediaPlayer.reset();
            mediaPlayer.setDataSource(this, musicURI);
            mediaPlayer.prepare();
            mediaPlayer.start();
            mBinding.seekbar.setMax(mediaPlayer.getDuration());
            if(mediaPlayer.isPlaying()){
                mBinding.play.setVisibility(View.GONE);
                mBinding.pause.setVisibility(View.VISIBLE);
            }else{
                mBinding.play.setVisibility(View.VISIBLE);
                mBinding.pause.setVisibility(View.GONE);
            }

            Bitmap bitmap = MusicUtils.getAlbumImage(this, musicDto.getAlbumId());
            Glide.with(this)
                    .load(bitmap)
                    .placeholder(R.drawable.me_bg_play_default)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                    .into(mBinding.album);
            Glide.with(this)
                    .load(bitmap)
                    .placeholder(R.drawable.me_bg_play_default)
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3)))
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            resource.setAlpha(60);
                            mBinding.bgImage.setBackground(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            mBinding.bgImage.setBackground(getDrawable(R.drawable.me_bg_play_default));
                        }
                    });

            new Handler().postDelayed(() -> mBinding.title.setSelected(true), 1000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class OnClickHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.play:
                    mBinding.pause.setVisibility(View.VISIBLE);
                    mBinding.play.setVisibility(View.GONE);
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition());
                    mediaPlayer.start();

                    break;
                case R.id.pause:
                    mBinding.pause.setVisibility(View.GONE);
                    mBinding.play.setVisibility(View.VISIBLE);
                    mediaPlayer.pause();
                    break;
                case R.id.pre:
                    if(position-1>=0 ){
                        position--;
                        playMusic(list.get(position));
                        mBinding.seekbar.setProgress(0);
                    }
                    break;
                case R.id.next:
                    if(position+1<list.size()){
                        position++;
                        playMusic(list.get(position));
                        mBinding.seekbar.setProgress(0);
                    }
                    break;
                case R.id.back_btn:
                    onBackPressed();
                    break;
            }
        }
    }



    class ProgressUpdate extends Thread{
        @Override
        public void run() {
            while(isPlaying){
                try {
                    Thread.sleep(500);
                    if(mediaPlayer!=null){
                        mBinding.seekbar.setProgress(mediaPlayer.getCurrentPosition());
                    }
                } catch (Exception e) {
                    Log.e("ProgressUpdate",e.getMessage());
                }

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isPlaying = false;
        if(mediaPlayer!=null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}