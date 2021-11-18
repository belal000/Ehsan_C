package com.ehsancharities;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import com.ehsancharities.sign_up.SignUpActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }


    private final LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onLocationChanged(final Location location) {
            //your code here


            Log.e("ERr" , location.getLongitude() +"");


            LatLng currentLocation = new LatLng(location.getLatitude() , location.getLongitude()) ;


            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(currentLocation).title("Marker in Sydney"));
            // mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation) );

            float zoomLevel = 18.0f; //This goes up to 21
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoomLevel));
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {


                SignUpActivity.latLng = latLng ;



                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in Jordan "));

            }
        });

        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        if(!checkPermission())
            requestPermission();
        else{

            mMap.setMyLocationEnabled(true);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1,
                    10, mLocationListener);

            // mMap.getMyLocation() ;

            mMap.getUiSettings().setMyLocationButtonEnabled(true);

        }



        double lat = getIntent().getDoubleExtra("lat" , 0);
        double lng = getIntent().getDoubleExtra("lng" , 0);

        Log.e("ERR",lat+"");

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    boolean checkPermission(){
        return ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    void requestPermission(){


        requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}  ,100);
    }


}