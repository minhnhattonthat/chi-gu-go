package com.nhatton.ggtalkvn.ui.fullscreen;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.nhatton.ggtalkvn.R;

import static com.nhatton.ggtalkvn.ui.main.MainActivity.tts;

public class FullscreenActivity extends Activity {

    public static final String KEY_TEXT = "toFullScreen";
    private String message = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_fullscreen);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            message = extras.getString(KEY_TEXT);
        }

        TextView textView = findViewById(R.id.fullscreen_text);

        textView.setText(message);

        findViewById(R.id.fullscreen_content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
                } else {
                    tts.speak(message, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });

    }
}
