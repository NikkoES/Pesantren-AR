package io.github.nikkoes.pesantrenar.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.ColorRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.nikkoes.pesantrenar.R;
import io.github.nikkoes.pesantrenar.adapter.bottomnav.BottomBarAdapter;
import io.github.nikkoes.pesantrenar.adapter.bottomnav.NoSwipePager;
import io.github.nikkoes.pesantrenar.fragment.AkunFragment;
import io.github.nikkoes.pesantrenar.fragment.HomeFragment;
import io.github.nikkoes.pesantrenar.fragment.ListFragment;
import io.github.nikkoes.pesantrenar.utils.PermissionCheck;

public class DashboardActivity extends AppCompatActivity implements LocationListener {

    @BindView(R.id.viewpager)
    NoSwipePager viewPager;
    @BindView(R.id.bottom_navigation)
    AHBottomNavigation bottomNavigation;

    final static int PERMISSION_ALL = 1;
    final static String[] PERMISSIONS = {android.Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    LocationManager locationManager;

    String provider;

    private BottomBarAdapter pagerAdapter;
    private boolean notificationVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ButterKnife.bind(this);

        initMyLocation();

        initView();
    }

    private void initMyLocation() {
        PermissionCheck.initialPermissionCheckAll(this, this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 23 && !isPermissionGranted())
            requestPermissions(PERMISSIONS, PERMISSION_ALL);
        else
            requestLocation();
        if (!isLocationEnabled())
            showAlert(1);
    }

    private void requestLocation() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(provider, 10000, 10, this);
    }

    private boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("mylog", "Permission is granted");
                return true;
            } else {
                Log.v("mylog", "Permission not granted");
                return false;
            }
        }
        return false;
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showAlert(final int status) {
        String message, title, btnText;
        if (status == 1) {
            message = "Lokasi GPS anda belum aktif.\nSilahkan aktifkan GPS anda !";
            title = "Aktifkan GPS";
            btnText = "Pengaturan";
        } else {
            message = "Aplikasi ini membutuhkan request lokasi anda !";
            title = "Terima permission";
            btnText = "Terima";
        }
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setTitle(title)
                .setMessage(message)
                .setPositiveButton(btnText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        if (status == 1) {
                            finish();
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(myIntent);
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(PERMISSIONS, PERMISSION_ALL);
                        }
                    }
                })
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        finish();
                    }
                });
        dialog.show();
    }

    private void initView() {

        setupViewPager();
        setupBottomNavBehaviors();
        setupBottomNavStyle();

        addBottomNavigationItems();
        bottomNavigation.setCurrentItem(0);

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
//                fragment.updateColor(ContextCompat.getColor(MainActivity.this, colors[position]));

                if (!wasSelected)
                    viewPager.setCurrentItem(position);

                // remove notification badge
                int lastItemPos = bottomNavigation.getItemsCount() - 1;
                if (notificationVisible && position == lastItemPos)
                    bottomNavigation.setNotification(new AHNotification(), lastItemPos);

                return true;
            }
        });
    }

    private void setupViewPager() {
        pagerAdapter = new BottomBarAdapter(getSupportFragmentManager());

        //get location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location myLocation = locationManager.getLastKnownLocation(provider);

        if (myLocation != null) {
            pagerAdapter.addFragments(HomeFragment.newInstance(myLocation.getLatitude(), myLocation.getLongitude()));
            pagerAdapter.addFragments(ListFragment.newInstance(myLocation.getLatitude(), myLocation.getLongitude()));

            Log.e("LOCATION", "" + myLocation.getLatitude() + " - " + myLocation.getLatitude());
        } else {
            pagerAdapter.addFragments(HomeFragment.newInstance(0, 0));
            pagerAdapter.addFragments(ListFragment.newInstance(0, 0));

            Log.e("LOCATION", "NULL");
        }
        pagerAdapter.addFragments(new AkunFragment());

        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(pagerAdapter);
    }


    public void setupBottomNavBehaviors() {
        bottomNavigation.setBehaviorTranslationEnabled(false);
        bottomNavigation.setTranslucentNavigationEnabled(false);
    }

    private void setupBottomNavStyle() {
        bottomNavigation.setColored(false);
        bottomNavigation.setBehaviorTranslationEnabled(false);
        bottomNavigation.setDefaultBackgroundColor(getResources().getColor(R.color.bottomtab));
        bottomNavigation.setAccentColor(fetchColor(R.color.colorPrimary));
        bottomNavigation.setInactiveColor(fetchColor(R.color.bottomtab_item_resting));
        bottomNavigation.setColoredModeColors(getResources().getColor(R.color.colorPrimary),
                fetchColor(R.color.bottomtab_item_resting));
        //bottomNavigation.setColored(true);
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
    }

    private void addBottomNavigationItems() {
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Beranda", R.drawable.ic_menu_home, R.color.bottomtab);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Pesantren", R.drawable.ic_menu_lainnya, R.color.bottomtab);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Akun", R.drawable.ic_menu_akun, R.color.bottomtab);

        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
    }

    private int fetchColor(@ColorRes int color) {
        return ContextCompat.getColor(this, color);
    }

    @Override
    public void onBackPressed() {
        finish();
        moveTaskToBack(true);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
