package com.heartsafety.jacobsmusic.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.heartsafety.jacobsmusic.R;
import com.heartsafety.jacobsmusic.activity.model.MusicDto;
import com.heartsafety.jacobsmusic.databinding.RowMusicListBinding;
import com.heartsafety.jacobsmusic.util.Log;
import com.heartsafety.jacobsmusic.util.MusicUtils;

import java.util.ArrayList;

public class MusicRecyclerAdapter extends RecyclerView.Adapter<MusicRecyclerAdapter.MusicViewHolder> {
    private RowMusicListBinding mBinding;
    private Context mContext;
    private LayoutInflater mInflater;

    private ArrayList<MusicDto> mDataSet;

    public static class MusicViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public RowMusicListBinding binding;
        public MusicViewHolder(RowMusicListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MusicRecyclerAdapter(Context context, ArrayList<MusicDto> dataSet) {
        mContext = context;
        mDataSet = dataSet;
    }

    @NonNull
    @Override
    public MusicRecyclerAdapter.MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(parent.getContext());
        }
        mBinding = DataBindingUtil.inflate(mInflater, R.layout.row_music_list, parent, false);

        return new MusicViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
//        Log.d("position: " + position);
        holder.binding.title.setText(mDataSet.get(position).getTitle());
        holder.binding.artist.setText(mDataSet.get(position).getArtist());
        Glide.with(mContext)
                .load(MusicUtils.getAlbumImage(mContext, mDataSet.get(position).getAlbumId()))
                .placeholder(R.drawable.me_bg_default_focus)
                .into(holder.binding.album);

        holder.binding.getRoot().setOnClickListener(v -> {
            ((MusicActivity) mContext).switchFragment(MusicUtils.Page.PLAYER, holder.getAdapterPosition());
        });

    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
