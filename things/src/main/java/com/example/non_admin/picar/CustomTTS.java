package com.example.non_admin.picar;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.util.Log;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Locale;

import static android.content.ContentValues.TAG;

/**
 * This class adds a simpler speak method, and implements the TextToSpeech callbacks for its
 * tts member. Using this class should be as simple as initializing it, then calling its stop
 * method in onStop and its shutdown method in onDestroy. Currently the callbacks just log information,
 * but they can be made to do more...
 *
 * @see MainActivity#onStop()
 * @see MainActivity#onDestroy()
 * @see CustomTTS#stop()
 * @see CustomTTS#shutdown()
 * @see CustomTTS#speak(String)
 */

public class CustomTTS extends UtteranceProgressListener implements TextToSpeech.OnInitListener {

    private static final String UTTERANCE_ID =
            "com.example.androidthings.bluetooth.audio.UTTERANCE_ID";

    private boolean available = false;

    private static final String TTS_ENGINE = "com.google.android.tts";
    //see the log statement "Available Engines" for this
    private static MediaPlayer mediaPlayer;

    private LinkedList<String> textToSpeehQueue;
    private static File myFile;


    private TextToSpeech tts;//all the callbacks are linked to this
    private Context context;
    private AudioAttributes audioAttributes;


    private class PlaybackDone implements MediaPlayer.OnCompletionListener{
        @Override
        public void onCompletion(MediaPlayer mp) {
            syntheziseNextToFile();
        }
    }

    private class Prepared implements MediaPlayer.OnPreparedListener{
        @Override
        public void onPrepared(MediaPlayer mp) {
            mediaPlayer.start();
        }
    }

    private MediaPlayer.OnCompletionListener playbackDone;

    private MediaPlayer.OnPreparedListener preparedListener;
    /**
     * initializes the class so it can use text to speech
     * @param context
     */
    public CustomTTS(Context context){
        Log.i(TAG, "API level: " + Build.VERSION.SDK_INT);
        this.context = context;
        this.tts = new TextToSpeech(context, this, TTS_ENGINE);
        this.tts.setOnUtteranceProgressListener(this);
        this.textToSpeehQueue = new LinkedList<>();
        this.mediaPlayer = new MediaPlayer();
        AudioAttributes.Builder audioAttributesBuilder = new AudioAttributes.Builder().
                setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION_SIGNALLING).
                setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).
                setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED);

        this.audioAttributes = audioAttributesBuilder.build();
        this.playbackDone = new PlaybackDone();
        this.preparedListener = new Prepared();

        if(this.myFile == null){
            try {
                myFile = File.createTempFile("tempSoundFile", ".wav");
                myFile.deleteOnExit();
                myFile.canRead();
                myFile.canWrite();
            }catch (IOException e){
                Log.e(TAG, "Error creating temp file", e);
            }
        }
        try {
            this.mediaPlayer.setDataSource(myFile.getPath());
        }catch (IOException e){
            Log.e(TAG, "Custom TTS illegal file access", e);
        }
        this.mediaPlayer.setOnCompletionListener(this.playbackDone);
        this.mediaPlayer.setOnPreparedListener(this.preparedListener);
        this.mediaPlayer.setAudioAttributes(this.audioAttributes);

    }

    /**
     * This is only useful on API level >= 28
     * @param context
     * @param info
     */
    public CustomTTS(Context context, AudioDeviceInfo info){
        this(context);
        if(Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.P) {
            if(info == null) {
                AudioDeviceInfo mAudioOutputDevice = findAudioOutputDevice( AudioDeviceInfo.TYPE_BUS );
                //TYPE_BUS is for I2S
                this.mediaPlayer.setPreferredDevice(mAudioOutputDevice);
            }else{
                this.mediaPlayer.setPreferredDevice(info);
            }

        }else{
            Log.e(TAG, "API level is too low, using speak with the default setup");
        }

    }

    /**
     * Allows you to specify which audio output device you want to play from.
     * This is only useful in API level >= 28
     *
     * @param deviceType
     * @return the audio device
     * @see https://developer.android.com/reference/android/media/AudioDeviceInfo
     * @see https://github.com/androidthings/sample-googleassistant for parameters
     */
    private AudioDeviceInfo findAudioOutputDevice(int deviceType) {
        Log.i(TAG, "Setting the audio output device to your liking. API level is: " + Build.VERSION.SDK_INT);
        AudioManager manager = (AudioManager) this.context.getSystemService(Context.AUDIO_SERVICE);
        AudioDeviceInfo[] adis = manager.getDevices(manager.GET_DEVICES_OUTPUTS);
        for (AudioDeviceInfo adi : adis) {
            if (adi.getType() == deviceType) {
                return adi;
            }
        }
        return null;
    }



    /**
     * Checks if onInit has been called yet, to check if speak can be used yet.
     * @return
     * @see this#onInit(int)
     */
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
        Log.i(TAG, "shutting down the tts, clearing the queue, releasing the mediaPlayer");
        this.tts.shutdown();
        this.mediaPlayer.release();
        this.textToSpeehQueue.clear();
    }

    private void syntheziseNextToFile(){
        if(this.textToSpeehQueue.size() > 0) {
            Bundle params = new Bundle();
            //pre lolipop devices: https://stackoverflow.com/questions/34562771/how-to-save-audio-file-from-speech-synthesizer-in-android-android-speech-tts

            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, UTTERANCE_ID);

            this.tts.synthesizeToFile(textToSpeehQueue.pop(), params, myFile, UTTERANCE_ID);
        }
    }

    /**
     * This makes speaking much easier.
     * @param textToSpeak
     */
    public void speak(String textToSpeak){
        if(isAvailable()) {

                //this.tts.speak(textToSpeak, TextToSpeech.QUEUE_ADD, null, UTTERANCE_ID);
            Log.d(TAG, "Add text to the speaking queue");

            this.textToSpeehQueue.add(textToSpeak);
            if(this.textToSpeehQueue.size() == 1){
                Log.d(TAG, "The queue was empty, so I'm going straight to running this.");
                syntheziseNextToFile();
            }

            //see the log statement "Engine features" for what parameters can be used in params
            //Engine specific params must be prefaced by their name (see the docs)
        }else{

            Log.e(TAG, "MainActivity.ttsEngine.speak(String) received text, but it's not done initializing yet.",
                    new IllegalStateException("eager beaver!"));

        }
    }

//********************************CALLBACKS*************************************


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
        Log.i(TAG, "Text to speech engine done. Probably done synthesizing, and ready to prepare and play" +
                " the media");
        this.mediaPlayer.prepareAsync();
    }

    /**
     * According to this you need network connection.
     *
     * @param utteranceId
     * @see UtteranceProgressListener#onError(String, int)
     */
    @Override
    public void onError(String utteranceId, int errorCode) {
        switch(errorCode){
            case TextToSpeech.ERROR_INVALID_REQUEST:
                Log.e(TAG, "Text to speech: invalid request see https://developer.android.com/reference/android/speech/tts/TextToSpeech.html#ERROR_INVALID_REQUEST");
                break;
            case TextToSpeech.ERROR_NETWORK_TIMEOUT:
                Log.e(TAG, "Text to speech: network timeout see https://developer.android.com/reference/android/speech/tts/TextToSpeech.html#ERROR_NETWORK_TIMEOUT");
                break;
            case TextToSpeech.ERROR_OUTPUT:
                Log.e(TAG, "Text to speech: error output SEE https://developer.android.com/reference/android/speech/tts/TextToSpeech.html#ERROR_OUTPUT");
                break;
            case TextToSpeech.ERROR_NOT_INSTALLED_YET:
                Log.e(TAG, "Text to speech: not installed yet SEE https://developer.android.com/reference/android/speech/tts/TextToSpeech.html#ERROR_NOT_INSTALLED_YET");
                break;
            case TextToSpeech.ERROR_SYNTHESIS:
                Log.e(TAG, "Text to speech: synthesis error see https://developer.android.com/reference/android/speech/tts/TextToSpeech.html#ERROR_SYNTHESIS");
                break;
            case TextToSpeech.ERROR_SERVICE:
                Log.e(TAG, "Text to speech: service error see https://developer.android.com/reference/android/speech/tts/TextToSpeech.html#ERROR_SERVICE");
                break;
            case TextToSpeech.ERROR:
                Log.e(TAG, "Text to speech: general error see https://developer.android.com/reference/android/speech/tts/TextToSpeech.html#ERROR");
                break;
            case TextToSpeech.ERROR_NETWORK:
                Log.e(TAG, "Text to speech: network error see https://developer.android.com/reference/android/speech/tts/TextToSpeech.html#ERROR_NETWORK");
                break;
            default:
                Log.e(TAG, "Text to speech: unknown error");
        }
    }

    /**
     * according to the docs, this method is deprecated, but the compiler still requires it?
     *
     * https://developer.android.com/reference/android/speech/tts/UtteranceProgressListener.html#onError(java.lang.String)
     * @param utteranceId
     */
    @Override
    public void onError(String utteranceId) {
        Log.e(TAG, "TextToSpeech: utterance error.");
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
        Log.d(TAG, "Text to speech engine audio available");
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

            try {
                this.tts.setAudioAttributes(this.audioAttributes);

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
                setOfflineVoice(Voice.QUALITY_NORMAL);
                available = true;

                //AudioPlaybackConfiguration
                speak("Hello world");
            } catch (Exception e) {
                Log.e(TAG, "Error creating CustomTTS", e);
            }
        } else {
            Log.w(TAG, "Could not open TTS Engine (onInit status=" + status + ")");
            //ttsEngine = null;
        }
    }

    /**
     * sets the TTS voice to the voice that can be used offline that has the highest quality and
     * whose quality is at least qualityMin
     *
     * @param qualityMin the minimum quality an offline voice can have.
     */
    private void setOfflineVoice(int qualityMin){

        // find a voice that doesn't require a network connection.
        Voice bestOfflineVoice = null;
        for (Voice curV : this.tts.getVoices()){
            if(!curV.isNetworkConnectionRequired()) {
                if(bestOfflineVoice == null || curV.getQuality() > bestOfflineVoice.getQuality()){
                    bestOfflineVoice = curV;
                }
                Log.d(TAG, "offline voice possibility: " + bestOfflineVoice.getName());

            }
        }
        if(bestOfflineVoice != null && bestOfflineVoice.getQuality() >= qualityMin) {
            //if the best o
            //https://developer.android.com/reference/android/speech/tts/Voice.html#QUALITY_NORMAL
            this.tts.setVoice(bestOfflineVoice);
        }
        Log.i(TAG, "Available Engines: " + Arrays.toString(this.tts.getEngines().toArray()));
        Log.i(TAG, "Engine features: " + Arrays.toString(this.tts.getVoice().getFeatures().toArray()));
    }
}
