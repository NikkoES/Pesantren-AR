package io.github.nikkoes.pesantrenar.activity;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.beyondar.android.view.OnClickBeyondarObjectListener;
import com.beyondar.android.world.BeyondarObject;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.nikkoes.pesantrenar.R;
import io.github.nikkoes.pesantrenar.ar.ArBeyondarGLSurfaceView;
import io.github.nikkoes.pesantrenar.ar.ArFragmentSupport;
import io.github.nikkoes.pesantrenar.ar.ArObject;
import io.github.nikkoes.pesantrenar.ar.OnTouchBeyondarViewListenerMod;
import io.github.nikkoes.pesantrenar.model.Pesantren;
import io.github.nikkoes.pesantrenar.utils.Algorithm;
import io.github.nikkoes.pesantrenar.utils.DialogUtils;
import io.github.nikkoes.pesantrenar.utils.NetworkCheck;
import io.github.nikkoes.pesantrenar.utils.TimeLogger;

public class CariPesantrenActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener, OnClickBeyondarObjectListener,
        OnTouchBeyondarViewListenerMod {

    @BindView(R.id.image_pesantren)
    ImageView imagePesantren;
    @BindView(R.id.txt_nama_pesantren)
    TextView txtNamaPesantren;
    @BindView(R.id.txt_alamat_pesantren)
    TextView txtAlamatPesantren;
    @BindView(R.id.txt_jarak_pesantren)
    TextView txtJarakPesantren;
    @BindView(R.id.layout_detail)
    CardView layoutDetail;
    @BindView(R.id.seekbar_jangkauan)
    SeekBar seekbarJangkauan;
    @BindView(R.id.layout_seekbar)
    CardView layoutSeekbar;

    private final static String TAG = "Pencarian A";

    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private ArFragmentSupport arFragmentSupport;
    private World world;

    List<Pesantren> listPesantren;
    List<ArObject> arObjectList;

    LatLng myLocation;

    String idPesantren;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pesantren);

        ButterKnife.bind(this);

        if (!NetworkCheck.isConnect(this)) {
            DialogUtils.showSnack(this, "Aktifkan koneksi internet anda !", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        initView();

    }

    private void initView() {
        listPesantren = (List<Pesantren>) getIntent().getSerializableExtra("list_pesantren");

        layoutSeekbar.setVisibility(View.GONE);
        layoutDetail.setVisibility(View.GONE);

        arFragmentSupport = (ArFragmentSupport) getSupportFragmentManager().findFragmentById(
                R.id.layout_cam_ar);
        arFragmentSupport.setOnClickBeyondarObjectListener(this);
        arFragmentSupport.setOnTouchBeyondarViewListener(this);

        setGoogleApiClient(); //Sets the GoogleApiClient

        seekbarJangkauan.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (i == 0) {
                    loadARObject(1000);
                } else {
                    loadARObject((i + 1) * 1000);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() == 0) {
                    Toast.makeText(CariPesantrenActivity.this, "Radius: 1000 Metres", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CariPesantrenActivity.this, "Radius: " + (seekBar.getProgress() + 1) * 1000 + " Metres", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void dialogDetailAR() {
        layoutSeekbar.setVisibility(View.GONE);
        layoutDetail.setVisibility(View.VISIBLE);

        for (int i = 0; i < arObjectList.size(); i++) {
            ArObject pesantren = arObjectList.get(i);
            if (idPesantren.equalsIgnoreCase(pesantren.getIdPesantren())) {
                Picasso.get().load(pesantren.getImagePesantren()).placeholder(R.drawable.ic_pesantren).into(imagePesantren);
                txtNamaPesantren.setText(pesantren.getNamaPesantren());
                txtAlamatPesantren.setText(pesantren.getAlamatPesantren());
                txtJarakPesantren.setText("" + pesantren.getJarakPesantren() + " KM");
                break;
            }
        }
    }

    private void setGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void loadARObject(int radius) {
        layoutSeekbar.setVisibility(View.VISIBLE);

        arObjectList = new ArrayList<>();

        world = new World(getApplicationContext());
        try {
            world.setGeoPosition(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        } catch (NullPointerException e) {
            world.setGeoPosition(0, 0);
        }
        world.setDefaultImage(R.drawable.ar_sphere_default);

        //distance in metre (m)
        arFragmentSupport.setMaxDistanceToRender(radius);

        Log.e("LIST PESANTREN", "" + listPesantren.size());

        for (int i = 0; i < listPesantren.size(); i++) {
            Pesantren pesantren = listPesantren.get(i);

            View view = getLayoutInflater().inflate(R.layout.item_ar_container, null);
            TextView txtNama = view.findViewById(R.id.txt_nama_pesantren);
            TextView txtJarak = view.findViewById(R.id.txt_jarak_pesantren);
            ImageView imagePesantren = view.findViewById(R.id.image_pesantren);

            double distance;

            try {
                distance = Algorithm.calculateHarversine(mLastLocation.getLatitude(), mLastLocation.getLongitude(), Double.parseDouble(listPesantren.get(i).getLatitude()), Double.parseDouble(listPesantren.get(i).getLongitude()));
            } catch (NullPointerException e) {
                distance = Algorithm.calculateHarversine(0, 0, Double.parseDouble(listPesantren.get(i).getLatitude()), Double.parseDouble(listPesantren.get(i).getLongitude()));
            }

            arFragmentSupport.getGLSurfaceView().setPullCloserDistance((float) distance * 30);

            Picasso.get().load(pesantren.getFoto()).placeholder(R.drawable.ic_launcher).into(imagePesantren);
            txtNama.setText(pesantren.getNamaPesantren());
            txtJarak.setText(distance + " KM");

            view.setDrawingCacheEnabled(true);
            view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);

            Bitmap snapshot;
            try {
                view.measure(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                snapshot = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(snapshot);
                view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                view.draw(canvas);
            } finally {
                view.setDrawingCacheEnabled(false);
            }

            String uri = saveToInternalStorage(snapshot, pesantren.getIdPesantren() + ".png");

            //GEO OBJECT
            GeoObject object = new GeoObject(1000 * (i + 1));
            object.setName(pesantren.getIdPesantren());
            object.setGeoPosition(Double.parseDouble(pesantren.getLatitude()), Double.parseDouble(pesantren.getLongitude()));
            object.setImageUri(uri);

            arObjectList.add(new ArObject(pesantren.getNamaPesantren(), pesantren.getAlamat(), distance, pesantren.getFoto(), pesantren.getIdPesantren()));
            world.addBeyondarObject(object);
        }

        arFragmentSupport.setWorld(world);
    }

    private String saveToInternalStorage(Bitmap bitmapImage, String name) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File mypath = new File(directory, name);

        Log.e(TAG, "saveToInternalStorage: PATH:" + mypath.toString());

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mypath.toString();
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        } else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (mLastLocation != null) {
                try {
                    loadARObject(1000);
                    myLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                } catch (Exception e) {
                    Log.e(TAG, "onCreate: Intent Error");
                }
            }
        }

    }

    @Override
    public void onClickBeyondarObject(ArrayList<BeyondarObject> beyondarObjects) {
        if (beyondarObjects.size() > 0) {
            idPesantren = beyondarObjects.get(0).getName();
            dialogDetailAR();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onTouchBeyondarView(MotionEvent event, ArBeyondarGLSurfaceView var2) {

        float x = event.getX();
        float y = event.getY();

        ArrayList<BeyondarObject> geoObjects = new ArrayList<BeyondarObject>();
        var2.getBeyondarObjectsOnScreenCoordinates(x, y, geoObjects);

        String textEvent = "";
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                textEvent = "Event type ACTION_DOWN: ";
                break;
            case MotionEvent.ACTION_UP:
                textEvent = "Event type ACTION_UP: ";
                break;
            case MotionEvent.ACTION_MOVE:
                textEvent = "Event type ACTION_MOVE: ";
                break;
            default:
                break;
        }

        for (BeyondarObject geoObject : geoObjects) {
            textEvent = textEvent + " " + geoObject.getName();
            Log.e(TAG, "onTouchBeyondarView: ATTENTION !!! " + textEvent);
        }
    }

    @OnClick({R.id.btn_detail, R.id.btn_remove})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_detail:
                Intent intent = new Intent(this, DetailPesantrenActivity.class);
                intent.putExtra("id_pesantren", idPesantren);
                intent.putExtra("my_location", myLocation);
                intent.putExtra("list_pesantren", (Serializable) listPesantren);
                startActivity(intent);
                break;
            case R.id.btn_remove:
                layoutSeekbar.setVisibility(View.VISIBLE);
                layoutDetail.setVisibility(View.GONE);
                imagePesantren.setImageResource(android.R.color.transparent);
                txtNamaPesantren.setText("");
                txtAlamatPesantren.setText("");
                txtJarakPesantren.setText("");
                break;
        }
    }
}
