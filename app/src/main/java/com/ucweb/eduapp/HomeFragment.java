package com.ucweb.eduapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.ucweb.eduapp.databinding.FragmentHomeBinding;
import java.util.ArrayList;
public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    FragmentHomeBinding binding;
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();

        final ArrayList<KategoriModel> kategori = new ArrayList<>();
        final KategoriAdapter kategoriAdapter = new KategoriAdapter(getContext(), kategori);

        db.collection("kategori")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        kategori.clear();
                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            KategoriModel model = snapshot.toObject(KategoriModel.class);
                            model.setKategoriID(snapshot.getId());
                            kategori.add(model);
                        }
                        kategoriAdapter.notifyDataSetChanged();
                    }
                });

        binding.recyclerCategoryList.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.recyclerCategoryList.setAdapter(kategoriAdapter);
        binding.spinweel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SpinActivity.class));
            }
        });

        binding.gamesTb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), GameActivity.class));
            }
        });
        return binding.getRoot();
    }
}