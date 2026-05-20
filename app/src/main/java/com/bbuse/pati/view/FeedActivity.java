package com.bbuse.pati.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bbuse.pati.R;
import com.bbuse.pati.adapter.PostAdapter;
import com.bbuse.pati.adapter.UserPostAdapter;
import com.bbuse.pati.databinding.ActivityFeedBinding;
import com.bbuse.pati.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private ActivityFeedBinding binding;
    ArrayList<Post> postArrayList;
    PostAdapter postAdapter;
    UserPostAdapter userPostAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        postArrayList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        getData(null, null, new ArrayList<>());

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter(postArrayList);
        binding.recyclerView.setAdapter(postAdapter);

        binding.filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilterDialog();
            }
        });
    }
    private void getData(String selectedAdoptionType, String selectedGender, ArrayList<String> selectedAnimalTypes) {
        postArrayList.clear();
        Query query = firebaseFirestore.collection("Posts").orderBy("date", Query.Direction.DESCENDING);

        boolean[] isFilterApplied = {false};

        if (selectedAdoptionType != null) {
            query = query.whereEqualTo("adoptionType", selectedAdoptionType);
            isFilterApplied[0] = true;
        }

        if (selectedGender != null) {
            query = query.whereEqualTo("gender", selectedGender);
            isFilterApplied[0] = true;
        }

        if (!selectedAnimalTypes.isEmpty()) {
            query = query.whereIn("animalType", selectedAnimalTypes);
            isFilterApplied[0] = true;
        }

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }

                if (value != null) {
                    postArrayList.clear();
                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                        Post post = createPostFromSnapshot(snapshot);
                        if (post != null) {
                            postArrayList.add(post);
                        }
                    }
                    if (postArrayList.isEmpty()) {
                        postAdapter.notifyDataSetChanged();
                    }

                    if (postArrayList.isEmpty() && isFilterApplied[0]) {
                        Toast.makeText(FeedActivity.this, "Seçilen filtrelere uygun ilan bulunamadı.", Toast.LENGTH_SHORT).show();
                    } else {
                        postAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }


    private Post createPostFromSnapshot(DocumentSnapshot snapshot) {
        Map<String, Object> data = snapshot.getData();
        if (data != null) {
            String userEmail = (String) data.get("useremail");
            String comment = (String) data.get("comment");
            String imageName = (String) data.get("imageName");
            String adoptionType = (String) data.get("adoptionType");
            String animalType = (String) data.get("animalType");
            String gender = (String) data.get("gender");

            return new Post(userEmail, comment, imageName, adoptionType, animalType, gender);
        }
        return null;
    }
    private void openFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filtrele");

        View dialogView = getLayoutInflater().inflate(R.layout.filter_dialog, null);
        builder.setView(dialogView);

        CheckBox kaliciCheckBox = dialogView.findViewById(R.id.kaliciCheckBox);
        CheckBox geciciCheckBox = dialogView.findViewById(R.id.geciciCheckBox);
        CheckBox erkekCheckBox = dialogView.findViewById(R.id.erkekCheckBox);
        CheckBox disiCheckBox = dialogView.findViewById(R.id.disiCheckBox);
        CheckBox kediCheckBox = dialogView.findViewById(R.id.kediCheckBox);
        CheckBox kopekCheckBox = dialogView.findViewById(R.id.kopekCheckBox);
        CheckBox kusCheckBox = dialogView.findViewById(R.id.kusCheckBox);
        Button resetFilterButton = dialogView.findViewById(R.id.resetFilterButton);

        builder.setPositiveButton("Filtrele", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedAdoptionType = null;
                if (kaliciCheckBox.isChecked()) {
                    selectedAdoptionType = "Kalici";
                } else if (geciciCheckBox.isChecked()) {
                    selectedAdoptionType = "Gecici";
                }

                String selectedGender = null;
                if (erkekCheckBox.isChecked()) {
                    selectedGender = "Erkek";
                } else if (disiCheckBox.isChecked()) {
                    selectedGender = "Disi";
                }

                ArrayList<String> selectedAnimalTypes = new ArrayList<>();
                if (kediCheckBox.isChecked()) selectedAnimalTypes.add("Kedi");
                if (kopekCheckBox.isChecked()) selectedAnimalTypes.add("Kopek");
                if (kusCheckBox.isChecked()) selectedAnimalTypes.add("Kus");

                if (selectedAdoptionType == null && selectedGender == null && selectedAnimalTypes.isEmpty()) {
                    getData(null, null, new ArrayList<>());
                } else {
                    getData(selectedAdoptionType, selectedGender, selectedAnimalTypes);
                }

            }
        });

        resetFilterButton.setOnClickListener(v -> {
            kaliciCheckBox.setChecked(false);
            geciciCheckBox.setChecked(false);
            erkekCheckBox.setChecked(false);
            disiCheckBox.setChecked(false);
            kediCheckBox.setChecked(false);
            kopekCheckBox.setChecked(false);
            kusCheckBox.setChecked(false);

            getData(null, null, new ArrayList<>());
        });

        builder.setNegativeButton("İptal", null);
        builder.show();
    }

    private void getUserPosts() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            Query query = firebaseFirestore.collection("Posts")
                    .whereEqualTo("userid", userId)  // UID ile sorgulama
                    .orderBy("date", Query.Direction.DESCENDING);

            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.e("FirebaseError", "Veri alınırken bir hata oluştu: ", error);
                        Toast.makeText(FeedActivity.this, "Veri alınırken bir hata oluştu.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        postArrayList.clear();

                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            Post post = createPostFromSnapshot(snapshot);
                            if (post != null) {
                                postArrayList.add(post);
                            }
                        }

                        if (postArrayList.isEmpty()) {
                            Toast.makeText(FeedActivity.this, "Henüz bir ilanınız yok.", Toast.LENGTH_SHORT).show();
                        }

                        binding.recyclerView.setAdapter(userPostAdapter);
                        userPostAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_post) {
            Intent intentToUpload = new Intent(FeedActivity.this, UploadActivity.class);
            startActivity(intentToUpload);
            return true;

        } else if (item.getItemId() == R.id.signout) {
            auth.signOut();
            Intent intentToMain = new Intent(FeedActivity.this, MainActivity.class);
            startActivity(intentToMain);
            finish();
            return true;

        } else if (item.getItemId() == R.id.action_my_posts) {
            Intent intent = new Intent(this, MyPostsActivity.class);
            startActivity(intent);
            return true;

        } else {
            return super.onOptionsItemSelected(item);
        }
    }



}



