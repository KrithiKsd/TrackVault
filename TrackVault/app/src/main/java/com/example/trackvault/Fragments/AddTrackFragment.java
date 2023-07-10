package com.example.trackvault.Fragments;
/*
 * Author: Krithika Kasaragod
 * FileName: AddTrackFragment.java
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

import com.example.trackvault.Model.Album;
import com.example.trackvault.Model.AlbumTracks;
import com.example.trackvault.Model.Mix;
import com.example.trackvault.Model.Track;
import com.example.trackvault.databinding.FragmentAddTrackBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;


public class AddTrackFragment extends Fragment {


    final String TAG = "demo";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private AlbumTracks mAlbumTrack;
    private Album mAlbum;
    ArrayList<String> mixType = new ArrayList<>();
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    ArrayAdapter<String> adapter;
    FragmentAddTrackBinding binding;
    ArrayList<Mix> mixAllList = new ArrayList<>();

    public AddTrackFragment() {
        // Required empty public constructor
    }

    public static AddTrackFragment newInstance(AlbumTracks param1, Album param2) {
        AddTrackFragment fragment = new AddTrackFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        args.putSerializable(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAlbumTrack = (AlbumTracks) getArguments().getSerializable(ARG_PARAM1);
            mAlbum = (Album) getArguments().getSerializable(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddTrackBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (mAlbum != null && mAlbumTrack != null) {
            binding.tvTrackTitle2.setText(mAlbumTrack.getTitle());

            getMixData();


        }
    }

    private void getMixData() {

        db.collection("MixList")
                .addSnapshotListener((value, error) -> {
                    mixType.clear();
                    mixAllList.clear();
                    assert value != null;
                    for (QueryDocumentSnapshot document : value) {
                        Mix mixList = document.toObject(Mix.class);

                        if (mixList.getUid().equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())) {
                            mixType.add(document.getString("mixName"));
                            mixAllList.add(mixList);
                        }

                    }

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            if (mixType != null) {
                                Log.d("TAG", "run: " + mixType);
                                adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, mixType);
                                binding.listMix.setAdapter(adapter);


                                Log.d(TAG, "MixList ALl: " + mixAllList);

                                binding.listMix.setOnItemClickListener((adapterView, view, position, l) -> {
                                    addSubCollection(adapter.getItem(position));
                                    Log.d("TAG", "onItemClick: adapter" + adapter.getItem(position));

                                });
                            }

                        });
                    }

                });
    }

    private void addSubCollection(String item) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String docID = "";
        for (Mix mix : mixAllList) {
            if (mix.getMixName().equals(item)) {
                docID = mix.getDid();
            }
        }


        DocumentReference newDoc = db.collection("MixList").document(docID)
                .collection("TrackList").document();
        String trackDocID = newDoc.getId();

        String finalDocID = docID;
        newDoc.set(new Track(trackDocID, docID, Objects.requireNonNull(mAuth.getCurrentUser()).getUid(), item, mAlbumTrack.getTitle(), mAlbumTrack.getDuration(), mAlbumTrack.getPreview(), mAlbum.getAlbumImage()))
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "onSuccess: ****");
                    callUpdateCounts(finalDocID);

                }).addOnFailureListener(e -> {

                });


    }

    private void callUpdateCounts(String finalDocID) {

        db.collection("MixList").document(finalDocID)
                .update("number_tracks", FieldValue.increment(1))
                .addOnSuccessListener(unused -> Log.d("TAG", "onSuccess: on updateCount ")).addOnFailureListener(e -> Log.d("TAG", "onFailure: Inside update " + e.getMessage()));
        mListener.gotoAlbumFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            if (context instanceof IAddFragmentService) {
                mListener = (IAddFragmentService) context;
            } else {
                throw new RuntimeException();
            }
        } catch (RuntimeException exception) {
            exception.printStackTrace();
        }
    }

    IAddFragmentService mListener;

    public interface IAddFragmentService {
        void gotoAlbumFragment();
    }
}