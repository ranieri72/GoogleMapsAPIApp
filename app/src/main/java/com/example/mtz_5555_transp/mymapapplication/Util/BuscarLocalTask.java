package com.example.mtz_5555_transp.mymapapplication.Util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.content.AsyncTaskLoader;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by mtz-5555-transp on 14/08/17.
 */

public class BuscarLocalTask extends AsyncTaskLoader<List<Address>> {

    String mLocal;
    List<Address> mEnderecosEncontrados;
    private Context mContext;

    public BuscarLocalTask(Context context, String local) {
        super(context);
        mContext = context;
        mLocal = local;
    }

    @Override
    protected void onStartLoading() {
        if (mEnderecosEncontrados == null) {
            forceLoad();
        } else {
            deliverResult(mEnderecosEncontrados);
        }
    }

    @Override
    public List<Address> loadInBackground() {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            mEnderecosEncontrados = geocoder.getFromLocationName(mLocal, 10);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mEnderecosEncontrados;
    }
}
