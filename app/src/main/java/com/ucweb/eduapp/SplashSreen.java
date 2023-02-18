package com.ucweb.eduapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashSreen extends AppCompatActivity {

    //Variable
    Animation animTop, animBottom;
    MediaPlayer music;
    ImageView im;
    TextView tv1, tv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_sreen);

        //animation
        animTop = AnimationUtils.loadAnimation(this, R.anim.anim_top);
        animBottom = AnimationUtils.loadAnimation(this, R.anim.bottom_anim);

        //hocks
        im = findViewById(R.id.imageView);
        tv1 = findViewById(R.id.textView);
        tv2 = findViewById(R.id.textView2);

        im.setAnimation(animTop);
        tv1.setAnimation(animBottom);
        tv2.setAnimation(animBottom);

        Thread timer=new Thread()
        {
            @Override            public void run() {
                try                {
                    music= MediaPlayer.create(SplashSreen.this,R.raw.sound_welcome);
                    music.start();
                    sleep(5600);

                }
                catch(InterruptedException e)
                {

                }
                finally {
                    Intent i=new Intent(SplashSreen.this,LoginActivity.class);
                    startActivity(i);

                }
            }
        };

        timer.start();

    }
    @Override    protected void onPause() {
        super.onPause();
        music.release();
        finish();
    }
}