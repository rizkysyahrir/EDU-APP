package com.ucweb.eduapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;

import com.ucweb.eduapp.databinding.ActivityGameBinding;
import com.ucweb.eduapp.databinding.ActivityKuisBinding;

public class GameActivity extends Activity {

    ActivityGameBinding binding;
    SharedPreferences save;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Helper.InitSounds(this, new String[]{"click"});

        binding.gameName.setText("Tebak Gambar");
        binding.gameName.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/headfont.ttf"));
        save = getSharedPreferences("SAVE_GAME", 0);
        editor = save.edit();

        binding.continueButton.setBackgroundResource(R.drawable.mybutton);
        binding.continueButton.setText("Continue");
        binding.continueButton.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/normalfont.ttf"));
        if (save.contains("gameSaved") && save.getBoolean("gameSaved", false)) {
            binding.continueButton.setVisibility(View.VISIBLE);
        } else {
            binding.continueButton.setVisibility(View.GONE);
        }
        binding.continueButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Helper.playSound(getApplicationContext(), "click");
                editor.putBoolean("continue", true);
                editor.commit();
                Intent game_intent = new Intent(GameActivity.this, TebakGambar.class);
                startActivity(game_intent);
                finish();
            }
        });

        //start the new game
        binding.playButton.setBackgroundResource(R.drawable.mybutton);
        binding.playButton.setText("New Game");
        binding.playButton.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/normalfont.ttf"));
        binding.playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Helper.playSound(getApplicationContext(), "click");
                editor.putBoolean("continue", false);
                editor.commit();
                Intent game_intent = new Intent(GameActivity.this, TebakGambar.class);
                startActivity(game_intent);
                finish();
            }
        });

        binding.btnBack.setBackgroundResource(R.drawable.mybutton);
        binding.btnBack.setText("Back");
        binding.btnBack.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/normalfont.ttf"));
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Helper.playSound(getApplicationContext(), "click");
                finish();
            }
        });
    }
}