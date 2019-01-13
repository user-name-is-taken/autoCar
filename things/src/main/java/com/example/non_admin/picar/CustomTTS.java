package com.example.non_admin.picar;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.AudioPlaybackConfiguration;
import android.provider.MediaStore;
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

    private Context context;

    public CustomTTS(Context context, TextToSpeech.OnInitListener listener){
        //AudioManager man = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        //man.setMode();
        super(context, listener);

        this.context = context;
        setOnUtteranceProgressListener(new MyProgListener());
        try {
            AudioAttributes.Builder audioAttributes = new AudioAttributes.Builder().
                    setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION_SIGNALLING).
                    setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).
                    setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED);
            setAudioAttributes(audioAttributes.build());

            //the google assistant speaker is I2S
            AudioDeviceInfo I2Sdev = findAudioDevice(AudioManager.GET_DEVICES_OUTPUTS, AudioDeviceInfo.TYPE_BUS);


            //AudioPlaybackConfiguration

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * Stolen from androidthings-googleassistant demo
     * see here for using this https://github.com/androidthings/sample-googleassistant/blob/master/README.md#audio-configuration
     * @param deviceFlag
     * @param deviceType
     * @return
     */
    private AudioDeviceInfo findAudioDevice(int deviceFlag, int deviceType) {
        AudioManager manager = (AudioManager) this.context.getSystemService(Context.AUDIO_SERVICE);
        //nager.set
        AudioDeviceInfo[] adis = manager.getDevices(deviceFlag);
        for (AudioDeviceInfo adi : adis) {
            if (adi.getType() == deviceType) {
               return adi;
            }
        }
        return null;
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
