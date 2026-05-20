package com.bbuse.pati.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bbuse.pati.databinding.RecyclerRowBinding;
import com.bbuse.pati.model.Post;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {

    private ArrayList<Post> postArrayList;

    public PostAdapter(ArrayList<Post> postArrayList) {
        this.postArrayList = postArrayList;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding binding = RecyclerRowBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new PostHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {

        Post currentPost = postArrayList.get(position);

        holder.binding.recyclerViewUserEmailText.setText(currentPost.useremail);
        holder.binding.recyclerViewCommentText.setText(currentPost.comment);
        holder.binding.recyclerViewAdoptionTypeText.setText(currentPost.adoptionType);
        holder.binding.recyclerViewAnimalTypeText.setText(currentPost.animalType);
        holder.binding.recyclerViewGenderText.setText(currentPost.gender);

        Context context = holder.binding.getRoot().getContext();

        int resId = context.getResources().getIdentifier(
                currentPost.imageName,
                "drawable",
                context.getPackageName()
        );

        holder.binding.recyclerViewImageView.setImageResource(resId);

        holder.binding.btnIletisimeGec.setOnClickListener(v -> {
            getPhoneNumberFromFirestore(currentPost.useremail, v.getContext());
        });
    }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }

    class PostHolder extends RecyclerView.ViewHolder {

        RecyclerRowBinding binding;

        public PostHolder(RecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private void getPhoneNumberFromFirestore(String email, Context context) {

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("Users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if (!queryDocumentSnapshots.isEmpty()) {

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                            String phoneNumber = document.getString("phoneNumber");

                            if (phoneNumber != null && !phoneNumber.isEmpty()) {
                                sendSMS(phoneNumber, context);
                            } else {
                                Toast.makeText(context,
                                        "Telefon numarası bulunamadı!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                    } else {
                        Toast.makeText(context,
                                "Kullanıcı bulunamadı!",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context,
                            "Hata: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void sendSMS(String phoneNumber, Context context) {

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:" + phoneNumber));
        intent.putExtra("sms_body",
                "Merhaba, ilanınızı gördüm ve bilgi almak istiyorum.");

        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context,
                    "SMS uygulaması açılamadı!",
                    Toast.LENGTH_SHORT).show();
        }
    }
}