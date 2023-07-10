package com.example.trackvault.Fragments;
/*
 * Author: Krithika Kasaragod
 * FileName: MixesFragment.java
 */
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackvault.Model.Mix;
import com.example.trackvault.R;
import com.example.trackvault.databinding.CustomMixListBinding;
import com.example.trackvault.databinding.FragmentMixesBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;


public class MixesFragment extends Fragment {


    FragmentMixesBinding binding;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    LinearLayoutManager linearLayoutManager;
    ArrayList<Mix> mixListArray = new ArrayList<>();
    MixAdapter adapter;

    public MixesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMixesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        getMixListData();

        binding.recyclerMix.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext());
        binding.recyclerMix.setLayoutManager(linearLayoutManager);
        adapter = new MixAdapter(mixListArray, mListener);
        binding.recyclerMix.getRecycledViewPool().setMaxRecycledViews(0, 0);
        binding.recyclerMix.setAdapter(adapter);

        binding.btnNewMix.setOnClickListener(view1 -> mListener.gotoCreateNewMixActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        getMixListData();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getMixListData() {

        db.collection("MixList")
                .addSnapshotListener((value, error) -> {
                    mixListArray.clear();
                    assert value != null;
                    for (QueryDocumentSnapshot document : value) {
                        Mix mixList = document.toObject(Mix.class);
                        if (mixList.getUid().equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                                || mixList.getInvitedUsers().contains(mAuth.getCurrentUser().getDisplayName())) {
                            mixListArray.add(mixList);
                        }
                    }

                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            if (context instanceof IMixesService) {
                mListener = (IMixesService) context;
            } else {
                throw new RuntimeException();
            }
        } catch (RuntimeException exception) {
            exception.printStackTrace();
        }
    }


    class MixAdapter extends RecyclerView.Adapter<MixAdapter.RecyclerMixiewHolder> {
        ArrayList<Mix> listAlbum;
        IMixesService mListener;

        public MixAdapter(ArrayList<Mix> sortNameList, IMixesService mListener) {
            this.listAlbum = sortNameList;
            this.mListener = mListener;
        }

        @NonNull
        @Override
        public RecyclerMixiewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            CustomMixListBinding binding = CustomMixListBinding.inflate(getLayoutInflater(), parent, false);
            return new RecyclerMixiewHolder(binding);

        }

        @Override
        public int getItemCount() {
            return listAlbum.size();
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerMixiewHolder holder, @SuppressLint("RecyclerView") int position) {

            Mix item = listAlbum.get(position);
            holder.setUpData(item);
        }

        public class RecyclerMixiewHolder extends RecyclerView.ViewHolder {

            CustomMixListBinding mBinding;
            Mix mItem;

            public RecyclerMixiewHolder(CustomMixListBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }

            @SuppressLint("SetTextI18n")
            public void setUpData(Mix item) {
                mItem = item;
                mBinding.textViewSLName.setText(mItem.getMixName());
                if (mItem.getNumber_tracks() <= 0) {
                    mBinding.textViewSLDateTime.setText(getString(R.string.label_zero_tracks));
                } else {
                    mBinding.textViewSLDateTime.setText(mItem.getNumber_tracks() + " " + getString(R.string.label_tracks));
                }
                if (item.getUid().equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())) {
                    mBinding.textViewOwnerName.setText(getString(R.string.label_created_by_me));
                } else {
                    mBinding.textViewOwnerName.setText(getString(R.string.label_shared_by_me) + " " + mItem.getCreatedBy());
                }

                mBinding.imageViewTrash.setImageResource(R.drawable.delete);


                mBinding.imageViewTrash.setOnClickListener(view -> deleteMixList(item));

                mBinding.getRoot().setOnClickListener(view -> {
                    Log.d("TAG", "onClick: " + mItem);
                    mListener.gotoMixActivity(mItem);
                });
            }

        }
    }

    private void deleteMixList(Mix item) {


        db.collection("MixList").document(item.getDid()).collection("TrackList")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        Log.d("TAG", "onEvent: " + value.size());

                        for (QueryDocumentSnapshot document : value) {
                            Log.d("TAG", "onEvent: commentID " + document.getId());
                            deleteDoc(document.getId(), item.getDid());
                        }
                    }
                });

        db.collection("MixList")
                .document(item.getDid())
                .delete()
                .addOnSuccessListener(getActivity(), unused -> {

                });

    }

    private void deleteDoc(String id, String name) {
        db.collection("MixList").document(name).collection("TrackList")
                .document(id)
                .delete()
                .addOnSuccessListener(requireActivity(), unused -> {

                });
    }

    IMixesService mListener;

    public interface IMixesService {
        void gotoCreateNewMixActivity();

        void gotoMixActivity(Mix mItem);
    }

}