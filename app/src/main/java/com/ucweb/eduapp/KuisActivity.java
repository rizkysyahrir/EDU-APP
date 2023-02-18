package com.ucweb.eduapp;

import androidx.appcompat.app.AppCompatActivity;
import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ucweb.eduapp.databinding.ActivityKuisBinding;
import org.w3c.dom.Text;
import java.util.ArrayList;
import java.util.Random;

public class KuisActivity extends AppCompatActivity {

    ActivityKuisBinding binding;
    ArrayList<Pertanyaan> pertanyaans;
    int index = 0;
    Pertanyaan pertanyaan;
    CountDownTimer timer;
    FirebaseFirestore db;
    int jawabanBetul = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityKuisBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final String kategoriID = getIntent().getStringExtra("kategoriID");
        Random random = new Random();
        final int acak = random.nextInt(10);
        pertanyaans = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        db.collection("kategori")
                .document(kategoriID)
                .collection("pertanyaans")
                .whereGreaterThanOrEqualTo("index", acak)
                .limit(5).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.getDocuments().size() <= 5){
                    db.collection("kategori")
                            .document(kategoriID)
                            .collection("pertanyaans")
                            .whereGreaterThanOrEqualTo("index", acak)
                            .limit(5).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                for(DocumentSnapshot snapshot: queryDocumentSnapshots){
                                    Pertanyaan pertanyaan = snapshot.toObject(Pertanyaan.class);
                                    pertanyaans.add(pertanyaan);
                                 }
                            setPertanyaanSelanjutnya();
                        }
                    });
                } else {
                    for(DocumentSnapshot snapshot: queryDocumentSnapshots){
                        Pertanyaan pertanyaan = snapshot.toObject(Pertanyaan.class);
                        pertanyaans.add(pertanyaan);
                    }
                    setPertanyaanSelanjutnya();
                }
            }
        });
        resetWaktu();
    }

    void resetWaktu(){
        timer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.tvTimer.setText(String.valueOf(millisUntilFinished/1000));
            }

            @Override
            public void onFinish() {
                ubahPertanyaan();
            }
        };
    }

    private void ubahPertanyaan() {

        timer.start();
        if (index < pertanyaans.size() - 1){

            index++;
            playAnim(binding.tvQuestion, 0, 0);
            playAnim(binding.option1, 0, 1);
            playAnim(binding.option2, 0, 2);
            playAnim(binding.option3, 0, 3);
            playAnim(binding.option4, 0, 4);

            binding.tvQuestCount.setText(String.format("%d/%d", (index+1), pertanyaans.size()));
            pertanyaan = pertanyaans.get(index);
            binding.tvQuestion.setText(pertanyaan.getPertanyaan());
            binding.option1.setText(pertanyaan.getOpsi1());
            binding.option2.setText(pertanyaan.getOpsi2());
            binding.option3.setText(pertanyaan.getOpsi3());
            binding.option4.setText(pertanyaan.getOpsi4());

        } else{
            timer.cancel();
        }
    }

    private void playAnim(View view, final int value, int viewNum) {
        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500)
                .setStartDelay(100).setInterpolator(new DecelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (value == 0){
                            switch (viewNum){
                                case 0:
                                    ((TextView)view).setText(pertanyaans.get(index).getPertanyaan());
                                    break;
                                case 1:
                                    ((TextView)view).setText(pertanyaans.get(index).getOpsi1());
                                    break;
                                case 2:
                                    ((TextView)view).setText(pertanyaans.get(index).getOpsi2());
                                    break;
                                case 3:
                                    ((TextView)view).setText(pertanyaans.get(index).getOpsi3());
                                    break;
                                case 4:
                                    ((TextView)view).setText(pertanyaans.get(index).getOpsi4());
                                    break;
                            }
                            playAnim(view, 1, viewNum);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
    }

    void tampilJawaban(){
        if (pertanyaan.getJawaban().equals(binding.option1.getText().toString()))
            binding.option1.setBackground(getResources().getDrawable(R.drawable.opsi_betul));
        else if (pertanyaan.getJawaban().equals(binding.option2.getText().toString()))
            binding.option2.setBackground(getResources().getDrawable(R.drawable.opsi_betul));
        else if (pertanyaan.getJawaban().equals(binding.option3.getText().toString()))
            binding.option3.setBackground(getResources().getDrawable(R.drawable.opsi_betul));
        else if (pertanyaan.getJawaban().equals(binding.option4.getText().toString()))
            binding.option4.setBackground(getResources().getDrawable(R.drawable.opsi_betul));

    }

    void setPertanyaanSelanjutnya(){
        if (timer !=null)
            timer.cancel();

        timer.start();
        if (index < pertanyaans.size()){
            playAnim(binding.tvQuestion, 0,0);
            playAnim(binding.option1, 0, 1);
            playAnim(binding.option2, 0, 2);
            playAnim(binding.option3, 0, 3);
            playAnim(binding.option4, 0, 4);
            binding.tvQuestCount.setText(String.format("%d/%d", (index+1), pertanyaans.size()));
            pertanyaan = pertanyaans.get(index);
            binding.tvQuestion.setText(pertanyaan.getPertanyaan());
            binding.option1.setText(pertanyaan.getOpsi1());
            binding.option2.setText(pertanyaan.getOpsi2());
            binding.option3.setText(pertanyaan.getOpsi3());
            binding.option4.setText(pertanyaan.getOpsi4());
        }
    }
    void cekJawaban(TextView textView){
        String piihanJawaban = textView.getText().toString();
        if (piihanJawaban.equals(pertanyaan.getJawaban())){
            jawabanBetul++;
            textView.setBackground(getResources().getDrawable(R.drawable.opsi_betul));
        } else{
            tampilJawaban();
            textView.setBackground(getResources().getDrawable(R.drawable.opsi_salah));
        }
    }
    void reset(){
        binding.option1.setBackground(getResources().getDrawable(R.drawable.border_opsi));
        binding.option2.setBackground(getResources().getDrawable(R.drawable.border_opsi));
        binding.option3.setBackground(getResources().getDrawable(R.drawable.border_opsi));
        binding.option4.setBackground(getResources().getDrawable(R.drawable.border_opsi));
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.option1:
            case R.id.option2:
            case R.id.option3:
            case R.id.option4:

                if (timer !=null)
                    timer.cancel();
                TextView pilih = (TextView) view;
                cekJawaban(pilih);
                break;

            case R.id.btn_next:
                reset();
                if (index <= pertanyaans.size()) {
                    index++;
                    setPertanyaanSelanjutnya();
                } else{
                    Intent i = new Intent(KuisActivity.this, ResultActivity.class);
                    i.putExtra("correct", jawabanBetul);
                    i.putExtra("total", pertanyaans.size());
                    startActivity(i);
                }
                break;

            case R.id.btn_quit:
                btnQuit();
                break;
        }
    }

    private void btnQuit() {
        Intent Quit = new Intent(KuisActivity.this, LoginActivity.class);
        Quit.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(Quit);
        finish();
    }
}