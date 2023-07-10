package com.example.trackvault.Fragments;
/*
 * Author: Krithika Kasaragod
 * FileName: SignUpFragment.java
 */
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.trackvault.IService;
import com.example.trackvault.Model.ListUser;
import com.example.trackvault.R;
import com.example.trackvault.databinding.FragmentSignUpBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignUpFragment extends Fragment {

    FragmentSignUpBinding registerBinding;
    String missingField;
    private FirebaseAuth mAuth;
    IService mListener;
    String fullName;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        registerBinding = FragmentSignUpBinding.inflate(inflater, container, false);
        return registerBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        registerBinding.buttonSubmit.setOnClickListener(view1 -> {
            fullName = registerBinding.editTextName.getText().toString();
            String emailName = registerBinding.editTextEmailAddress.getText().toString();
            String password = registerBinding.editTextPassword.getText().toString();
            boolean success = validation(fullName, emailName, password);
            if (success) {
                mAuth = FirebaseAuth.getInstance();


                mAuth.createUserWithEmailAndPassword(emailName, password)
                        .addOnCompleteListener(getActivity(), task -> {
                            if (task.isSuccessful()) {

                                FirebaseUser user = mAuth.getCurrentUser();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(fullName)
                                        .build();

                                assert user != null;
                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(Task::isSuccessful);
                                setUser(Objects.requireNonNull(mAuth.getCurrentUser()).getUid(), fullName, emailName);
                                mListener.gotoForumsFragment();

                            } else {
                                displayAlert(Objects.requireNonNull(task.getException()).getMessage());
                            }
                        });


            } else {
                displayAlert(missingField);
            }
        });


        registerBinding.buttonCancel.setOnClickListener(view12 -> mListener.gotoPreviousFragment());
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            if (context instanceof IService) {
                mListener = (IService) context;
            } else {
                throw new RuntimeException();
            }
        } catch (RuntimeException exception) {
            exception.printStackTrace();
        }
    }
    private void setUser(String uid, String fullName, String emailName) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDoc = db.collection("ListUser").document();
        String DID = userDoc.getId();
        userDoc.set(new ListUser(DID, uid, fullName, emailName))
                .addOnSuccessListener(unused -> Log.d("TAG", "onSuccess: user collection")).addOnFailureListener(e -> Log.d("TAG", "onFailure: user collection"));


    }

    public void displayAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.label_alert))
                .setMessage(message)
                .setPositiveButton(getString(R.string.label_ok), (dialogInterface, i) -> dialogInterface.dismiss());
        builder.show();
    }

    private boolean validation(String fullName, String emailName, String password) {
        if (fullName.isEmpty() && emailName.isEmpty() && password.isEmpty()) {
            missingField = getString(R.string.label_full_name_email_password_field);
            return false;
        } else if (fullName.isEmpty()) {
            missingField = getString(R.string.label_full_name_field);
            return false;
        } else if (emailName == null || emailName.isEmpty()) {
            missingField = getString(R.string.label_email_field);
            return false;
        } else if (!validEmailPattern(emailName)) {
            missingField = getString(R.string.label_proper_email);
            return false;
        } else if (password == null || password.isEmpty()) {
            missingField = getString(R.string.label_password_field);
            return false;
        }
        return true;
    }

    private boolean validEmailPattern(String email) {
        Pattern emailPattern = Pattern.compile(".+@.+\\.[a-z]+");
        Matcher emailMatcher = emailPattern.matcher(email);
        return emailMatcher.matches();
    }
}