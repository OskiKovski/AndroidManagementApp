package oski.thesis.androidmanagementapp;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import oski.thesis.androidmanagementapp.arduinoutils.ArduinoWifiAPConnectionManager;

/**
 * Created by Oskar Kowalski on 20.10.2016.
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

  private GoogleMap mMap;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    StrictMode.setThreadPolicy(policy);
    setContentView(R.layout.activity_maps);
    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;
    LatLng warsaw = new LatLng(52.245, 21);
    mMap.moveCamera(CameraUpdateFactory.newLatLng(warsaw));
    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(warsaw, 13.0f));
    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
      @Override
      public void onMapClick(LatLng point) {
        ArduinoWifiAPConnectionManager.setDestinationPoint(point);
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(point));
        ArduinoWifiAPConnectionManager.sendDestinationPointToArduino();
      }
    });
  }
}
