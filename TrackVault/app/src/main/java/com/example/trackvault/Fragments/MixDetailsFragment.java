package com.example.trackvault.Fragments;
/*
 * Author: Krithika Kasaragod
 * FileName: MixDetailsFragment.java
 */
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackvault.Model.Mix;
import com.example.trackvault.Model.Track;
import com.example.trackvault.R;
import com.example.trackvault.databinding.CustomTrackDetailListBinding;
import com.example.trackvault.databinding.FragmentMixDetailsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;


public class MixDetailsFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private Mix mParam1;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    ArrayList<Track> listTrack = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    TrackAdapter adapter;

    FragmentMixDetailsBinding binding;

    public MixDetailsFragment() {
        // Required empty public constructor
    }

    public static MixDetailsFragment newInstance(Mix param1) {
        MixDetailsFragment fragment = new MixDetailsFragment();
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
        binding = FragmentMixDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        if (mParam1 != null) {
            binding.tvMixTypeName.setText(mParam1.getMixName());
            getTrackList();

            binding.recyclertracks.setHasFixedSize(true);
            linearLayoutManager = new LinearLayoutManager(getContext());
            binding.recyclertracks.setLayoutManager(linearLayoutManager);
            adapter = new TrackAdapter(listTrack, mListener);
            binding.recyclertracks.getRecycledViewPool().setMaxRecycledViews(0, 0);
            binding.recyclertracks.setAdapter(adapter);


        }
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    private void getTrackList() {
        db.collection("MixList").document(mParam1.getDid()).collection("TrackList")
                .addSnapshotListener((value, error) -> {
                    listTrack.clear();
                    assert value != null;
                    for (QueryDocumentSnapshot document : value) {

                        Track item = document.toObject(Track.class);

                        if (item.getuID().equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()) || mParam1.getInvitedUsers().contains(mAuth.getCurrentUser().getDisplayName())) {
                            listTrack.add(item);
                        }

                    }

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            binding.tvTrackListNumber.setText("TrackList(" + listTrack.size() + ")");
                            if (listTrack.size() > 0) {
                                Log.d("TAG", "run: size " + listTrack.size());

                                binding.ivShare.setOnClickListener(view -> mListener.gotoMixSharingFragment(mParam1));
                            } else {
                                Toast.makeText(getActivity(), "Sorry no tracks", Toast.LENGTH_SHORT).show();
                            }

                        });
                    }
                    adapter.notifyDataSetChanged();

                });
    }


    class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.RecyclerMixiewHolder> {
        ArrayList<Track> listAlbum;
        IMixDetailService mListener;

        public TrackAdapter(ArrayList<Track> sortNameList, IMixDetailService mListener) {
            this.listAlbum = sortNameList;
            this.mListener = mListener;
        }

        @NonNull
        @Override
        public RecyclerMixiewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            CustomTrackDetailListBinding binding = CustomTrackDetailListBinding.inflate(getLayoutInflater(), parent, false);
            return new RecyclerMixiewHolder(binding);

        }

        @Override
        public int getItemCount() {
            return listAlbum.size();
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerMixiewHolder holder, @SuppressLint("RecyclerView") int position) {

            Track item = listAlbum.get(position);
            holder.setUpData(item);
        }

        public class RecyclerMixiewHolder extends RecyclerView.ViewHolder {

            CustomTrackDetailListBinding mBinding;
            Track mItem;

            public RecyclerMixiewHolder(CustomTrackDetailListBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }

            public void setUpData(Track item) {
                mItem = item;
                mBinding.tvListTitle.setText(mItem.getTrack());
                mBinding.tvListDuration.setText(String.valueOf(DateUtils.formatElapsedTime((long) Double.parseDouble(mItem.getDuration()))));
                mBinding.ivPlus.setImageResource(R.drawable.delete);


                mBinding.ivPlus.setOnClickListener(view -> deleteMixList(item));

                mBinding.getRoot().setOnClickListener(view -> mListener.gotoAudioPlayer(mItem));
            }

        }
    }

    public void deleteMixList(Track item) {


        db.collection("MixList").document(item.getDocID()).collection("TrackList")
                .document(item.getTrackId())
                .delete()
                .addOnSuccessListener(requireActivity(), unused -> Log.d("TAG", "onSuccess: deleted track and mix collection"));

        db.collection("MixList").document(item.getDocID())
                .update("number_tracks", FieldValue.increment(-1))
                .addOnSuccessListener(unused -> {
                    Log.d("TAG", "onSuccess: ");
                    getTrackList();

                }).addOnFailureListener(e -> Log.d("TAG", "onFailure: Inside update " + e.getMessage()));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            if (context instanceof IMixDetailService) {
                mListener = (IMixDetailService) context;
            } else {
                throw new RuntimeException();
            }
        } catch (RuntimeException exception) {
            exception.printStackTrace();
        }
    }

    IMixDetailService mListener;

    public interface IMixDetailService {
        void gotoAudioPlayer(Track item);

        void gotoMixSharingFragment(Mix item);
    }
}