package com.nhatton.ggtalkvn.ui.main;


import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nhatton.ggtalkvn.R;
import com.nhatton.ggtalkvn.data.DaoSession;
import com.nhatton.ggtalkvn.data.Sound;
import com.nhatton.ggtalkvn.data.SoundDao;

import java.util.List;


public class SoundAdapter extends RecyclerView.Adapter<SoundAdapter.SoundHolder> {

    private List<Sound> mData;

    private SoundCallback mCallback;

    private DaoSession daoSession;

    public SoundAdapter(DaoSession daoSession, SoundCallback callback) {
        this.daoSession = daoSession;
        mCallback = callback;
    }

    @Override
    public SoundHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sound, parent, false);
        return new SoundHolder(view);
    }

    @Override
    public void onBindViewHolder(SoundHolder holder, int position) {

        final Sound sound = mData.get(position);
        final String text = sound.getText();
        holder.text.setText(text);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onSoundSelected(sound);
            }
        });

        holder.fullscreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onFullScreen(sound);
            }
        });

        holder.exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onExport(sound);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void remove(int position) {
        daoSession.getSoundDao().deleteByKey(mData.get(position).getId());
        mData.remove(position);
        notifyItemRemoved(position);
    }

    public void insert(Sound sound) {
        Sound exist = daoSession.getSoundDao().queryBuilder().where(SoundDao.Properties.Text.eq(sound.getText())).unique();
        if (exist != null) {
            daoSession.getSoundDao().deleteByKey(exist.getId());
            mData.remove(sound);
        }
        daoSession.getSoundDao().insert(sound);
        mData.add(0, sound);
        notifyDataSetChanged();
    }

    public void setList(List<Sound> data) {
        if (mData == null) {
            mData = data;
            notifyItemRangeInserted(0, data.size());
        } else {
            final DiffUtil.DiffResult result = DiffUtil.calculateDiff(new SoundDiffCallback(mData, data));
            mData = data;
            result.dispatchUpdatesTo(this);
        }
    }

    class SoundHolder extends RecyclerView.ViewHolder {
        TextView text;
        ImageButton fullscreenButton;
        ImageButton exportButton;

        SoundHolder(View view) {
            super(view);
            text = view.findViewById(R.id.sound_text);
            fullscreenButton = view.findViewById(R.id.fullscreen_button);
            exportButton = view.findViewById(R.id.export_button);
        }
    }

    class SoundDiffCallback extends DiffUtil.Callback {

        List<Sound> oldList;
        List<Sound> newList;

        SoundDiffCallback(List<Sound> oldList, List<Sound> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return false;
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return false;
        }
    }
}