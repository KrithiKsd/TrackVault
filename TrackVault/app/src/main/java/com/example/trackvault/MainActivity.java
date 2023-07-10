package com.example.trackvault;
/*
* Author: Krithika Kasaragod
* FileName: MainActivity.java
*/

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.trackvault.Fragments.MixesFragment;
import com.example.trackvault.Fragments.SearchFragment;
import com.example.trackvault.Model.Album;
import com.example.trackvault.Model.Mix;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements SearchFragment.SearchInterface, MixesFragment.IMixesService {
    private FirebaseAuth mAuth;
    ViewPager2 viewPager2;
    TabLayout tabLayout;
    final static public String NAME_KEY = "NAME";
    final static public String NAME_MIX = "MIX";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        viewPager2 = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabLayout);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager2.setAdapter(viewPagerAdapter);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);


        TabLayoutMediator tabLayoutMediator =
                new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
                    if (position == 0) {
                        tab.setText("Search");
                    } else if (position == 1) {
                        tab.setText("Mixes");
                    }
                });
        tabLayoutMediator.attach();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_course, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.menu_item_add) {
            mAuth.signOut();
            Intent intent = new Intent(this, AuthActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void gotoCreateNewMixActivity() {
        Intent intent = new Intent(this, CreateNewMixActivity.class);
        startActivity(intent);
    }

    @Override
    public void gotoMixActivity(Mix mItem) {
        Intent intent = new Intent(this, MixActivity.class);
        intent.putExtra(NAME_MIX, mItem);
        startActivity(intent);
    }


    static class ViewPagerAdapter extends FragmentStateAdapter {
        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {

            switch (position) {

                case 0:
                    return new SearchFragment();
                case 1:
                    return new MixesFragment();
            }
            return null;
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }

    @Override
    public void gotoAlbumDetails(Album album) {
        Intent intent = new Intent(this, AlbumActivity.class);
        intent.putExtra(NAME_KEY, album);
        startActivity(intent);
    }

}