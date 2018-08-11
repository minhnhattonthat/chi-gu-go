package com.nhatton.ggtalkvn.ui.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nhatton.ggtalkvn.R;
import com.nhatton.ggtalkvn.data.SoundDbService;
import com.nhatton.ggtalkvn.ui.list.CollectionActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    public static final String LOCALE_AS_STRING = "vi_VN";
    private static final int SMOOTHNESS = 10;
    private static final int MY_DATA_CHECK_CODE = 0;

    private EditText txtText;
    private TextView pitchValue;
    private TextView speedValue;

    public static TextToSpeech tts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //check for TTS resource available
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);

        ImageButton btnSpeak = findViewById(R.id.input_button);
        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String sentence = txtText.getText().toString();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    tts.speak(sentence, TextToSpeech.QUEUE_FLUSH, null, null);
                } else {
                    tts.speak(sentence, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });

        txtText = findViewById(R.id.input_text);
        txtText.requestFocus();

        pitchValue = findViewById(R.id.pitch_value);
        pitchValue.setText("1.0");

        SeekBar pitchBar = findViewById(R.id.pitch_bar);
        pitchBar.setOnSeekBarChangeListener(new SeekBar.
                OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float floatVal = .25f * i + 0.5f;
                pitchValue.setText(String.valueOf(floatVal));
                tts.setPitch(floatVal);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

        });

        speedValue = findViewById(R.id.speed_value);
        speedValue.setText("1.0");

        SeekBar speedBar = findViewById(R.id.speed_bar);
        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float floatVal = .5f * i + 0.5f;
                speedValue.setText(String.valueOf(floatVal));
                tts.setSpeechRate(floatVal);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBar volControl = findViewById(R.id.vol_bar);
        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        assert audioManager != null;
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        volControl.setMax(maxVolume * SMOOTHNESS);
        volControl.setProgress(curVolume * SMOOTHNESS);
        volControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress / SMOOTHNESS, 0);
            }
        });

    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(new Locale(LOCALE_AS_STRING));
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            }
        } else {
            Log.e("TTS", "Initialization Failed!");
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                tts = new TextToSpeech(this, this);
            } else {
                Intent installIntent = new Intent();
                installIntent.setAction(
                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            String description = txtText.getText().toString();
            SoundDbService mDbHelper = new SoundDbService(this);
            long t = mDbHelper.open().createSound(description);

            if (t < 0) {
                Toast.makeText(this, R.string.toast_exist, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.toast_saved, Toast.LENGTH_SHORT).show();
            }

            mDbHelper.close();

        } else if (item.getItemId() == R.id.action_list) {
            Intent intent = new Intent(MainActivity.this, CollectionActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tts.shutdown();
    }
}
