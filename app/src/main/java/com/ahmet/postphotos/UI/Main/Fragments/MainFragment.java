package com.ahmet.postphotos.UI.Main.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.ahmet.postphotos.Config.Common;
import com.ahmet.postphotos.R;
import com.ahmet.postphotos.UI.Main.Fragments.Post.Fragments.AddPostFragment;
import com.ahmet.postphotos.UI.Main.Fragments.Post.Fragments.HomeFragment;

public class MainFragment extends Fragment {

    private BottomNavigationView mBottomNavigationView;

    private int id;
    private FragmentManager fragmentManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        id = R.id.frame_layout_home;
        fragmentManager = getActivity().getSupportFragmentManager();

        Common.replaceFragments(new HomeFragment(), id, fragmentManager);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View convertView = inflater.inflate(R.layout.fragment_main, container, false);

        mBottomNavigationView = convertView.findViewById(R.id.btn_navigation_view);

        return convertView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                switch (item.getItemId()){

                    case R.id.nav_home:
                        Common.replaceFragments(new HomeFragment(), id, fragmentManager);
                        // getSupportActionBar().setTitle(R.string.app_name);
                        return true;
                    case R.id.nav_add_post:
                        Common.replaceFragments(new AddPostFragment(), id, fragmentManager);
                        //  getSupportActionBar().setTitle("Add Post");
                        return true;
                    case R.id.nav_profile:
                        Common.replaceFragments(new ProfileFragment(), id, fragmentManager);
                        //  getSupportActionBar().setTitle("Profile");
                        return true;
                    case R.id.nav_friends_request:
                        Common.replaceFragments(new RequestsFragment(), id, fragmentManager);
                        //  getSupportActionBar().setTitle("Profile");
                        return true;
                    default:
                        return false;
                }
            }
        });

    }
}
