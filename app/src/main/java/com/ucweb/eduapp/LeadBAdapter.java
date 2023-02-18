 package com.ucweb.eduapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ucweb.eduapp.databinding.RowLeadboardBinding;

import java.util.ArrayList;

public class LeadBAdapter extends RecyclerView.Adapter<LeadBAdapter.LeadBViewHolder>{

    Context context;
    ArrayList<User> users;
    public LeadBAdapter(Context context, ArrayList<User> users){
        this.context = context;
        this.users = users;
    }
    @NonNull
    @Override
    public LeadBViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_leadboard, parent, false);

        return new LeadBViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeadBViewHolder holder, int position) {
        User user = users.get(position);
        holder.binding.tvnama.setText(user.getNama());
        holder.binding.coins.setText(String.valueOf(user.getCoins()));
        holder.binding.index.setText(String.format("#%d", position+1));

        Glide.with(context).load(user.getProfil()).into(holder.binding.imageView7);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class LeadBViewHolder extends RecyclerView.ViewHolder{

        RowLeadboardBinding binding;
        public LeadBViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RowLeadboardBinding.bind(itemView);
        }
    }
}
