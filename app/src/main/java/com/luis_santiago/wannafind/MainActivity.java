package com.luis_santiago.wannafind;

import android.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final int MY_PERMISION_REQUEST = 1;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private final String TAG = MainActivity.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;

    private LatLng mCenterLatLong;

    private GoogleMap mGoogleMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpMap();
        buildGoogleApiClient();
    }


    private void setUpMap(){
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setScrollGesturesEnabled(true);
        mGoogleMap.getUiSettings().setRotateGesturesEnabled(false);
        mGoogleMap.getUiSettings().setZoomGesturesEnabled(false);

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @SuppressLint("MissingPermission")
                @Override
                public void onSuccess(Location location) {
                    mGoogleMap.setMyLocationEnabled(true);
                    LatLng latLng = new LatLng(location.getLatitude() , location.getLongitude());
                    CameraPosition cameraPosition = setUpCameraPosition(latLng);
                    //googleMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
                    mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    mGoogleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                        @Override
                        public void onCameraChange(CameraPosition cameraPosition) {
                                Log.e(TAG , "Position camera change" + cameraPosition);
                                mCenterLatLong = cameraPosition.target;
                                try{
                                    Location mLocation = new Location("");
                                    mLocation.setLatitude(mCenterLatLong.latitude);
                                    mLocation.setLongitude(mCenterLatLong.longitude);
                                    //googleMap.addMarker(new MarkerOptions().position(new LatLng(mCenterLatLong.latitude , mCenterLatLong.longitude)));

                                }catch (Exception e ){
                                    e.printStackTrace();
                                }
                        }
                    });
                }
            });
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISION_REQUEST);
        }
    }

    private CameraPosition setUpCameraPosition(LatLng location){
        return CameraPosition.builder()
                .target(location)
                .zoom(16)
                .build();
    }



    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
