package com.example.mtz_5555_transp.mymapapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mtz_5555_transp.mymapapplication.Util.BuscarLocalTask;
import com.example.mtz_5555_transp.mymapapplication.Util.MessageDialogFragment;
import com.example.mtz_5555_transp.mymapapplication.Util.RotaTask;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {//, LocationListener {

    static final int LOADER_ROTA = 2;
    static final int LOADER_ENDERECO = 1;
    static final String EXTRA_ORIG = "orig";
    static final String EXTRA_DEST = "dest";
    static final String EXTRA_ROTA = "rota";
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    private static final String REQUESTING_LOCATION_UPDATES_KEY = "location_updates";

    @BindView(R.id.edt_local)
    EditText mEdtLocal;
    @BindView(R.id.imgBtn_buscar)
    ImageButton mBtnBuscar;
    @BindView(R.id.txt_progresso)
    TextView mTxtProgresso;
    @BindView(R.id.llProgresso)
    LinearLayout mLayoutProgresso;

    // Firebase Database
    private DatabaseReference mRoutesDatabaseReference;

    private ArrayList<LatLng> mRota;
    private LatLng mDestino;
    private GoogleMap mGoogleMap;
    private LatLng mOrigem;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private boolean mRequestingLocationUpdates;

    private LoaderManager mLoaderManager;
    private BitmapDescriptor polylineOrigem;
    private BitmapDescriptor polylineDestino;
    LoaderManager.LoaderCallbacks<List<LatLng>> mRotaCallback = new LoaderManager.LoaderCallbacks<List<LatLng>>() {
        @Override
        public Loader<List<LatLng>> onCreateLoader(int id, Bundle args) {
            //Toast.makeText(MapsActivity.this, String.valueOf(mDestino.latitude), Toast.LENGTH_LONG).show();
            return new RotaTask(MapsActivity.this, mOrigem, mDestino);
        }

        @Override
        public void onLoadFinished(Loader<List<LatLng>> loader, final List<LatLng> data) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    mRota = new ArrayList<>(data);
                    //Toast.makeText(MapsActivity.this, String.valueOf(data.get(data.size()-1).latitude), Toast.LENGTH_LONG).show();
                    atualizarMapa();
                    ocultarProgresso();
                }
            });
        }

        @Override
        public void onLoaderReset(Loader<List<LatLng>> loader) {

        }
    };
    LoaderManager.LoaderCallbacks<List<Address>> mBuscaLocalCallback = new LoaderManager.LoaderCallbacks<List<Address>>() {
        @Override
        public Loader<List<Address>> onCreateLoader(int id, Bundle args) {
            return new BuscarLocalTask(MapsActivity.this, mEdtLocal.getText().toString());
        }

        @Override
        public void onLoadFinished(Loader<List<Address>> loader, final List<Address> data) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    exibirListaEnderecos(data);
                }
            });
        }

        @Override
        public void onLoaderReset(Loader<List<Address>> loader) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        updateValuesFromBundle(savedInstanceState);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                mRoutesDatabaseReference.push().setValue(locationResult.getLastLocation());
                //mRoutesDatabaseReference.push().setValue(new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude()));

//                for (Location location : locationResult.getLocations()) {
//                }
                Date date = new Date(locationResult.getLastLocation().getTime());
                Toast.makeText(MapsActivity.this, locationResult.getLastLocation().getProvider()
                        + " Data: " + date
                        + " Speed: " + String.valueOf(((locationResult.getLastLocation().getSpeed() * 3600) / 1000)), Toast.LENGTH_SHORT).show();
            }
        };

        mLoaderManager = getSupportLoaderManager();

        polylineDestino = BitmapDescriptorFactory
                .fromResource(R.drawable.ic_polyline_destino);
        polylineOrigem = BitmapDescriptorFactory
                .fromResource(R.drawable.ic_polyline_origem);

        mRoutesDatabaseReference = FirebaseDatabase.getInstance().getReference().child("routes");
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        // Update the value of mRequestingLocationUpdates from the Bundle.
        if (savedInstanceState != null && savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
            mRequestingLocationUpdates = savedInstanceState.getBoolean(
                    REQUESTING_LOCATION_UPDATES_KEY);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putParcelable(EXTRA_ORIG, mOrigem);
        outState.putParcelable(EXTRA_DEST, mDestino);
        outState.putParcelable(EXTRA_ROTA, (Parcelable) mRota);
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                mRequestingLocationUpdates);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mOrigem = savedInstanceState.getParcelable(EXTRA_ORIG);
            mDestino = savedInstanceState.getParcelable(EXTRA_DEST);
            mRota = savedInstanceState.getParcelable(EXTRA_ROTA);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (estaCarregando(LOADER_ENDERECO) && mDestino == null) {
            mLoaderManager.initLoader(LOADER_ENDERECO, null, mBuscaLocalCallback);
            exibirProgresso("Buscando endereço...");
        } else if (estaCarregando(LOADER_ROTA) && mRota == null) {
            mLoaderManager.initLoader(LOADER_ROTA, null, mRotaCallback);
            exibirProgresso("Carregando rota...");
        }
        getLastLocation();
    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                dialogPermission();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            }
        } else {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        mOrigem = new LatLng(location.getLatitude(), location.getLongitude());
                        atualizarMapa();
                    }
                }
            });
        }
    }

    private void dialogPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissão de GPS");
        builder.setMessage("Esse aplicativo necessita da permissão de uso do GPS para funcionar");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);

                if (ContextCompat.checkSelfPermission(MapsActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    finish();
                } else {
                    getLastLocation();
                }
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        AlertDialog alerta = builder.create();
        alerta.show();
    }

    private boolean estaCarregando(int loaderEndereco) {
        Loader<?> loader = mLoaderManager.getLoader(loaderEndereco);
        return loader != null && loader.isStarted();
    }

    private void exibirProgresso(String msg) {
        mTxtProgresso.setText(msg);
        mLayoutProgresso.setVisibility(View.VISIBLE);
    }

    private void ocultarProgresso() {
        mLayoutProgresso.setVisibility(View.GONE);
    }

    private void exibirListaEnderecos(final List<Address> enderecosEncontrados) {
        ocultarProgresso();
        mBtnBuscar.setEnabled(true);

        if (enderecosEncontrados != null && enderecosEncontrados.size() > 0) {
            String[] descricaoDosEnderecos = new String[enderecosEncontrados.size()];
            for (int i = 0; i < descricaoDosEnderecos.length; i++) {
                Address address = enderecosEncontrados.get(i);
                StringBuffer rua = new StringBuffer();
                for (int j = 0; j < address.getMaxAddressLineIndex(); j++) {
                    if (rua.length() > 0) {
                        rua.append('\n');
                    }
                    rua.append(address.getAddressLine(j));
                }
                String pais = address.getCountryName();
                String descricaoEndereco = String.format("%s, %s", rua, pais);
                descricaoDosEnderecos[i] = descricaoEndereco;
            }
            DialogInterface.OnClickListener selecionarEnderecoClick = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Address enderecoSelecionado = enderecosEncontrados.get(i);
                    mDestino = new LatLng(
                            enderecoSelecionado.getLatitude(),
                            enderecoSelecionado.getLongitude());
                    //atualizarMapa();
                    carregarRota();
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Selecione o destino")
                    .setItems(descricaoDosEnderecos, selecionarEnderecoClick);
            MessageDialogFragment mDialogEnderecos = new MessageDialogFragment();
            mDialogEnderecos.setDialog(builder.create());
            mDialogEnderecos.show(getFragmentManager(), "DIALOG_ENDERECO_DESTINO");
        }
    }

    private void carregarRota() {
        //Toast.makeText(MapsActivity.this, "Carregando rota...", Toast.LENGTH_SHORT).show();
        mRota = null;
        mLoaderManager.restartLoader(LOADER_ROTA, null, mRotaCallback);
        exibirProgresso("Carregando rota...");
    }

    private void buscarEndereco() {
        InputMethodManager methodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        methodManager.hideSoftInputFromWindow(mEdtLocal.getWindowToken(), 0);

        mLoaderManager.restartLoader(LOADER_ENDERECO, null, mBuscaLocalCallback);
        exibirProgresso("Procurando endereço...");
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5 * 1000);
        locationRequest.setFastestInterval(1000);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(locationRequest,
                    mLocationCallback,
                    null /* Looper */);
        }
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private void atualizarMapa() {
        mGoogleMap.clear();

        if (mRota != null && mRota.size() > 0) {
            //Toast.makeText(MapsActivity.this, "Atualizando mapa com rota!", Toast.LENGTH_SHORT).show();
            LatLngBounds area = new LatLngBounds.Builder()
                    .include(mOrigem)
                    .include(mDestino)
                    .build();

            mGoogleMap.addMarker(new MarkerOptions().position(mDestino).title("Destino").icon(polylineDestino));
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(area, 50));

            Marker mMarkerLocalAtual = mGoogleMap.addMarker(new MarkerOptions()
                    .position(mDestino)
                    .title("Destino")
                    .icon(polylineOrigem)
                    .position(mOrigem));
            startLocationUpdates();

            //Toast.makeText(MapsActivity.this, String.valueOf(mRota.get(mRota.size() - 1).latitude), Toast.LENGTH_LONG).show();

            PolylineOptions polylineOptions = new PolylineOptions()
                    .addAll(mRota)
                    .width(5)
                    .color(Color.RED)
                    .visible(true);
            mGoogleMap.addPolyline(polylineOptions);
        } else {
            if (mOrigem != null) {
                Toast.makeText(MapsActivity.this, "Atualizando mapa sem rota!", Toast.LENGTH_SHORT).show();
                mGoogleMap.addMarker(new MarkerOptions().position(mOrigem).title("Local Atual"));
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mOrigem, 17.0F));
            }
        }
    }

    @OnClick(R.id.imgBtn_buscar)
    void onItemClicked(View view) {
        switch (view.getId()) {
            case R.id.imgBtn_buscar:
                mBtnBuscar.setEnabled(false);
                buscarEndereco();
                break;
        }
    }

//    @Override
//    public void onLocationChanged(Location location) {
//        Toast.makeText(MapsActivity.this, "Localização alterada!", Toast.LENGTH_SHORT).show();
//        mMarkerLocalAtual.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
//    }
}