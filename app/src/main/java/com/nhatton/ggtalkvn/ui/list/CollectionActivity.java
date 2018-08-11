package com.nhatton.ggtalkvn.ui.list;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.ContextMenu;
import android.view.View;
import android.view.MenuItem;
import android.widget.ListView;

import com.nhatton.ggtalkvn.R;
import com.nhatton.ggtalkvn.data.SoundDbService;
import com.nhatton.ggtalkvn.ui.fullscreen.FullscreenActivity;

import static com.nhatton.ggtalkvn.ui.fullscreen.FullscreenActivity.KEY_TEXT;
import static com.nhatton.ggtalkvn.ui.main.MainActivity.tts;

public class CollectionActivity extends ListActivity{

    public SoundDbService mDbHelper;
    public SoundAdapter mAdapter;
    private Cursor mSoundCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        mDbHelper = new SoundDbService(this);
        mDbHelper.open();
        fillData();

        ListView listView = getListView();
        // Create a ListView-specific touch listener. ListViews are given special treatment because
        // by default they handle touches for their list items... i.e. they're in charge of drawing
        // the pressed state (the list selector), handling list item clicks, etc.
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        listView,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    mAdapter.remove(position);
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        });
        listView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        listView.setOnScrollListener(touchListener.makeScrollListener());

        registerForContextMenu(getListView());
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Cursor c = mSoundCursor;
        c.moveToPosition(position);
        String description = c.getString(c.getColumnIndexOrThrow(SoundDbService.KEY_DESCRIPTION));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(description, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            tts.speak(description, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSoundCursor.close();
        mDbHelper.close();
    }

    @Override
    protected void onRestart() {
        mDbHelper = new SoundDbService(this);
        mDbHelper.open();
        fillData();
        super.onRestart();
    }

    private void fillData() {
        mSoundCursor = mDbHelper.fetchAllSounds();
        setListAdapter(mAdapter = new SoundAdapter(this, mSoundCursor));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        ListView.AdapterContextMenuInfo info = (ListView.AdapterContextMenuInfo) menuInfo;
        menu.add(0, info.position, 0, R.string.enter_fullscreen);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id > -1) {
            Cursor cursor = mDbHelper.fetchAllSounds();
            cursor.moveToPosition(id);
            Intent intent = new Intent(this, FullscreenActivity.class);
            String description = cursor.getString(cursor.getColumnIndexOrThrow(SoundDbService.KEY_DESCRIPTION));
            intent.putExtra(KEY_TEXT, description);
            startActivity(intent);
        }

        return super.onContextItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
