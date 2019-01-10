package com.example.non_admin.picar;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.Locale;

import static android.content.ContentValues.TAG;

/**
 * This class just adds a simpler speak method.
 */

public class CustomTTS extends TextToSpeech {

    private static final String UTTERANCE_ID =
            "com.example.androidthings.bluetooth.audio.UTTERANCE_ID";

    public CustomTTS(Context context, TextToSpeech.OnInitListener listener){
        super(context, listener);
        setOnUtteranceProgressListener(new MyProgListener());

    }

    public void speak(String textToSpeak){
            this.speak(textToSpeak, TextToSpeech.QUEUE_ADD, null, UTTERANCE_ID);
    }


    private class MyProgListener extends UtteranceProgressListener{
        @Override
        public void onStart(String utteranceId) {
            Log.i(TAG, "Text to speech engine started");
        }

        @Override
        public void onDone(String utteranceId) {
            Log.i(TAG, "Text to speech engine done");
        }

        @Override
        public void onError(String utteranceId) {
            Log.i(TAG, "Text to speech engine error");
        }

        @Override
        public void onAudioAvailable(String utteranceId, byte[] audio){
            super.onAudioAvailable(utteranceId, audio);
            Log.i(TAG, "Text to speech engine audio available");
        }
    }
}