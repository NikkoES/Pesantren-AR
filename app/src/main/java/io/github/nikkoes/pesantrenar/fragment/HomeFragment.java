package io.github.nikkoes.pesantrenar.fragment;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import io.github.nikkoes.pesantrenar.R;
import io.github.nikkoes.pesantrenar.activity.DetailPesantrenActivity;
import io.github.nikkoes.pesantrenar.model.Pesantren;
import io.github.nikkoes.pesantrenar.model.PesantrenResponse;
import io.github.nikkoes.pesantrenar.utils.Algorithm;

import static io.github.nikkoes.pesantrenar.data.Constant.PESANTREN;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap mMap;

    private UiSettings mUiSettings;

    public static HomeFragment newInstance(double latitude, double longitude) {

        Bundle args = new Bundle();
        args.putDouble("latitude", latitude);
        args.putDouble("longitude", longitude);
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();

        }

        mapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mUiSettings = mMap.getUiSettings();

        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

        AndroidNetworking.get(PESANTREN)
                .build()
                .getAsObject(PesantrenResponse.class, new ParsedRequestListener() {
                    @Override
                    public void onResponse(Object response) {
                        if (response instanceof PesantrenResponse) {
                            final List<Pesantren> listPesantren = ((PesantrenResponse) response).getData();

                            int height = 100;
                            int width = 100;
                            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.marker);
                            Bitmap b = bitmapdraw.getBitmap();
                            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

                            final double myLat = getArguments().getDouble("latitude", 0);
                            final double myLong = getArguments().getDouble("longitude", 0);

                            for (int i = 0; i < listPesantren.size(); i++) {
                                Pesantren pesantren = listPesantren.get(i);
                                //init pesantren location
                                LatLng lokasiPesantren = new LatLng(Double.parseDouble(pesantren.getLatitude()), Double.parseDouble(pesantren.getLongitude()));
                                if (myLat != 0 || myLong != 0) {
                                    mMap.addMarker(new MarkerOptions().position(lokasiPesantren).title(pesantren.getNamaPesantren()).snippet("Jarak : " + Algorithm.calculateHarversine(myLat, myLong, lokasiPesantren.latitude, lokasiPesantren.longitude) + " KM").icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                                }
                                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                                    @Override
                                    public View getInfoWindow(Marker arg0) {
                                        return null;
                                    }

                                    @Override
                                    public View getInfoContents(Marker marker) {

                                        LinearLayout info = new LinearLayout(getActivity());
                                        info.setOrientation(LinearLayout.VERTICAL);

                                        TextView title = new TextView(getActivity());
                                        title.setTextColor(Color.BLACK);
                                        title.setGravity(Gravity.CENTER);
                                        title.setTypeface(null, Typeface.BOLD);
                                        title.setText(marker.getTitle());

                                        TextView snippet = new TextView(getActivity());
                                        snippet.setTextColor(Color.GRAY);
                                        snippet.setGravity(Gravity.CENTER);
                                        snippet.setText(marker.getSnippet());

                                        info.addView(title);
                                        info.addView(snippet);

                                        return info;
                                    }
                                });
                                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                    @Override
                                    public void onInfoWindowClick(Marker marker) {
                                        for (int i = 0; i < listPesantren.size(); i++) {
                                            Pesantren pesantren = listPesantren.get(i);
                                            if (marker.getTitle().equalsIgnoreCase(pesantren.getNamaPesantren())) {
                                                Intent intent = new Intent(getActivity(), DetailPesantrenActivity.class);
                                                intent.putExtra("pesantren", pesantren);
                                                intent.putExtra("my_location", new LatLng(myLat, myLong));
                                                startActivity(intent);
                                                break;
                                            }
                                        }
                                    }
                                });
                            }

                            //init my location
                            if (myLat != 0 || myLong != 0) {
                                LatLng lokasiMember = new LatLng(getArguments().getDouble("latitude"), getArguments().getDouble("longitude"));
                                CameraPosition cameraPosition = new CameraPosition.Builder().target(lokasiMember).zoom(10).build();
                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            } else {
                                LatLng lokasiMember = new LatLng(-6.9314104, 107.7180555);
                                CameraPosition cameraPosition = new CameraPosition.Builder().target(lokasiMember).zoom(10).build();
                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            }

                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(getActivity(), "Kesalahan teknis, silahkan cek koneksi internet anda !", Toast.LENGTH_SHORT).show();
                    }

                });
    }
}
