package io.github.nikkoes.pesantrenar.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.nikkoes.pesantrenar.R;
import io.github.nikkoes.pesantrenar.model.Pesantren;
import io.github.nikkoes.pesantrenar.utils.Algorithm;

public class DetailPesantrenActivity extends AppCompatActivity implements OnMapReadyCallback {

    @BindView(R.id.txt_alamat)
    TextView txtAlamat;
    @BindView(R.id.txt_kontak)
    TextView txtKontak;
    @BindView(R.id.txt_jarak)
    TextView txtJarak;
    @BindView(R.id.image_pesantren)
    ImageView imagePesantren;
    @BindView(R.id.txt_pimpinan)
    TextView txtPimpinan;
    @BindView(R.id.txt_ormas)
    TextView txtOrmas;
    @BindView(R.id.txt_kurikulum)
    TextView txtKurikulum;
    @BindView(R.id.txt_fasilitas)
    TextView txtFasilitas;

    private GoogleMap mMap;
    private UiSettings mUiSettings;

    List<Pesantren> listPesantren;
    String idPesantren;
    Pesantren pesantren;

    LatLng myLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pesantren);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        listPesantren = (List<Pesantren>) getIntent().getSerializableExtra("list_pesantren");
        idPesantren = getIntent().getStringExtra("id_pesantren");
        myLocation = getIntent().getExtras().getParcelable("my_location");

        for (int i = 0; i < listPesantren.size(); i++) {
            if (idPesantren.equalsIgnoreCase(listPesantren.get(i).getIdPesantren())) {
                pesantren = listPesantren.get(i);
                break;
            }
        }

        initMapFragment();
        initToolbar();
        initUI();
    }

    private void initMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void initUI() {
        Picasso.get().load(pesantren.getFoto()).placeholder(R.drawable.ic_pesantren).into(imagePesantren);
        Location pesantrenLocation = new Location("");
        pesantrenLocation.setLatitude(Double.parseDouble(pesantren.getLatitude()));
        pesantrenLocation.setLongitude(Double.parseDouble(pesantren.getLongitude()));

        double distanceHarversine = Algorithm.calculateHarversine(myLocation.latitude, myLocation.longitude, Double.parseDouble(pesantren.getLatitude()), Double.parseDouble(pesantren.getLongitude()));

        txtPimpinan.setText(pesantren.getPimpinan());
        txtOrmas.setText(pesantren.getOrmas());
        txtKurikulum.setText(pesantren.getKurikulum());
        txtFasilitas.setText(pesantren.getFasilitas());
        txtKontak.setText(pesantren.getNoTelp());
        txtJarak.setText(distanceHarversine + " km");
        txtAlamat.setText(pesantren.getAlamat());
    }

    private void initToolbar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Detail Pesantren");
        getSupportActionBar().setSubtitle(pesantren.getNamaPesantren());
    }

    @OnClick({R.id.btn_navigation, R.id.btn_shareloc})
    public void actionButton(View v) {
        switch (v.getId()) {
            case R.id.btn_navigation:
                Intent intent = new Intent(this, RutePesantrenActivity.class);
                intent.putExtra("pesantren", pesantren);
                intent.putExtra("origin", myLocation.latitude + "," + myLocation.longitude);
                intent.putExtra("destination", pesantren.getLatitude() + "," + pesantren.getLongitude());
                startActivity(intent);
                break;
            case R.id.btn_shareloc:
                String uri = "http://maps.google.com/maps?saddr=" + pesantren.getLatitude() + "," + pesantren.getLongitude();
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String subject = "Berikut adalah lokasi dari " + pesantren.getNamaPesantren();
                sharingIntent.putExtra(Intent.EXTRA_TEXT, subject + "\n" + uri);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                super.onBackPressed();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mUiSettings = mMap.getUiSettings();

        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

        int height = 100;
        int width = 100;

        BitmapDrawable bitMine = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_pesantren);
        Bitmap bMine = bitMine.getBitmap();
        Bitmap smallMarkerMine = Bitmap.createScaledBitmap(bMine, width, height, false);

        //icon marker
        LatLng lokasiPesantren = new LatLng(Double.parseDouble(pesantren.getLatitude()), Double.parseDouble(pesantren.getLongitude()));
        mMap.addMarker(new MarkerOptions().position(lokasiPesantren).title(pesantren.getNamaPesantren()).snippet("Alamat : " + pesantren.getAlamat()).icon(BitmapDescriptorFactory.fromBitmap(smallMarkerMine)));
        CameraPosition cameraPosition = new CameraPosition.Builder().target(lokasiPesantren).zoom(15).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
