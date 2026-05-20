package com.bbuse.pati.view;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bbuse.pati.R;
import com.bbuse.pati.adapter.UserPostAdapter;
import com.bbuse.pati.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;

public class MyPostsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMyPosts;
    private UserPostAdapter userPostAdapter;
    private ArrayList<Post> postArrayList;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);

        recyclerViewMyPosts = findViewById(R.id.recyclerViewMyPosts);
        recyclerViewMyPosts.setLayoutManager(new LinearLayoutManager(this));

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        postArrayList = new ArrayList<>();
        userPostAdapter = new UserPostAdapter(postArrayList);
        recyclerViewMyPosts.setAdapter(userPostAdapter);

        getMyPosts();
    }

    private void getMyPosts() {

        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this,
                    "Oturum açmış bir kullanıcı bulunamadı!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();

        firestore.collection("Posts")
                .whereEqualTo("userid", userId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    postArrayList.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                        Post post = new Post();

                        post.useremail = document.getString("useremail");
                        post.comment = document.getString("comment");
                        post.adoptionType = document.getString("adoptionType");
                        post.animalType = document.getString("animalType");
                        post.gender = document.getString("gender");
                        post.imageName = document.getString("imageName");

                        post.postId = document.getId();

                        postArrayList.add(post);
                    }

                    userPostAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MyPostsActivity.this,
                            "İlanları çekerken hata oluştu!",
                            Toast.LENGTH_SHORT).show();
                });
    }
}



