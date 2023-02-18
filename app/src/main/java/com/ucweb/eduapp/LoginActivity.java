package com.ucweb.eduapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ucweb.eduapp.Utility.LoadingDialog;
import com.ucweb.eduapp.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private TextView tvCA, tvFP;
    private ActivityLoginBinding binding;
    private FirebaseAuth auth;
    private LoadingDialog loadingDialog;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Proses Login berjalan...");

        if (auth.getCurrentUser() != null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, pass;
                email = binding.etEmail.getText().toString().trim();
                pass = binding.etPassword.getText().toString().trim();

                if (email.isEmpty()){
                    binding.etEmail.setError("Email anda kosong!");
                    binding.etEmail.requestFocus(); return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    binding.etEmail.setError("Masukkan email terdaftar!");
                    binding.etEmail.requestFocus(); return;
                }
                if (pass.isEmpty()){
                    binding.etPassword.setError("Password Anda kosong!");
                    binding.etPassword.requestFocus(); return;
                }
                if (pass.length() < 6){
                    binding.etPassword.setError("Minimal Password 6 Karakter!");
                    binding.etPassword.requestFocus(); return;
                }
                dialog.show();
                auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            if (user.isEmailVerified()){
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                dialog.show();
                                finish();
                            }else{
                                user.sendEmailVerification();
                                Toast.makeText(LoginActivity.this, "Cek email untuk verifikasi akun!", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        }else{
                            Toast.makeText(LoginActivity.this, "Gagal Login! Cek kembali Email dan Password!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

        binding.tvCA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        binding.tvFP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetMail = new EditText(v.getContext());
                AlertDialog.Builder passResetDialog = new AlertDialog.Builder(v.getContext());
                passResetDialog.setTitle("Reset Password Anda?");
                passResetDialog.setMessage("Masukkan email anda untuk menerima link Reset Password!");
                passResetDialog.setView(resetMail);

                passResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mail = resetMail.getText().toString();
                        auth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(LoginActivity.this, "Periksa Email Masuk Anda!", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this, "Error, periksa kembali email Anda!" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                passResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                passResetDialog.create().show();
            }
        });

    }

}