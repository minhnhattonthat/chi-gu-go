package com.nhatton.ggtalkvn.tts;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nhatton.ggtalkvn.BuildConfig;
import com.nhatton.ggtalkvn.R;
import com.nhatton.ggtalkvn.data.Sound;
import com.nhatton.ggtalkvn.util.DateConverter;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.provider.Settings.Secure.ANDROID_ID;
import static android.speech.tts.TextToSpeech.SUCCESS;

public class TTSService {

    private static final String TAG = "TTSService";

    private static final int MY_CURRENT_REQUEST_WRITE_EXTERNAL_STORAGE = 2;

    private static final String VN_LOCALE = "vi_VN";

    private static final String FOLDER_NAME = "Chi Gu Go";

    private static TTSService INSTANCE;

    private static TextToSpeech tts;

    private TTSService() {

    }

    public static void initialize(Context context) {
        tts = new TextToSpeech(context.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == SUCCESS) {
                    int result = tts.setLanguage(new Locale(VN_LOCALE));
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This language is not supported");
                    }
                } else {
                    Log.e("TTS", "Initialization failed!");
                }
            }
        });
    }

    public static TTSService getInstance() {
        if (INSTANCE == null) {
            synchronized (TTSService.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TTSService();
                }
            }
        }

        return INSTANCE;
    }

    public void speak(String sentence) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(sentence, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            tts.speak(sentence, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public void speak(Sound sound) {
        tts.setSpeechRate(sound.getSpeed());
        tts.setPitch(sound.getPitch());
        speak(sound.getText());
    }

    public void export(Sound sound, Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{WRITE_EXTERNAL_STORAGE},
                    MY_CURRENT_REQUEST_WRITE_EXTERNAL_STORAGE);
        }

        String text = sound.getText();

        if (text.length() > 10) {
            text = text.substring(0, 11);
        }

        String fileName = text.replace(" ", "-") +
                "_" +
                DateConverter.getTimeStamp() +
                ".wav";
        File file = new File(createExportFolder(), fileName);

        int result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bundle params = new Bundle();
            params.putFloat("pitch", sound.getPitch());
            params.putFloat("rate", sound.getSpeed());

            result = tts.synthesizeToFile(text, params, file, fileName);
        } else {
            HashMap<String, String> params = new HashMap<>();
            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, fileName);
            params.put("pitch", String.valueOf(sound.getPitch()));
            params.put("rate", String.valueOf(sound.getPitch()));

            result = tts.synthesizeToFile(sound.getText(), params,
                    createExportFolder().getAbsolutePath() + fileName);
        }

        if (result == SUCCESS) {
            Toast.makeText(activity, R.string.toast_exported, Toast.LENGTH_SHORT).show();
        }
    }

    private File createExportFolder() {
        File folder = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MUSIC), FOLDER_NAME);
        if (!folder.exists()) {
            if (!folder.mkdir()) {
                Log.e("TTS", "Directory not created");
            }
        }

        return folder;
    }

    @SuppressLint("HardwareIds")
    public void writeToFireStore(Sound sound, Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        HashMap<String, Object> map = new HashMap<>();
        map.put("sentence", sound.getText());
        map.put("timestamp", System.currentTimeMillis());

        db.collection("users")
                .document(Settings.Secure.getString(context.getContentResolver(), ANDROID_ID))
                .collection("sentences")
                .add(map)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (!BuildConfig.DEBUG) {
                            Crashlytics.log("Error adding document " + e.getMessage());
                        } else {
                            Log.w(TAG, "Error adding document", e);
                        }
                    }
                });
    }

    public static class TTSLifecycleObserver implements LifecycleObserver {

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        void onDestroy() {
            tts.shutdown();
        }

    }
}
