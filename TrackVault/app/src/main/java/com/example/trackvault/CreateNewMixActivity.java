package com.example.trackvault;
/*
 * Author: Krithika Kasaragod
 * FileName: CreateNewMixActivity.java
 */
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trackvault.Model.Mix;
import com.example.trackvault.databinding.ActivityCreateNewMixBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class CreateNewMixActivity extends AppCompatActivity {


    ActivityCreateNewMixBinding binding;
    public ArrayList<String> invitedUsers;
    int number_tracks = 0;
    FirebaseAuth mAuth;

    static String TAG = "Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateNewMixBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        Log.d(TAG, "onCreate: mAuth" + mAuth.getCurrentUser());

        binding.buttonSubmit.setOnClickListener(view -> createNewMix());

        binding.buttonCancel.setOnClickListener(view -> finish());
    }

    private void createNewMix() {
        String mixName = binding.etMixName.getText().toString();
        if (mixName.isEmpty()) {
            displayAlert(getString(R.string.label_error_forum_description));
        } else {

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Log.d(TAG, "createNewMix: db" + db);


            invitedUsers = new ArrayList<>();
            DocumentReference newDoc = db.collection("MixList").document();

            Log.d("TAG", "UserList createNewList: " + invitedUsers);
            String DID = newDoc.getId();

            newDoc.set(new Mix(DID, Objects.requireNonNull(mAuth.getCurrentUser()).getUid(), mixName, mAuth.getCurrentUser().getDisplayName(), number_tracks, invitedUsers))
                    .addOnSuccessListener(unused -> {

                    }).addOnFailureListener(e -> Log.d("TAG", "onFailure: CreateList"));

            CreateNewMixActivity.this.finish();
        }
    }

    public void displayAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.label_alert))
                .setMessage(message)
                .setPositiveButton(getString(R.string.label_ok), (dialogInterface, i) -> dialogInterface.dismiss());
        builder.show();
    }
}