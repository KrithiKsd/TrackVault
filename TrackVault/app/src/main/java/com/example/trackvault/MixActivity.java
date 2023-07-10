package com.example.trackvault;
/*
 * Author: Krithika Kasaragod
 * FileName: MixActivity.java
 */
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trackvault.Fragments.AudioPlayerFragment;
import com.example.trackvault.Fragments.MixDetailsFragment;
import com.example.trackvault.Fragments.MixSharingFragment;
import com.example.trackvault.Model.Mix;
import com.example.trackvault.Model.Track;

public class MixActivity extends AppCompatActivity implements MixDetailsFragment.IMixDetailService, MixSharingFragment.iMixSharing {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mix);


        if (getIntent().getExtras() != null && getIntent() != null && getIntent().hasExtra(MainActivity.NAME_MIX)) {

            Mix mix = (Mix) getIntent().getSerializableExtra(MainActivity.NAME_MIX);
            Log.d("TAG", "onCreate: id " + mix.getMixName());

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.containers, MixDetailsFragment.newInstance(mix), "MixDetailsFragment")
                    .commit();
        }
    }

    @Override
    public void gotoAudioPlayer(Track item) {

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containers, AudioPlayerFragment.newInstance(item), "MixDetailsFragment")
                .commit();
    }

    @Override
    public void gotoMixSharingFragment(Mix item) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containers, MixSharingFragment.newInstance(item), "MixSharingFragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void gotoMixDetails() {

        getSupportFragmentManager().popBackStack();
    }
}