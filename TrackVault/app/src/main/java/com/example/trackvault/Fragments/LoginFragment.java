package com.example.trackvault.Fragments;
/*
 * Author: Krithika Kasaragod
 * FileName: LoginFragment.java
 */
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.trackvault.IService;
import com.example.trackvault.R;
import com.example.trackvault.databinding.FragmentLoginBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoginFragment extends Fragment {


    FragmentLoginBinding loginBinding;
    String missingField = "";
    private FirebaseAuth mAuth;

    IService mListener;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        loginBinding = FragmentLoginBinding.inflate(inflater, container, false);
        return loginBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        loginBinding.buttonLogin.setOnClickListener(view1 -> {

            String emailName = loginBinding.editTextEmail.getText().toString();
            String password = loginBinding.editTextPswd.getText().toString();
            boolean success = validation(emailName, password);
            if (success) {
                mAuth = FirebaseAuth.getInstance();
                mAuth.signInWithEmailAndPassword(emailName, password)
                        .addOnCompleteListener(requireActivity(), task -> {
                            if (task.isSuccessful()) {

                                mListener.gotoForumsFragment();

                            } else {
                                displayAlert(Objects.requireNonNull(task.getException()).getMessage());
                            }
                        });

            } else {
                displayAlert(missingField);
            }

        });


        loginBinding.buttonCreateAccount.setOnClickListener(view12 -> mListener.gotoRegisterFragment());
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

    public void displayAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(getString(R.string.label_alert))
                .setMessage(message)
                .setPositiveButton(getString(R.string.label_ok), (dialogInterface, i) -> dialogInterface.dismiss());
        builder.show();
    }

    private boolean validation(String emailName, String password) {
        if (emailName.isEmpty() && password.isEmpty()) {
            missingField = getString(R.string.label_email_password_field);
            return false;
        } else if (emailName.isEmpty()) {
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