package com.nhatton.ggtalkvn.ui.list;


import android.content.Context;
import android.content.DialogInterface;

import android.database.Cursor;

import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nhatton.ggtalkvn.R;
import com.nhatton.ggtalkvn.data.SoundDbService;


public class SoundAdapter extends BaseAdapter {

    private SoundDbService mDbHelper;

    private LayoutInflater mInflater;

    private Cursor mCursor;

    private Context ctx;

    SoundAdapter(Context context, Cursor cursor) {
        mCursor = cursor;
        ctx = context;
        mDbHelper = new SoundDbService(ctx);

        mInflater = LayoutInflater.from(ctx);
    }

    public int getCount() {
        return mCursor.getCount();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getLong(mCursor.getColumnIndexOrThrow(SoundDbService.KEY_ROW_ID));
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        SoundAdapter.ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_row, parent, false);

            holder = new SoundAdapter.ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.description);
            holder.buttonDelete = (Button) convertView.findViewById(R.id.button_delete);

            convertView.setTag(holder);
        } else {
            holder = (SoundAdapter.ViewHolder) convertView.getTag();
        }
        mCursor.moveToPosition(position);

        holder.buttonDelete.setTag(position);

        holder.text.setText(mCursor.getString(
                mCursor.getColumnIndexOrThrow(SoundDbService.KEY_DESCRIPTION)));


        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final int position = (int) view.getTag();

                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

                builder.setMessage(R.string.alert_delete)
                        .setTitle(R.string.alert_delete_title);

                builder.setPositiveButton(R.string.alert_delete_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mDbHelper.open();
                        Cursor c = mDbHelper.fetchAllSounds();

                        c.moveToPosition(position);

                        if (mDbHelper.deleteSound(c.getLong
                                (c.getColumnIndex(SoundDbService.KEY_ROW_ID)))) {
                            Toast.makeText(ctx, R.string.toast_deleted, Toast.LENGTH_SHORT).show();
                            mCursor = mDbHelper.fetchAllSounds();
                            notifyDataSetChanged();
                        }
                        c.close();
                        mDbHelper.close();
                    }
                });

                builder.setNegativeButton(R.string.alert_delete_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                builder.create().show();
            }
        });

        return convertView;
    }

    public void remove(int position) {
        mDbHelper.open();
        Cursor c = mDbHelper.fetchAllSounds();

        c.moveToPosition(position);

        if (mDbHelper.deleteSound(c.getLong
                (c.getColumnIndex(SoundDbService.KEY_ROW_ID)))) {
            Toast.makeText(ctx, R.string.toast_deleted, Toast.LENGTH_SHORT).show();
            mCursor = mDbHelper.fetchAllSounds();
            notifyDataSetChanged();
        }
        c.close();
        mDbHelper.close();
    }

    private class ViewHolder {
        TextView text;
        Button buttonDelete;
    }
}