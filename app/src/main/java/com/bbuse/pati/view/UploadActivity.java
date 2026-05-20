package com.bbuse.pati.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bbuse.pati.databinding.ActivityUploadBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class UploadActivity extends AppCompatActivity {

    private ActivityUploadBinding binding;

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;

    String selectedImageName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUploadBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public void selectImage(View view) {

        final String[] imageNames = {

                "kedi",
                "kedi1",
                "kedi2",
                "kedi3",
                "kedi4",
                "kedi5",
                "kedi6",
                "kedi7",
                "kedi8",
                "kedi9",
                "kopek1",
                "kopek2",
                "kopek3",
                "kopek4",
                "kopek5",
                "kus1",
                "kus2",
                "kus3"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Fotoğraf Seç");

        builder.setItems(imageNames, (dialog, which) -> {

            selectedImageName = imageNames[which];

            int resId = getResources().getIdentifier(
                    selectedImageName,
                    "drawable",
                    getPackageName()
            );

            binding.imageView2.setImageResource(resId);

            Toast.makeText(this,
                    "Fotoğraf seçildi",
                    Toast.LENGTH_SHORT).show();

        });

        builder.show();
    }

    public void uploadButtonClicked(View view) {

        if (selectedImageName.isEmpty()) {
            Toast.makeText(this,
                    "Lütfen bir fotoğraf seçin!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String adoptionType = binding.adoptionTypeSpinner.getSelectedItem().toString();

        String animalType = binding.animalTypeSpinner.getSelectedItem().toString();

        String gender = binding.genderSpinner.getSelectedItem().toString();

        String comment = binding.commentText.getText().toString();

        savePost(
                selectedImageName,
                comment,
                adoptionType,
                animalType,
                gender
        );
    }

    private void savePost(String imageName,
                          String comment,
                          String adoptionType,
                          String animalType,
                          String gender) {

        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {

            String userId = user.getUid();

            String email = user.getEmail();

            HashMap<String, Object> postData = new HashMap<>();

            postData.put("useremail", email);

            postData.put("imageName", imageName);

            postData.put("comment", comment);

            postData.put("adoptionType", adoptionType);

            postData.put("animalType", animalType);

            postData.put("gender", gender);

            postData.put("userid", userId);

            postData.put("date", FieldValue.serverTimestamp());

            firebaseFirestore.collection("Posts")
                    .add(postData)
                    .addOnSuccessListener(documentReference -> {

                        Toast.makeText(UploadActivity.this,
                                "İlan başarıyla kaydedildi!",
                                Toast.LENGTH_SHORT).show();

                        Intent intent =
                                new Intent(UploadActivity.this,
                                        FeedActivity.class);

                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(intent);

                    })
                    .addOnFailureListener(e -> {

                        Toast.makeText(UploadActivity.this,
                                "Hata oluştu!",
                                Toast.LENGTH_SHORT).show();

                        e.printStackTrace();

                    });

        } else {

            Toast.makeText(this,
                    "Oturum açmanız gerekiyor.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}