package com.ucweb.eduapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class KategoriAdapter extends RecyclerView.Adapter<KategoriAdapter.KategoriViewHolder>{

    Context context;
    ArrayList<KategoriModel> kategoriModels;
    public KategoriAdapter(Context context, ArrayList<KategoriModel> kategoriModels){
        this.context = context;
        this.kategoriModels = kategoriModels;
    }

    @NonNull
    @Override
    public KategoriViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.kategori_item, null);
        return new KategoriViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KategoriViewHolder holder, int position) {
        KategoriModel model = kategoriModels.get(position);
        holder.textView.setText(model.getKategoriNama());
        Glide.with(context).load(model.getKategoriImage()).into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, KuisActivity.class);
                i.putExtra("kategoriID", model.getKategoriID());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return kategoriModels.size();
    }

    public class KategoriViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView textView;

        public KategoriViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            textView = itemView.findViewById(R.id.kategori);
        }
    }

}
