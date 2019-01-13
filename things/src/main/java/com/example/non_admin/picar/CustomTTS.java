package com.example.non_admin.picar;

import android.content.Context;
import android.media.AudioAttributes;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;


import java.util.Locale;

import static android.content.ContentValues.TAG;

/**
 * This class adds a simpler speak method, and implements the TextToSpeech callbacks for its
 * tts member.
 */

public class CustomTTS extends UtteranceProgressListener implements TextToSpeech.OnInitListener {

    private static final String UTTERANCE_ID =
            "com.example.androidthings.bluetooth.audio.UTTERANCE_ID";

    private boolean available = false;

    private Context context;

    private TextToSpeech tts;//all the callbacks are linked to this

    /**
     * initializes the class so it can use text to speech
     * @param context
     */
    public CustomTTS(Context context){
        //AudioManager man = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        //man.setMode();
        this.context = context;
        this.tts = new TextToSpeech(context, this);
        this.tts.setOnUtteranceProgressListener(this);
    }


    public boolean isAvailable() {
        return available;
    }

    /**
     * @see MainActivity#onStop()
     */
    public void stop(){
        this.tts.stop();
    }

    /**
     * @see MainActivity#onDestroy()
     */
    public void shutdown(){
        this.tts.shutdown();
    }


    /**
     * This makes speaking much easier.
     * @param textToSpeak
     */
    public void speak(String textToSpeak){
            this.tts.speak(textToSpeak, TextToSpeech.QUEUE_ADD, null, UTTERANCE_ID);
    }


    /**
     *
     * @param utteranceId
     * @see UtteranceProgressListener#onStart(String)
     */

    @Override
    public void onStart(String utteranceId) {
        Log.i(TAG, "Text to speech engine started");
    }

    /**
     *
     * @param utteranceId
     * @see UtteranceProgressListener#onDone(String)
     */
    @Override
    public void onDone(String utteranceId) {
        Log.i(TAG, "Text to speech engine done");
    }

    /**
     *
     * @param utteranceId
     * @see UtteranceProgressListener#onError(String, int)
     */
    @Override
    public void onError(String utteranceId) {
        Log.i(TAG, "Text to speech engine error");
    }

    /**
     *
     * @param utteranceId
     * @param audio
     * @see UtteranceProgressListener#onAudioAvailable(String, byte[])
     */
    @Override
    public void onAudioAvailable(String utteranceId, byte[] audio){
        super.onAudioAvailable(utteranceId, audio);
        Log.i(TAG, "Text to speech engine audio available");
    }

    /**
     * This method is Overriden from the TextToSpeech.OnInitListener interface.
     * It is called when the ttsEngine is initialized.
     * @param status
     * @see TextToSpeech.OnInitListener#onInit(int)
     */
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            Log.i(TAG, "Created text to speech engine");
            speak("Hello world");

            try {
                AudioAttributes.Builder audioAttributes = new AudioAttributes.Builder().
                        setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION_SIGNALLING).
                        setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).
                        setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED);
                this.tts.setAudioAttributes(audioAttributes.build());

                //Locale.ENGLISH
                Locale myLoc = new Locale("en", "US");
                //Locale myLoc = Locale.US;
                Locale.setDefault(myLoc);
                //I'm having an error where the local isn't set.
                // this sets the local: https://proandroiddev.com/change-language-programmatically-at-runtime-on-android-5e6bc15c758
                //I don't think this is why the speech isn't working.
                this.tts.setLanguage(myLoc);
                this.tts.setPitch(1f);
                this.tts.setSpeechRate(1f);

                available = true;

                //AudioPlaybackConfiguration

            } catch (Exception e) {
                Log.e(TAG, "Error creating CustomTTS", e);
            }
        } else {
            Log.w(TAG, "Could not open TTS Engine (onInit status=" + status + ")");
            //ttsEngine = null;
        }
    }
}
