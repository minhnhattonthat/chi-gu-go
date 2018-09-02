package com.nhatton.ggtalkvn.ui.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.nhatton.ggtalkvn.BuildConfig;
import com.nhatton.ggtalkvn.R;
import com.nhatton.ggtalkvn.data.Sound;
import com.nhatton.ggtalkvn.tts.TTSService;
import com.nhatton.ggtalkvn.ui.BaseActivity;
import com.nhatton.ggtalkvn.ui.camera.CameraActivity;
import com.nhatton.ggtalkvn.ui.fullscreen.FullscreenActivity;

import java.util.Collections;
import java.util.List;

import static com.nhatton.ggtalkvn.ui.fullscreen.FullscreenActivity.KEY_SOUND;

public class MainActivity extends BaseActivity {

    private static final int MY_DATA_CHECK_CODE = 0;

    private static final String TAG = "MainActivity";
    public SoundAdapter mAdapter;
    private TextView pitchValue;
    private TextView speedValue;
    private EditText inputTextView;
    private float speedFloatVal;
    private float pitchFloatVal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initAds();

        //check for TTS resource available
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);

        initTextInput();

        initReadButton();

        initSpeakButton();

        initPitchPanel();

        initSpeedPanel();

        initListView();

        fillData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                TTSService.initialize(this);
                this.getLifecycle().addObserver(new TTSService.TTSLifecycleObserver());
            } else {
                Intent installIntent = new Intent();
                installIntent.setAction(
                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }

        return super.dispatchTouchEvent(event);
    }

    private void initAds() {
        MobileAds.initialize(this, BuildConfig.ADMOB_APP_ID);

        AdView mAdView = findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void initTextInput() {
        inputTextView = findViewById(R.id.input_text);

        findViewById(R.id.clear_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputTextView.setText("");
            }
        });
    }

    private void initReadButton() {
        findViewById(R.id.read_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initSpeakButton() {
        ImageButton speakButton = findViewById(R.id.input_button);
        speakButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String sentence = inputTextView.getText().toString();

                if (sentence.isEmpty()) return;

                Sound sound = new Sound.Builder()
                        .text(sentence)
                        .pitch(pitchFloatVal)
                        .speed(speedFloatVal)
                        .build();

                TTSService.getInstance().speak(sound);

                mAdapter.insert(sound);

                TTSService.getInstance().writeToFireStore(sound, MainActivity.this);
            }
        });
    }

    private void initPitchPanel() {
        pitchValue = findViewById(R.id.pitch_value);
        pitchValue.setText("1.0");

        SeekBar pitchBar = findViewById(R.id.pitch_bar);
        pitchBar.setOnSeekBarChangeListener(new SeekBar.
                OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                pitchFloatVal = .25f * i + 0.5f;
                pitchValue.setText(String.valueOf(pitchFloatVal));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

        });
    }

    private void initSpeedPanel() {
        speedValue = findViewById(R.id.speed_value);
        speedValue.setText("1.0");

        SeekBar speedBar = findViewById(R.id.speed_bar);
        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                speedFloatVal = .5f * i + 0.5f;
                speedValue.setText(String.valueOf(speedFloatVal));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initListView() {
        RecyclerView listView = findViewById(R.id.sound_list);
        mAdapter = new SoundAdapter(daoSession, new SoundCallback() {
            @Override
            public void onSoundSelected(Sound sound) {
                TTSService.getInstance().speak(sound);
            }

            @Override
            public void onFullScreen(Sound sound) {
                Intent intent = new Intent(MainActivity.this, FullscreenActivity.class);
                intent.putExtra(KEY_SOUND, sound);
                startActivity(intent);
            }

            @Override
            public void onExport(Sound sound) {
                TTSService.getInstance().export(sound, MainActivity.this);
            }
        });
        listView.setAdapter(mAdapter);

        ItemTouchHelper touchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder,
                                         int direction) {
                        int position = viewHolder.getAdapterPosition();
                        mAdapter.remove(position);
                    }
                });

        touchHelper.attachToRecyclerView(listView);
    }

    private void fillData() {
        List<Sound> sounds = daoSession.getSoundDao().loadAll();
        Collections.sort(sounds);
        Collections.reverse(sounds);
        mAdapter.setList(sounds);
    }

}
