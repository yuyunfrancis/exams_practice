package com.example.exampractice;

import android.os.Bundle;

import com.example.exampractice.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout main_frame;
    private TextView drawerProfileName, drawerProfileText;

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {


                    switch (menuItem.getItemId())
                    {
                        case R.id.nav_home :
                            setFragment(new CategoryFragment());
                            return true;

                        case R.id.nav_leaderboard :
                            setFragment(new LeaderBoardFragment());
                            return  true;

                        case R.id.nav_account :
                            setFragment(new AccountFragment());
                            return true;


                    }
                    return false;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        bottomNavigationView = findViewById(R.id.bottom_nav_bar);
        main_frame = findViewById(R.id.main_frame);

        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawerProfileName = navigationView.getHeaderView(0).findViewById(R.id.nav_drawer_name);
        drawerProfileText = navigationView.getHeaderView(0).findViewById(R.id.nav_drawer_text_img);

        String name = DbQuery.myProfile.getName();
        drawerProfileName.setText("Welcome! " + name);

        drawerProfileText.setText(name.toUpperCase().substring(0,1));

        setFragment(new CategoryFragment());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            setFragment(new CategoryFragment());
        } else if (id == R.id.nav_account) {
             setFragment(new AccountFragment());

        } else if (id == R.id.nav_leaderboard) {
            setFragment(new LeaderBoardFragment());

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void setFragment(Fragment fragment)
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(main_frame.getId(),fragment);
        transaction.commit();
    }



}
