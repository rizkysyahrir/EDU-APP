package com.ucweb.eduapp;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ucweb.eduapp.databinding.FragmentLeadBoardBinding;
import java.util.ArrayList;

public class LeadBoardFragment extends Fragment {

    ImageView profilImage;

    public LeadBoardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    FragmentLeadBoardBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLeadBoardBinding.inflate(inflater, container, false);
        View view = inflater.inflate(R.layout.row_leadboard, container, false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<User> users = new ArrayList<>();
        LeadBAdapter adapter = new LeadBAdapter(getContext(), users);
        profilImage = view.findViewById(R.id.imageView7);
        binding.recyclerLeadBoard.setAdapter(adapter);
        binding.recyclerLeadBoard.setLayoutManager(new LinearLayoutManager(getContext()));
        db.collection("users")
                .orderBy("coins", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot snapshot : queryDocumentSnapshots){
                    String imageProfil = snapshot.getString("profil");
                    Glide.with(LeadBoardFragment.this).load(imageProfil).into(profilImage);
                    User user = snapshot.toObject(User.class);
                    users.add(user);
                }
                    adapter.notifyDataSetChanged();
            }
        });
        return binding.getRoot();
    }
}