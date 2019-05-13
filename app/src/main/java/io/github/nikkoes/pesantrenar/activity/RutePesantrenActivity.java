package io.github.nikkoes.pesantrenar.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.beyondar.android.util.location.BeyondarLocationManager;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.nikkoes.pesantrenar.R;
import io.github.nikkoes.pesantrenar.ar.ArFragmentSupport;
import io.github.nikkoes.pesantrenar.model.Pesantren;
import io.github.nikkoes.pesantrenar.model.rute.RuteResponse;
import io.github.nikkoes.pesantrenar.model.rute.Step;
import io.github.nikkoes.pesantrenar.utils.Algorithm;
import io.github.nikkoes.pesantrenar.utils.DialogUtils;

import static io.github.nikkoes.pesantrenar.data.Constant.URL_MAPS;

public class RutePesantrenActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    @BindView(R.id.txt_nama_pesantren)
    TextView txtNamaPesantren;
    @BindView(R.id.arcam_layout)
    FrameLayout arcamLayout;

    Pesantren pesantren;

    private final static String TAG = "Rute A";
    private String srcLatLng;
    private String destLatLng;
    private Step steps[];

    private LocationManager locationManager;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private ArFragmentSupport arFragmentSupport;
    private World world;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_pesantren);
        ButterKnife.bind(this);

        pesantren = (Pesantren) getIntent().getSerializableExtra("pesantren");

        setGoogleClient();
    }

    private void setGoogleClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void initAR() {
        List<List<LatLng>> polylineLatLng = new ArrayList<>();

        world = new World(getApplicationContext());

        world.setGeoPosition(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        Log.d(TAG, "LOCATION : " + mLastLocation.getLatitude() + " " + mLastLocation.getLongitude());
        world.setDefaultImage(R.drawable.ar_sphere_default);

        arFragmentSupport = (ArFragmentSupport) getSupportFragmentManager().findFragmentById(R.id.layout_cam_ar);

        Log.d(TAG, "BANYAKNYA STEP : " + steps.length);

        for (int i = 0; i < steps.length; i++) {
            polylineLatLng.add(i, PolyUtil.decode(steps[i].getPolyline().getPoints()));

            String instructions = steps[i].getHtmlInstructions();

            //indeks 0 berarti step pertama
            if (i == 0) {
                GeoObject signObject = new GeoObject(10000 + i);
                signObject.setImageResource(R.drawable.start);
                signObject.setGeoPosition(steps[i].getStartLocation().getLat(), steps[i].getStartLocation().getLng());
                world.addBeyondarObject(signObject);
                Log.d(TAG, "\nSTART : " + i);
            }

            //indeks terakhir berarti step terakhir
            if (i == steps.length - 1) {
                GeoObject signObject = new GeoObject(10000 + i);
                signObject.setImageResource(R.drawable.stop);
                LatLng latlng = SphericalUtil.computeOffset(
                        new LatLng(steps[i].getEndLocation().getLat(), steps[i].getEndLocation().getLng()),
                        4f, SphericalUtil.computeHeading(
                                new LatLng(steps[i].getStartLocation().getLat(), steps[i].getStartLocation().getLng()),
                                new LatLng(steps[i].getEndLocation().getLat(), steps[i].getEndLocation().getLng())));
                signObject.setGeoPosition(latlng.latitude, latlng.longitude);
                world.addBeyondarObject(signObject);
                Log.d(TAG, "\nEND : " + i);
            }

            //instruksi belok kanan
            if (instructions.contains("right")) {
                Log.d(TAG, "INSTRUKSI : " + instructions);
                GeoObject signObject = new GeoObject(10000 + i);
                signObject.setImageResource(R.drawable.turn_right);
                signObject.setGeoPosition(steps[i].getStartLocation().getLat(), steps[i].getStartLocation().getLng());
                world.addBeyondarObject(signObject);
                Log.d(TAG, "BELOK KANAN : " + i);
            }
            //instruksi belok kiri
            else if (instructions.contains("left")) {
                Log.d(TAG, "INSTRUKSI : " + instructions);
                GeoObject signObject = new GeoObject(10000 + i);
                signObject.setImageResource(R.drawable.turn_left);
                signObject.setGeoPosition(steps[i].getStartLocation().getLat(), steps[i].getStartLocation().getLng());
                world.addBeyondarObject(signObject);
                Log.d(TAG, "BELOK KIRI : " + i);
            }
        }

        int temp_polycount = 0;
        int temp_inter_polycount = 0;

        //untuk melakukan render ke bentuk AR
        for (int j = 0; j < polylineLatLng.size(); j++) {
            for (int k = 0; k < polylineLatLng.get(j).size(); k++) {
                GeoObject polyGeoObj = new GeoObject(1000 + temp_polycount++);

                polyGeoObj.setGeoPosition(polylineLatLng.get(j).get(k).latitude, polylineLatLng.get(j).get(k).longitude);
                polyGeoObj.setImageResource(R.drawable.ar_sphere_default);
                polyGeoObj.setName("arObj" + j + k);

                try {
                    //menghitung jarak antar polyline
                    double dist = Algorithm.calculateHarversine(polylineLatLng.get(j).get(k).latitude,
                            polylineLatLng.get(j).get(k).longitude, polylineLatLng.get(j).get(k + 1).latitude,
                            polylineLatLng.get(j).get(k + 1).longitude) * 1000;

                    //mengecek jika jarak antara 2 polyline lebih besar 2 kali dari pada luas space (biar gak terjadi renggang")
                    if (dist > 6) {

                        Log.d(TAG, "EXCEPTION : " + dist);

                        //jumlah ar objek yang akan ditambahkan
                        int arObj_count = ((int) dist / 3) - 1;

                        double heading = SphericalUtil.computeHeading(new LatLng(polylineLatLng.get(j).get(k).latitude,
                                        polylineLatLng.get(j).get(k).longitude),
                                new LatLng(polylineLatLng.get(j).get(k + 1).latitude,
                                        polylineLatLng.get(j).get(k + 1).longitude));

                        LatLng tempLatLng = SphericalUtil.computeOffset(new LatLng(polylineLatLng.get(j).get(k).latitude,
                                        polylineLatLng.get(j).get(k).longitude)
                                , 3f
                                , heading);

                        //jarak objek yang ditambahkan
                        double increment_dist = 3f;

                        for (int i = 0; i < arObj_count; i++) {

                            GeoObject inter_polyGeoObj = new GeoObject(5000 + temp_inter_polycount++);

                            //simpen lat long tambahan
                            if (i > 0 && k < polylineLatLng.get(j).size()) {
                                increment_dist += 3f;

                                tempLatLng = SphericalUtil.computeOffset(new LatLng(polylineLatLng.get(j).get(k).latitude,
                                                polylineLatLng.get(j).get(k).longitude),
                                        increment_dist,
                                        SphericalUtil.computeHeading(new LatLng(polylineLatLng.get(j).get(k).latitude
                                                , polylineLatLng.get(j).get(k).longitude), new LatLng(
                                                polylineLatLng.get(j).get(k + 1).latitude
                                                , polylineLatLng.get(j).get(k + 1).longitude)));
                            }

                            inter_polyGeoObj.setGeoPosition(tempLatLng.latitude, tempLatLng.longitude);
                            inter_polyGeoObj.setImageResource(R.drawable.ar_sphere_default);
                            inter_polyGeoObj.setName("inter_arObj" + j + k + i);

                            world.addBeyondarObject(inter_polyGeoObj);
                        }
                    }
                } catch (Exception e) {
                    Log.d(TAG, "EXCEPTION : " + e.getMessage());
                }

                world.addBeyondarObject(polyGeoObj);
            }
        }

        arFragmentSupport.setWorld(world);
    }

    private void initData() {
        if (getIntent() != null) {
            intent = getIntent();

            srcLatLng = getIntent().getStringExtra("origin");
            destLatLng = getIntent().getStringExtra("destination");

            txtNamaPesantren.setText(pesantren.getNamaPesantren());

            loadRutePesantren();
        }
    }

    private void loadRutePesantren() {
        AndroidNetworking.get(URL_MAPS + "maps/api/directions/json?")
                .addQueryParameter("origin", srcLatLng)
                .addQueryParameter("destination", destLatLng)
                .addQueryParameter("key", getResources().getString(R.string.google_maps_key))
                .build()
                .getAsObject(RuteResponse.class, new ParsedRequestListener() {
                    @Override
                    public void onResponse(Object response) {
                        if (response instanceof RuteResponse) {
                            RuteResponse response1 = ((RuteResponse) response);
                            int stepSize = response1.getRoutes().get(0).getLegs().get(0).getSteps().size();

                            steps = new Step[stepSize];

                            for (int i = 0; i < stepSize; i++) {
                                steps[i] = response1.getRoutes().get(0).getLegs().get(0).getSteps().get(i);
                            }

                            initAR();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        DialogUtils.closeDialog();
                        Toast.makeText(getApplicationContext(), "Kesalahan teknis, silahkan cek koneksi internet anda !", Toast.LENGTH_SHORT).show();
                    }

                });
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        BeyondarLocationManager.disable();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BeyondarLocationManager.enable();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        } else {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

            if (mLastLocation != null) {
                try {
                    initData(); //Fetch Intent Values
                } catch (Exception e) {
                    Log.d(TAG, "onCreate: Intent Error");
                }
            }
        }

        startLocationUpdates();
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    protected void startLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, createLocationRequest(), this);

        } catch (SecurityException e) {
            Toast.makeText(this, "Location Permission not granted . Please Grant the permissions", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        if (world != null) {
            world.setGeoPosition(location.getLatitude(), location.getLongitude());
        }
    }
}
