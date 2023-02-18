/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ucweb.eduapp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.DisplayMetrics;

import java.util.HashMap;

public class Helper {

    //getting Device Screen Width and Height
    public static Point getScreenSize() {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return new Point(metrics.widthPixels, metrics.heightPixels);
    }

    private static HashMap<String, Integer> sounds;
    private static SoundPool sp;

    public static void InitSounds(Context c, String[] names) {
        sounds = new HashMap<String, Integer>();
        sp = new SoundPool(10, AudioManager.STREAM_NOTIFICATION, 100);
        for (int i = 0; i < names.length; i++) {
            sounds.put(names[i], sp.load(c, c.getResources().getIdentifier(names[i], "raw", c.getPackageName()), 1));
        }

    }

    public static void playSound(Context c, String name) {
        AudioManager mgr = (AudioManager) c.getSystemService(Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
        float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
        float volume = name.equals("click")? streamVolumeCurrent/streamVolumeMax-0.3f:streamVolumeCurrent/streamVolumeMax;
        /* Play the sound with the correct volume */
        sp.play(sounds.get(name), volume, volume, 1, 0, 1f);
    }
}
