package com.example.taukir.selfdevelopmentproject.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.taukir.selfdevelopmentproject.Fragment.InformationFragment;
import com.example.taukir.selfdevelopmentproject.R;


public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initViews();
        setSupportActionBar(toolbar);
        gotonextFragment();
    }

    private void gotonextFragment() {
        InformationFragment infromation = new InformationFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, infromation).commit();
    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
    }
    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount()> 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
