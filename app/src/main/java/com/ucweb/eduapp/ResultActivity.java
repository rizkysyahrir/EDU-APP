package com.ucweb.eduapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ucweb.eduapp.databinding.ActivityResultBinding;

public class ResultActivity extends AppCompatActivity {

    int KOIN = 10;
    ActivityResultBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int jawabanBetul = getIntent().getIntExtra("correct", 0);
        int totalJawaban = getIntent().getIntExtra("total", 0);
        long point = jawabanBetul*KOIN;
        binding.tvScore.setText(String.format("%d/%d", jawabanBetul, totalJawaban));
        binding.tvKoinPeroleh.setText(String.valueOf(point));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .update("coins", FieldValue.increment(point));

        binding.btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent Home = new Intent(ResultActivity.this, LoginActivity.class);
                Home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(Home);
                finish();

            }
        });

        binding.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String Body = "Download Aplikasi ini";
                String Sub = "http://play.google.com/store/apps/details?id=com.ucweb.eduapp";
                intent.putExtra(Intent.EXTRA_TEXT, Body);
                intent.putExtra(Intent.EXTRA_TEXT, Sub);
                startActivity(Intent.createChooser(intent, "Share using"));
            }
        });
    }
}