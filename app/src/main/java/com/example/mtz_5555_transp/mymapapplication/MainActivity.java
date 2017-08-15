package com.example.mtz_5555_transp.mymapapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.map)
    void onItemClicked(View view) {
        Intent it;
        switch (view.getId()) {
            case R.id.map:
                it = new Intent(this, MapsActivity.class);
                startActivity(it);
                break;
        }
    }
}
