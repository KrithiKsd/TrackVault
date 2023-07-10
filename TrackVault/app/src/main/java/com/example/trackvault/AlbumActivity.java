package com.example.trackvault;
/*
 * Author: Krithika Kasaragod
 * FileName: AlbumActivity.java
 */
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trackvault.Fragments.AddTrackFragment;
import com.example.trackvault.Fragments.AlbumFragment;
import com.example.trackvault.Fragments.AudioPlayerFragment;
import com.example.trackvault.Model.Album;
import com.example.trackvault.Model.AlbumTracks;


public class AlbumActivity extends AppCompatActivity implements AlbumFragment.IAlbumService, AddTrackFragment.IAddFragmentService {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        if (getIntent().getExtras() != null && getIntent() != null && getIntent().hasExtra(MainActivity.NAME_KEY)) {

            Album album = (Album) getIntent().getSerializableExtra(MainActivity.NAME_KEY);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.containerNew, AlbumFragment.newInstance(album), "AlbumkFragment")
                    .commit();
        }


    }

    @Override
    public void gotoAddTrackFragment(AlbumTracks mItem, Album album) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerNew, AddTrackFragment.newInstance(mItem, album), "AddTrackFragment")
                .addToBackStack("AlbumkFragment")
                .commit();
    }

    @Override
    public void gotoAudioPlayerFragment(AlbumTracks mItem, Album mAlbum) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerNew, AudioPlayerFragment.newInstance(mItem, mAlbum), "AudioPlayerFragment")
                .commit();
    }

    @Override
    public void gotoAlbumFragment() {
        getSupportFragmentManager().popBackStack();
    }
}