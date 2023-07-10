package com.example.trackvault.Fragments;
/*
 * Author: Krithika Kasaragod
 * FileName: AlbumFragment.java
 */
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackvault.Api;
import com.example.trackvault.Model.Album;
import com.example.trackvault.Model.AlbumTrackData;
import com.example.trackvault.Model.AlbumTracks;
import com.example.trackvault.RetrofitClient;
import com.example.trackvault.databinding.CustomTracklistBinding;
import com.example.trackvault.databinding.FragmentAlbumBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class AlbumFragment extends Fragment {


    private static final String ARG_PARAM1 = "ALBUM";
    private Album mAlbum;
    FragmentAlbumBinding binding;
    ArrayList<AlbumTracks> albumList = new ArrayList<>();
    AlbumAdapter adapter;
    LinearLayoutManager linearLayoutManager;


    public AlbumFragment() {
        // Required empty public constructor
    }


    public static AlbumFragment newInstance(Album album) {
        AlbumFragment fragment = new AlbumFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, album);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAlbum = (Album) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAlbumBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mAlbum != null) {
            binding.recyclerList.setHasFixedSize(true);
            linearLayoutManager = new LinearLayoutManager(getContext());

            getAlbumList();
        }
    }


    public void getAlbumList() {
        Api api = RetrofitClient.getRetrofitInstance().create(Api.class);
        albumList.clear();
        retrofit2.Call<AlbumTrackData> call = api.getAllPhotos(Integer.parseInt(mAlbum.getId()));

        call.enqueue(new retrofit2.Callback<AlbumTrackData>() {
            @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NonNull retrofit2.Call<AlbumTrackData> call, @NonNull retrofit2.Response<AlbumTrackData> response) {

                AlbumTrackData data = response.body();

                assert data != null;
                albumList.addAll(data.getSponsors());

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        binding.tvAlbumTitle.setText(mAlbum.getAlbumTitle());

                        binding.tvArtistName.setText(mAlbum.getArtist().getArtistName());
                        binding.tvTrackListNBTrack.setText("Track list(" + mAlbum.getAlbumNumber() + ")");

                        Picasso.get()
                                .load(mAlbum.getAlbumImage())
                                .into(binding.ivCoverBig);
                        Picasso.get()
                                .load(mAlbum.getArtist().getArtistImage())
                                .into(binding.ivSmall);

                        Log.d("TAG", "getActivity:List " + albumList.toString());
                        adapter = new AlbumAdapter(albumList, mListener);


                        adapter.notifyDataSetChanged();
                        binding.recyclerList.setLayoutManager(linearLayoutManager);
                        binding.recyclerList.setAdapter(adapter);
                    });

                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<AlbumTrackData> call, @NonNull Throwable t) {

            }
        });


    }


    class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.RecyclerAlbumViewHolder> {
        ArrayList<AlbumTracks> listTrackList;
        IAlbumService mListener;

        public AlbumAdapter(ArrayList<AlbumTracks> sortNameList, IAlbumService mListener) {
            this.listTrackList = sortNameList;
            this.mListener = mListener;
        }

        @NonNull
        @Override
        public RecyclerAlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            CustomTracklistBinding binding = CustomTracklistBinding.inflate(getLayoutInflater(), parent, false);
            return new RecyclerAlbumViewHolder(binding);

        }

        @Override
        public int getItemCount() {
            return listTrackList.size();
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerAlbumViewHolder holder, @SuppressLint("RecyclerView") int position) {

            AlbumTracks item = listTrackList.get(position);
            holder.setUpData(item);
        }

        public class RecyclerAlbumViewHolder extends RecyclerView.ViewHolder {

            CustomTracklistBinding mBinding;
            AlbumTracks mItem;

            public RecyclerAlbumViewHolder(CustomTracklistBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }

            public void setUpData(AlbumTracks item) {
                mItem = item;
                mBinding.tvListTitle.setText(mItem.getTitle());
                mBinding.tvListDuration.setText(String.valueOf(DateUtils.formatElapsedTime((long) Double.parseDouble(mItem.getDuration()))));

                mBinding.getRoot().setOnClickListener(view -> mListener.gotoAudioPlayerFragment(mItem, mAlbum));
                mBinding.ivPlus.setOnClickListener(view -> mListener.gotoAddTrackFragment(mItem, mAlbum));
            }

        }


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            if (context instanceof IAlbumService) {
                mListener = (IAlbumService) context;
            } else {
                throw new RuntimeException();
            }
        } catch (RuntimeException exception) {
            exception.printStackTrace();
        }
    }

    IAlbumService mListener;

    public interface IAlbumService {

        void gotoAddTrackFragment(AlbumTracks mItem, Album album);

        void gotoAudioPlayerFragment(AlbumTracks mItem, Album mAlbum);
    }
}