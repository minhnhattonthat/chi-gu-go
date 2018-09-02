package com.nhatton.ggtalkvn.ui.fullscreen;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.nhatton.ggtalkvn.R;
import com.nhatton.ggtalkvn.data.Sound;
import com.nhatton.ggtalkvn.tts.TTSService;

public class FullscreenActivity extends Activity {

    public static final String KEY_SOUND = "sound";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_fullscreen);

        Sound sound = null;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            sound = extras.getParcelable(KEY_SOUND);
        }

        if (sound != null) {
            init(sound);
        }

    }

    private void init(final Sound sound) {
        TextView textView = findViewById(R.id.fullscreen_text);

        final String message = sound.getText();

        textView.setText(message);

        findViewById(R.id.fullscreen_content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TTSService.getInstance().speak(sound);
            }
        });
    }
}
