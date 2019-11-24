package com.myapp.constants;

import android.content.Context;
import android.media.AudioManager;

public class Constants {
    public static final String Region = "region";
    public static final String BotName = "Bot Name";
    public static final String BotAlias = "BotAlias";
    public static String SelectedLanguage = "Langauge";
    public static final String LangTranslatorURL = "Your LangTranslatorURL";
    public static String DIALOG_STATE = "";
    public static String AWSAccessKeyId = "AWSAccessKeyId";
    public static String AWSSecretKey = "AWSSecretKey";

    public static void muteAudio(Context context){
        //mute audio
        AudioManager amanager=(AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
        amanager.setStreamMute(AudioManager.STREAM_ALARM, true);
        amanager.setStreamMute(AudioManager.STREAM_MUSIC, true);
        amanager.setStreamMute(AudioManager.STREAM_RING, true);
        amanager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
    }

    public static void unmuteAudio(Context context){
        //unmute audio
        AudioManager amanager=(AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

        amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
        amanager.setStreamMute(AudioManager.STREAM_ALARM, false);
        amanager.setStreamMute(AudioManager.STREAM_MUSIC, false);
        amanager.setStreamMute(AudioManager.STREAM_RING, false);
        amanager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
    }
}
