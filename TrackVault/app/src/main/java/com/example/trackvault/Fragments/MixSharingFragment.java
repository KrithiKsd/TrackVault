package com.example.trackvault.Fragments;
/*
 * Author: Krithika Kasaragod
 * FileName: MixSharingFragment.java
 */
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.trackvault.Model.ListUser;
import com.example.trackvault.Model.Mix;
import com.example.trackvault.databinding.FragmentMixSharingBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;


public class MixSharingFragment extends Fragment {

    FragmentMixSharingBinding binding;
    private static final String ARG_PARAM1 = "param1";
    private Mix mParam1;
    ArrayList<ListUser> userList = new ArrayList<>();
    ArrayList<String>invitedUsers = new ArrayList<>();
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    ArrayAdapter adapter;


    public MixSharingFragment() {
        // Required empty public constructor
    }

    public static MixSharingFragment newInstance(Mix param1) {
        MixSharingFragment fragment = new MixSharingFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = (Mix) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMixSharingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        if(mParam1!=null){
            binding.tvType.setText(mParam1.getMixName());
        }
        getInvitedList();
        getUserList();
    }

    private void getUserList() {
        db.collection("ListUser")
                .addSnapshotListener((value, error) -> {
                    userList.clear();
                    assert value != null;
                    for (QueryDocumentSnapshot document : value) {
                        ListUser listUser = document.toObject(ListUser.class);

                        if (!listUser.getName().equals(Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName())){
                            userList.add(listUser);
                        }

                    }

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            if (userList != null) {

                                    adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, userList);
                                    binding.listUsers.setAdapter(adapter);

                                    binding.listUsers.setOnItemClickListener((adapterView, view, position, l) -> addUser((ListUser) adapter.getItem(position)));


                            }

                        });
                    }

                });
    }

    public void getInvitedList() {

        db.collection("MixList").document(mParam1.getDid())
                .get().addOnSuccessListener(documentSnapshot -> {
                    invitedUsers.clear();
                    invitedUsers = (ArrayList<String>) documentSnapshot.get("invitedUsers");
                });
    }
    private void addUser(ListUser item) {

        Log.d("TAG", "addUser: invitedUsers"+invitedUsers);
        ArrayList<String> newInvited = invitedUsers;
        if (newInvited != null) {
            if (newInvited.contains(item.getName())) {
                newInvited.remove(item.getName());
            } else if (!newInvited.contains(item.getName())) {
                newInvited.add(item.getName());
            }
        } else {
            assert false;
            newInvited.add(item.getName());
        }

        DocumentReference docRef = db.collection("MixList").document(mParam1.getDid());
        docRef.update("invitedUsers", newInvited)
                .addOnSuccessListener(unused -> Log.d("TAG", "onSuccess: invitedUser updated")).addOnFailureListener(e -> Log.d("TAG", "onSuccess: invitedUser updated NOT"));
        mListener.gotoMixDetails();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            if (context instanceof iMixSharing) {
                mListener = (iMixSharing) context;
            } else {
                throw new RuntimeException();
            }
        } catch (RuntimeException exception) {
            exception.printStackTrace();
        }
    }
    iMixSharing mListener;

    public interface iMixSharing{
        void gotoMixDetails();
    }
}