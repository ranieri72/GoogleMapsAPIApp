package com.example.mtz_5555_transp.mymapapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.example.mtz_5555_transp.mymapapplication.Util.RotaHttp;
import com.google.android.gms.maps.model.LatLng;
import com.koushikdutta.ion.Ion;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StaticMapActivity extends AppCompatActivity {

    @BindView(R.id.staticmap)
    ImageView mImageView;

    private LatLng mDestino;
    private LatLng mOrigem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static_map);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mDestino = intent.getParcelableExtra("mDestino");
        mOrigem = intent.getParcelableExtra("mOrigem");

        Ion.with(mImageView)
                .placeholder(R.drawable.common_google_signin_btn_icon_dark)
                .error(R.drawable.ic_polyline_origem)
                .load(RotaHttp.urlStaticMap(mDestino, mOrigem));
    }
}
