package com.github.jing332.alistandroid.config;

import java.util.HashSet;
import java.util.Set;

/* loaded from: classes2.dex */
public class AppConfig {
    private static final String TAG = "AppConfig";
    private static final AppConfig instance = new AppConfig();


    public static AppConfig getInstance() {
        return instance;
    }

    private AppConfig() {
    }

    public int getVoicePitch() {
        return 300;
    }


    public int getVoiceSpeed() {
        return 100;
    }


    public float getVoiceVolume() {
        return 100;
    }


    public Set<String> getDialogueVoices() {
        return new HashSet<>();
    }


    public Set<String> getNarrationVoices() {
        return new HashSet<>();
    }

    public Set<String> getBackgroundMusics() {
        return new HashSet<>();
    }


    public boolean isConfigCvst() {
        return false;
    }


    public boolean isConfigNotify() {
        return false;
    }


    public boolean isConfigPriority() {
        return false;
    }


    public boolean isConfigBgMusic() {
        return false;
    }

    public boolean isConfigRole() {
        return false;
    }


    public boolean isConfigRecordVoice() {
        return false;
    }

    public boolean isConfigCheckNetwork() {
        return false;
    }

    public boolean isConfigDecodeStream() {
        return false;
    }
}
