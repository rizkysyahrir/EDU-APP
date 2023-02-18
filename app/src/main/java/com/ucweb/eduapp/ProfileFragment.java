package com.ucweb.eduapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.gson.internal.$Gson$Preconditions;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.ucweb.eduapp.Constant.AllConstant;
import com.ucweb.eduapp.Permissions.AppPermissions;
import com.ucweb.eduapp.Utility.LoadingDialog;
import com.ucweb.eduapp.databinding.FragmentHomeBinding;
import com.ucweb.eduapp.databinding.FragmentProfileBinding;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import de.hdodenhof.circleimageview.CircleImageView;

public class    ProfileFragment extends Fragment {
    private int STORAGE_REQUEST_CODE=300;
    private AppPermissions appPermissions;
    private Uri imageUri;
    private String uid;
    private StorageReference storageProfil;
    private StorageTask uploadTask;
    private FirebaseStorage storage;
    private FirebaseFirestore db;
    private FirebaseUser users;
    private FirebaseAuth auth;
    private LoadingDialog loadingDialog;
    private ProgressDialog pd, ha;
    private DatabaseReference databaseReference;
    private DocumentReference docRef;
    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    FragmentProfileBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        users = auth.getCurrentUser();
        uid = auth.getCurrentUser().getUid();
        docRef = db.collection("users").document("profil");
        Glide.with(requireContext()).load(auth.getCurrentUser().getPhotoUrl()).into(binding.imgProfile);
        storageProfil = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User");
        appPermissions = new AppPermissions();
        loadingDialog = new LoadingDialog(getActivity());
        ha = new ProgressDialog(getActivity());
        ha.setMessage("Proses Hapus Akun...");
        pd = new ProgressDialog(getActivity());
        pd.setMessage("Please Wait...");
        db.collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(User.class);
                binding.tvNama.setText(String.valueOf(user.getNama()));
                binding.tvEmail.setText(user.getEmail());
                binding.tvPass.setText(user.getPass());
                String imageP = documentSnapshot.getString("profil");
                Glide.with(ProfileFragment.this).load(imageP).into(binding.imgProfile);
            }
        });
        binding.tvNama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUbahnama();
            }
        });
        binding.cardLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                keluarUser();
                PlaySound(1);
            }
        });
        binding.ubahPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUbahPassword();
            }
        });
        binding.imgKamera.setOnClickListener(camera -> {

            if (appPermissions.isStorageOk(getContext())) {
                mGetContent.launch("image/*");

            } else {
                getActivity().requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
            }
        });
        binding.cardDeleteAkun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TampilDialog();
            }
        });
        return binding.getRoot();
    }

    private void PlaySound(int i) {
        MediaPlayer mp = null;
        if (i == 1){
            mp = MediaPlayer.create(ProfileFragment.this.getActivity(), R.raw.sound_logout);
        }
        mp.start();
    }

    private void TampilDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileFragment.this.getContext());
        builder.setTitle("Hapus Akun");
        builder.setMessage("Kamu yakin ingin hapus akun ?");
        builder.setPositiveButton("Iya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ha.show();
                users.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        ha.dismiss();
                        if (task.isSuccessful()){
                            db.collection("users").document(uid).delete();
                            Toast.makeText(ProfileFragment.this.getContext(), "Akun anda telah dihapus", Toast.LENGTH_SHORT).show();
                            auth.signOut();
                            keluarUser();
                        } else {
                            Toast.makeText(ProfileFragment.this.getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }); builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    binding.cardLogout.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(ProfileFragment.this.getActivity(), LoginActivity.class));
    }
    });
    }

    private void uploadImage(Uri imageUri) {
            loadingDialog.startLoading();
            docRef = db.collection("users").document("profil");
            final StorageReference sR = FirebaseStorage.getInstance().getReference();
            sR.child("profil/"+System.currentTimeMillis()+"."+getFileExtention(imageUri))
                    .putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> image = taskSnapshot.getStorage().getDownloadUrl();
                    while (!image.isSuccessful());
                    Uri donwloadUrl = image.getResult();
                    final String download_url = String.valueOf(donwloadUrl);
                    Map<String, Object> Hmap = new HashMap<>();
                    Hmap.put("profil", download_url);
                    loadingDialog.stopLoading();
                    db.collection("users").document(users.getUid()).update(Hmap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getContext(), "Upload berhasil", Toast.LENGTH_SHORT).show();
                                    getFoto();
                                }
                            });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Upload gagal!", Toast.LENGTH_SHORT).show();
                    loadingDialog.stopLoading();
                }
            });
    }

    private void getFoto() {
        db.collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String imageP = documentSnapshot.getString("profil");
                Glide.with(ProfileFragment.this).load(imageP).into(binding.imgProfile);
            }
        });
    }

    private String getFileExtention(Uri uri) {
        String extension;
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        extension= mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
        return extension;
    }

    private void showUbahnama() {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.username_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final EditText userNamenew = view.findViewById(R.id.userNameup);
        final EditText pasKon = view.findViewById(R.id.passKonfirm);
        builder.setView(view);
        Button btnUpnama = view.findViewById(R.id.btn_ubahUsername);
        AlertDialog dialog = builder.create();
        dialog.show();

        btnUpnama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userNameBaru = userNamenew.getText().toString().trim();
                String passKonfir = pasKon.getText().toString().trim();
                if (TextUtils.isEmpty(userNameBaru)){
                    Toast.makeText(getActivity(), "Kolom username belum diisi!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(passKonfir)){
                    Toast.makeText(getActivity(), "Masukkan Password aktif Anda!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (passKonfir.length()<6){
                    Toast.makeText(getActivity(), "Minimal password harus 6 karakter!", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog.dismiss();
                btnUpnama(userNameBaru, passKonfir);
            }
        });
    }

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {

                    if (result != null){

                        imageUri = result;
                        uploadImage(imageUri);
                    }
                }
            });

    private void btnUpnama(String userNameBaru, String passKonfir) {
        pd.show();
        FirebaseUser user = auth.getCurrentUser();
        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), passKonfir);
        user.reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                DocumentReference documentReference = db.collection("users").document(user.getUid());
                Map<String,Object> edit = new HashMap<>();
                edit.put("nama", userNameBaru);
                documentReference.update(edit).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ProfileFragment.this.getContext(), "Username Berubah!", Toast.LENGTH_SHORT).show();
                    }
                });
                Toast.makeText(getActivity(), "Updated!", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void showUbahPassword() {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_update_pass, null);
        final EditText passLamEt = view.findViewById(R.id.passLamEt);
        final EditText passBarEt = view.findViewById(R.id.passBarEt);
        Button btn_updatePass = view.findViewById(R.id.btn_ubahPass);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();

        btn_updatePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passLama = passLamEt.getText().toString().trim();
                String passBaru = passBarEt.getText().toString().trim();

                if (TextUtils.isEmpty(passLama)){
                    Toast.makeText(getActivity(), "Masukkan Password Lama Anda!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (passBaru.length()<6){
                    Toast.makeText(getActivity(), "Minimal password harus 6 karakter!", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog.dismiss();
                btn_updatePass(passLama, passBaru);
            }
        });
    }

    private void btn_updatePass(String passLama, String passBaru) {
        pd.show();

        FirebaseUser user = auth.getCurrentUser();
        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), passLama);
        user.reauthenticate(authCredential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        user.updatePassword(passBaru)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        DocumentReference docRef = db.collection("users").document(users.getUid());
                                        Map<String,Object> edit = new HashMap<>();
                                        edit.put("pass", passBaru);
                                        docRef.update(edit).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(ProfileFragment.this.getContext(), "Profil Berhasil di Update!", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(getContext(), MainActivity.class));
                                                getActivity().finish();
                                            }
                                        });
                                        pd.dismiss();
                                        Toast.makeText(getActivity(), "Password Berubah...", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getActivity(), "Gagal! "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void keluarUser() {
        Intent logOut = new Intent(ProfileFragment.this.getContext(), LoginActivity.class);
        logOut.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(logOut);
        getActivity().finish();
    }
}