package com.bbuse.pati.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bbuse.pati.databinding.RecyclerUserBinding;
import com.bbuse.pati.model.Post;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class UserPostAdapter extends RecyclerView.Adapter<UserPostAdapter.PostHolder> {

    private ArrayList<Post> postArrayList;
    private FirebaseFirestore firestore;

    public UserPostAdapter(ArrayList<Post> postArrayList) {
        this.postArrayList = postArrayList;
        firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerUserBinding binding = RecyclerUserBinding.inflate(
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

        // 🔥 DRAWABLE RESİM
        int resId = holder.binding.getRoot().getContext()
                .getResources()
                .getIdentifier(
                        currentPost.imageName,
                        "drawable",
                        holder.binding.getRoot().getContext().getPackageName()
                );

        holder.binding.recyclerViewImageView.setImageResource(resId);

        // 🗑 DELETE
        holder.binding.deleteButton.setOnClickListener(v -> {

            String postId = currentPost.postId;

            firestore.collection("Posts")
                    .document(postId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {

                        Toast.makeText(v.getContext(),
                                "İlan silindi!",
                                Toast.LENGTH_SHORT).show();

                        postArrayList.remove(position);
                        notifyItemRemoved(position);

                    })
                    .addOnFailureListener(e -> {

                        Toast.makeText(v.getContext(),
                                "Silme başarısız!",
                                Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }

    class PostHolder extends RecyclerView.ViewHolder {

        RecyclerUserBinding binding;

        public PostHolder(RecyclerUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}