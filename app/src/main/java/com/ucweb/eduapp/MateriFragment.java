package com.ucweb.eduapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ucweb.eduapp.Matpel.AgamaIslam;
import com.ucweb.eduapp.Matpel.BahasaIndo;
import com.ucweb.eduapp.Matpel.Informatika;
import com.ucweb.eduapp.Matpel.MatematikaActivity;
import com.ucweb.eduapp.Matpel.SainsActivity;
import com.ucweb.eduapp.Matpel.Sejarah;
import com.ucweb.eduapp.databinding.FragmentMateriBinding;
public class MateriFragment extends Fragment {

    public MateriFragment() {
        // Required empty public constructor
    }

    FragmentMateriBinding binding;
    FirebaseFirestore db;
    User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMateriBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(User.class);
                binding.tvKoinAnda.setText(String.valueOf(user.getCoins()));
                //binding.tvKoinAnda.setText(user.getCoins() + "");
            }
        });
        binding.cardMTK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), MatematikaActivity.class));
            }
        });
        binding.cardSains.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), SainsActivity.class));
            }
        });
        binding.cardTekno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), Informatika.class));
            }
        });
        binding.cardAgama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AgamaIslam.class));
            }
        });
        binding.cardBahasa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), BahasaIndo.class));
            }
        });
        binding.cardSejarah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), Sejarah.class));
            }
        });
        return binding.getRoot();
    }
}