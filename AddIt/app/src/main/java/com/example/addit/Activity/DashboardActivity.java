package com.example.addit.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.addit.Fragment.MyListsFragment;
import com.example.addit.Fragment.NotificationsFragment;
import com.example.addit.Fragment.ProfileFragment;
import com.example.addit.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardActivity extends AppCompatActivity {

   BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        bottomNavigationView=findViewById(R.id.nav_menu);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new MyListsFragment()).commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment=null;
                switch (item.getItemId()){
                    case R.id.nav_list:
                        selectedFragment=new MyListsFragment();
                        break;
                    case R.id.nav_profile:
                        selectedFragment=new ProfileFragment();
                        break;
                    case R.id.nav_notification:
                            selectedFragment=new NotificationsFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).addToBackStack(null).commit();
                return true;
            }
        });

    }


}