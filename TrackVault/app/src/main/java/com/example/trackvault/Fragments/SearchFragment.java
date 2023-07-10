package com.example.trackvault.Fragments;
/*
 * Author: Krithika Kasaragod
 * FileName: SearchFragment.java
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackvault.Api;
import com.example.trackvault.Model.Album;
import com.example.trackvault.Model.AlbumData;
import com.example.trackvault.R;
import com.example.trackvault.RetrofitClient;
import com.example.trackvault.databinding.CustomAlbumListBinding;
import com.example.trackvault.databinding.FragmentSearchBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class SearchFragment extends Fragment {


    FragmentSearchBinding binding;
    ArrayList<Album> albumList = new ArrayList<>();

    AlbumAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recycler.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext());

        binding.btnSearch.setOnClickListener(view1 -> {

            String albumName = binding.etAlbumName.getText().toString();

            if (albumName.isEmpty()) {
                displayAlert(getString(R.string.label_error_forum_description));
            } else {
                getAlbums(albumName);
            }
        });
    }

    public void getAlbums(String albumName) {

        Api api = RetrofitClient.getRetrofitInstance().create(Api.class);
        albumList.clear();

        retrofit2.Call<AlbumData> call = api.getAlbums(albumName);

        call.enqueue(new retrofit2.Callback<AlbumData>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull retrofit2.Call<AlbumData> call, @NonNull retrofit2.Response<AlbumData> response) {
                AlbumData data = response.body();

                assert data != null;
                albumList.addAll(data.getAlbums());

                getActivity().runOnUiThread(() -> {
                    binding.etAlbumName.setText("");
                    adapter = new AlbumAdapter(albumList, mListener);
                    adapter.notifyDataSetChanged();
                    binding.recycler.setLayoutManager(linearLayoutManager);
                    binding.recycler.setAdapter(adapter);
                });
            }

            @Override
            public void onFailure(retrofit2.Call<AlbumData> call, Throwable t) {
                Log.d("TAG", "onFailure: ****");
            }
        });
    }

    public void displayAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.label_alert))
                .setMessage(message)
                .setPositiveButton(getString(R.string.label_ok), (dialogInterface, i) -> dialogInterface.dismiss());
        builder.show();
    }

    class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.RecyclerAlbumViewHolder> {
        ArrayList<Album> listAlbum;
        SearchInterface mListener;

        public AlbumAdapter(ArrayList<Album> sortNameList, SearchInterface mListener) {
            this.listAlbum = sortNameList;
            this.mListener = mListener;
        }

        @NonNull
        @Override
        public RecyclerAlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            CustomAlbumListBinding binding = CustomAlbumListBinding.inflate(getLayoutInflater(), parent, false);
            return new RecyclerAlbumViewHolder(binding);

        }

        @Override
        public int getItemCount() {
            return listAlbum.size();
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerAlbumViewHolder holder, @SuppressLint("RecyclerView") int position) {

            Album item = listAlbum.get(position);
            holder.setUpData(item);
        }

        public class RecyclerAlbumViewHolder extends RecyclerView.ViewHolder {

            CustomAlbumListBinding mBinding;
            Album mItem;
            public RecyclerAlbumViewHolder(CustomAlbumListBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }

            @SuppressLint("SetTextI18n")
            public void setUpData(Album item) {
                mItem = item;
                mBinding.tvTitle.setText(mItem.getAlbumTitle());
                mBinding.tvName.setText(getString(R.string.label_artist) + mItem.getArtist().getArtistName());
                mBinding.tvNBtracks.setText(getString(R.string.label_album_number) + mItem.getAlbumNumber());
                Picasso.get()
                        .load(mItem.getAlbumImage())
                        .into(mBinding.imageView);

                mBinding.getRoot().setOnClickListener(view -> mListener.gotoAlbumDetails(mItem));
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            if (context instanceof SearchInterface) {
                mListener = (SearchInterface) context;
            } else {
                throw new RuntimeException();
            }
        } catch (RuntimeException exception) {
            exception.printStackTrace();
        }
    }

    SearchInterface mListener;

    public interface SearchInterface {
        void gotoAlbumDetails(Album album);
    }
}