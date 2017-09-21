package com.example.mtz_5555_transp.mymapapplication.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.AsyncTaskLoader;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by mtz-5555-transp on 15/08/17.
 */

public class StaticBitmapTask extends AsyncTaskLoader<Bitmap> {

    Bitmap bmp;
    LatLng mOrigem;
    LatLng mDestino;

    public StaticBitmapTask(Context context, LatLng orig, LatLng dest) {
        super(context);
        mOrigem = orig;
        mDestino = dest;
    }

    @Override
    protected void onStartLoading() {
        if (bmp == null) {
            forceLoad();
        } else {
            deliverResult(bmp);
        }
    }

    @Override
    public Bitmap loadInBackground() {
        bmp = RotaHttp.carregarBitmap(mOrigem, mDestino);
        return bmp;
    }
}
