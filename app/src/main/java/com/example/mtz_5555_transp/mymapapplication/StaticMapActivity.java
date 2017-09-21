package com.example.mtz_5555_transp.mymapapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import butterknife.BindView;

public class StaticMapActivity extends AppCompatActivity {

    @BindView(R.id.staticmap)
    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static_map);


        Intent intent = getIntent();
        Bitmap bitmap = intent.getParcelableExtra("bitmap");


        mImageView.setImageBitmap(bitmap);
    }


}
