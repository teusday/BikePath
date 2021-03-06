package me.zaksharp.bikepath;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap myMap;
    private FusedLocationProviderClient fusedLocationClient;

    private FirebaseAuth mAuth;

    private Button profileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        profileButton=(Button)findViewById(R.id.profileButton);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(MainActivity.this,Profile.class);

            }
        });

        MapFragment mMap = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        mMap.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        LatLng here = new LatLng(43, -88);
        switch(checkSelfPermission("android.permission.ACCESS_FINE_LOCATION")) {
            case PackageManager.PERMISSION_GRANTED:
                myMap.setMyLocationEnabled(true);
                break;
            case PackageManager.PERMISSION_DENIED:
                requestPermissions(new String[]{"android.permission.ACCESS_FINE_LOCATION"},0);
                if(checkSelfPermission("android.permission.ACCESS_FINE_LOCATION")==PackageManager.PERMISSION_GRANTED)
                    myMap.setMyLocationEnabled(true);
                break;
            default:
                break;
        }

        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(here,5));
        myMap.moveCamera(CameraUpdateFactory.newLatLng(here));

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            myMap.moveCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                            new LatLng(location.getLatitude(),location.getLongitude())
                                            ,15));
                        }
                    }
                });


        Polyline polyline1 = myMap.addPolyline(new PolylineOptions()
                .clickable(false)
                .add(
                        new LatLng(43.043826, -87.903335),
                        new LatLng(43.044136, -87.898351)));
        polyline1.setColor(Color.GREEN);
        polyline1.setTag("Bike Lane");

        Marker mSnow = myMap.addMarker(new MarkerOptions()
                .position(new LatLng(43.043933, -87.901246))
                .title("Snow in Bike Lane")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
    }




    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){

            sendToStart();

        }

    }

    private void sendToStart(){
        Intent startIntent = new Intent(this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }

}
