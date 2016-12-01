package oski.thesis.vibranavigation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import oski.thesis.vibranavigation.arduinoutils.ArduinoWifiAPConnectionManager;

/**
 * Created by Oskar Kowalski on 20.10.2016.
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

  private GoogleMap mMap;

  private void displayConfirmationDialogBox(final LatLng point) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setCancelable(true);
    builder.setTitle("Navigate to this point?");
    builder.setInverseBackgroundForced(true);
    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        ArduinoWifiAPConnectionManager.setDestinationPoint(point);
        ArduinoWifiAPConnectionManager.sendDestinationPointToArduino();
        dialog.dismiss();
      }
    });
    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        if (ArduinoWifiAPConnectionManager.getDestinationPoint() != null) {
          mMap.clear();
          mMap.addMarker(new MarkerOptions().position(ArduinoWifiAPConnectionManager.getDestinationPoint()));
        } else {
          mMap.clear();
        }
        dialog.dismiss();
      }
    });
    final AlertDialog alert = builder.create();
    alert.show();
  }

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
    if (ArduinoWifiAPConnectionManager.getDestinationPoint() != null) {
      mMap.addMarker(new MarkerOptions().position(ArduinoWifiAPConnectionManager.getDestinationPoint()));
    }
    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
      @Override
      public void onMapClick(LatLng point) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(point));
        displayConfirmationDialogBox(point);
      }
    });
  }
}
