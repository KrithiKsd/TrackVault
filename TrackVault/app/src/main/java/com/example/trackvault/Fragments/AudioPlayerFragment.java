package com.example.trackvault.Fragments;
/*
 * Author: Krithika Kasaragod
 * FileName: AudioPlayerFragment.java
 */
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.trackvault.Model.Album;
import com.example.trackvault.Model.AlbumTracks;
import com.example.trackvault.Model.Track;
import com.example.trackvault.R;
import com.example.trackvault.databinding.FragmentAudioPlayerBinding;
import com.squareup.picasso.Picasso;

import java.io.IOException;


public class AudioPlayerFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";


    private AlbumTracks mAlbumTrack;
    private Album mAlbum;

    private Track mTrack;

    boolean checked = false;
    FragmentAudioPlayerBinding binding;
    MediaPlayer mediaPlayer = null;

    public static int oneTimeOnly = 0;

    private final Handler myHandler = new Handler();


    private double startTime = 0;

    public AudioPlayerFragment() {
        // Required empty public constructor
    }

    public static AudioPlayerFragment newInstance(AlbumTracks param1, Album param2) {
        AudioPlayerFragment fragment = new AudioPlayerFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        args.putSerializable(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static AudioPlayerFragment newInstance(Track track) {
        AudioPlayerFragment fragment = new AudioPlayerFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM3, track);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAlbumTrack = (AlbumTracks) getArguments().getSerializable(ARG_PARAM1);
            mAlbum = (Album) getArguments().getSerializable(ARG_PARAM2);
            mTrack = (Track) getArguments().getSerializable(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAudioPlayerBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.seekBar.setClickable(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAlbum != null && mAlbumTrack != null) {
            binding.tvTrackTitle.setText(mAlbum.getAlbumTitle());
            Picasso.get()
                    .load(mAlbum.getAlbumImage())
                    .into(binding.ivBig);


            if (!checked) {
                binding.imageView2.setImageResource(R.drawable.play);
            } else {
                binding.imageView2.setImageResource(R.drawable.stop);
            }


            binding.imageView2.setOnClickListener(view -> {
                checked = !checked;

                if (!checked) {
                    binding.imageView2.setImageResource(R.drawable.play);
                    if (mediaPlayer != null) {
                        mediaPlayer.pause();
                    }
                } else {
                    binding.imageView2.setImageResource(R.drawable.stop);
                    player(mAlbumTrack.getPreview());
                }

            });
        }

        if (mTrack != null) {
            Log.d("TAG", "onResume: data**" + mTrack);

            binding.tvTrackTitle.setText(mTrack.getTrack());
            Picasso.get()
                    .load(mTrack.getImage())
                    .into(binding.ivBig);


            if (!checked) {
                binding.imageView2.setImageResource(R.drawable.play);
            } else {
                binding.imageView2.setImageResource(R.drawable.stop);
            }


            binding.imageView2.setOnClickListener(view -> {
                checked = !checked;

                if (!checked) {
                    binding.imageView2.setImageResource(R.drawable.play);
                    if (mediaPlayer != null) {
                        mediaPlayer.pause();
                    }
                } else {
                    binding.imageView2.setImageResource(R.drawable.stop);
                    player(mTrack.getPreview());
                }

            });
        }

    }

    void player(String preview) {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        try {
            mediaPlayer.setDataSource(preview);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();

        double finalTime = mediaPlayer.getDuration();
        startTime = mediaPlayer.getCurrentPosition();

        if (oneTimeOnly == 0) {
            binding.seekBar.setMax((int) finalTime);
        }

        binding.seekBar.setProgress((int) startTime);
        myHandler.postDelayed(UpdateSongTime, 100);


        mediaPlayer.setOnCompletionListener(mediaPlayer -> binding.imageView2.setImageResource(R.drawable.circle_gray));

    }

    private final Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();

            binding.seekBar.setProgress((int) startTime);
            myHandler.postDelayed(this, 100);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }
}