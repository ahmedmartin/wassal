package com.ahmed.martin.wassal;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private  order_data order_details;
    private String type,address;
    private MarkerOptions marker;
    private Double latitude , longitude;
    private boolean select_address;
    private AutocompleteSupportFragment autocompleteFragment;

    private LocationManager lm;
    private LocationListener l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        order_details=(order_data) getIntent().getSerializableExtra("order");
        type = getIntent().getStringExtra("type");


/**
 * Initialize Places. For simplicity, the API key is hard-coded. In a production
 * environment we recommend using a secure mechanism to manage API keys.
 */
        // Initialize Places.
        Places.initialize(getApplicationContext(), "AIzaSyArI8Jxx-U4Z3pU0fey3Q9d6bvbMKKYCJE");

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyArI8Jxx-U4Z3pU0fey3Q9d6bvbMKKYCJE");
        }

// Initialize the AutocompleteSupportFragment.
         autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.ADDRESS
        ,Place.Field.LAT_LNG));


        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                mMap.clear();
                address=place.getAddress();
                LatLng point=place.getLatLng();
                latitude=point.latitude;
                longitude=point.longitude;
                marker=new MarkerOptions().position(point).icon(BitmapDescriptorFactory.fromResource(R.drawable.location)).title(address);
                mMap.addMarker(marker);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,18));
                select_address=true;
            }

            @Override
            public void onError(Status status) {
                Toast.makeText(MapsActivity.this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        /* find current location for delivery every 5 m */
       /* l = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // every time location change get data and calculate every thing for new location
                // and put location info in locate
                // locat.setText(location.getLatitude()+" : "+location.getLongitude()+"\n");

                Geocoder geo =new Geocoder(MapsActivity.this);
                List<Address> list =new ArrayList<>();
                try {
                    list=geo.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                } catch (IOException e) {

                }
                if(list.size()>0)
                    // locat.append(list.get(0).getAddressLine(0));

                    latitude=location.getLatitude();
                longitude=location.getLongitude();

            }
            public void onStatusChanged(String s, int i, Bundle bundle) {}
            public void onProviderEnabled(String s) {
                Toast.makeText(MapsActivity.this, " the location is opened ", Toast.LENGTH_LONG).show();
            }
            public void onProviderDisabled(String s) {
                // if location turn off show location setting
                Intent it = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(it);
                Toast.makeText(MapsActivity.this, "must open the location", Toast.LENGTH_LONG).show();
            }
        };

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},10);

        }else
            //do change every 5 s or 10 m
            lm.requestLocationUpdates(lm.GPS_PROVIDER, 60*5000, 1000, l);*/
    }


   /* @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions, int[] grantResults) {
        if (requestCode == 10) {

            if (grantResults.length >0&&grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                location();
            }else{
                Toast.makeText(MapsActivity.this, "must get us location permission to can get orders", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void location  () {

        if (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 10);

        else
            lm.requestLocationUpdates(lm.GPS_PROVIDER, 5000, 1000, l);
    }*/


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                latitude = latLng.latitude;
                longitude = latLng.longitude;
                Geocoder geo =new Geocoder(MapsActivity.this);
                List<Address> list =new ArrayList<>();
                try {
                    list=geo.getFromLocation(latitude,longitude,1);
                } catch (IOException e) {

                }
                if(list.size()>0) {
                    mMap.clear();
                    address = list.get(0).getAddressLine(0);
                    autocompleteFragment.setText(address);
                    marker = new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.location)).title(address);
                    mMap.addMarker(marker);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                    select_address = true;
                }else{
                    Toast.makeText(MapsActivity.this,"must location contain address or you can use search box",Toast.LENGTH_LONG).show();
                }
            }
        });

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(30.0440680, 31.2355120);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,18));
    }

    public void my_location(View view) {
       // location();
    }

    public void select_address(View view) {
        if(select_address) {
                order_details.setR_address(address);
                order_details.setR_lat(latitude);
                order_details.setR_long(longitude);
            Intent main = new Intent(MapsActivity.this, MainActivity.class);
            main.putExtra("order",order_details);
            main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
            startActivity(main);
        }else{
            Toast.makeText(MapsActivity.this,"sorry must search or select location to continue",Toast.LENGTH_LONG).show();
        }
    }
}
