package com.ucweb.eduapp.Matpel;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.ucweb.eduapp.R;
import com.ucweb.eduapp.databinding.ActivityMatematikaBinding;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
public class MatematikaActivity extends AppCompatActivity {

    ActivityMatematikaBinding binding;
    FirebaseFirestore db;
    FirebaseDatabase database;
    SimpleExoPlayer exoPlayer;
    FirebaseStorage firebaseStorage;
    RecyclerView videoRv;
    Button btn_download, btn_download1;
    DatabaseReference reference;
    StorageReference storageReference, ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matematika);
//        videoRv = findViewById(R.id.videoRv);
        YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);
        YouTubePlayerView youTubePlayerView1 = findViewById(R.id.youtube_player_view1);
        getLifecycle().addObserver(youTubePlayerView1);


//        videoRv.setHasFixedSize(true);
//        videoRv.setLayoutManager(new LinearLayoutManager(this));
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("video");

        db = FirebaseFirestore.getInstance();
        btn_download = findViewById(R.id.btn_download);
        btn_download1 = findViewById(R.id.btn_download1);

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download();
            }
        });
        btn_download1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                download1();

            }
        });


    }

    private void download1() {
        storageReference = firebaseStorage.getInstance().getReference();
        ref  = storageReference.child("materipdf/MTK SEM 2.pdf");
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url = uri.toString();
                donwloadFile(MatematikaActivity.this,"Materi_MTK_KelasVII_UBS_SEM2",".pdf", DIRECTORY_DOWNLOADS, url);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void download() {
        storageReference = firebaseStorage.getInstance().getReference();
        ref  = storageReference.child("materipdf/MTK SEM 1.pdf");
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url = uri.toString();
                donwloadFile(MatematikaActivity.this,"Materi_MTK_KelasVII_UBS_SEM1",".pdf", DIRECTORY_DOWNLOADS, url);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void donwloadFile(Context context, String fileName, String fileExtension, String destinationDirectory, String url) {
        DownloadManager downloadManager = (DownloadManager) context
                .getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);
        downloadManager.enqueue(request);

    }

}