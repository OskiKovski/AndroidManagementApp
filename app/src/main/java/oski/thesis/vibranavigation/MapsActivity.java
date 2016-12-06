package oski.thesis.vibranavigation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Document;

import java.util.ArrayList;

import oski.thesis.vibranavigation.arduinoutils.ArduinoWifiAPConnectionManager;
import oski.thesis.vibranavigation.maputils.GMapV2Direction;

/**
 * Created by Oskar Kowalski on 20.10.2016.
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

  private GoogleMap mMap;
  private static MapMode mapMode = MapMode.AZIMUTH;
  private static LatLng currentLocation;

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
        mMap.clear();
        if (ArduinoWifiAPConnectionManager.getDestinationPoint() != null) {
          mMap.addMarker(new MarkerOptions().position(ArduinoWifiAPConnectionManager.getDestinationPoint()));
        }
        dialog.dismiss();
      }
    });
    final AlertDialog alert = builder.create();
    alert.show();
  }

  private void displayConfirmationDialogBox(final ArrayList<LatLng> points) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setCancelable(true);
    builder.setTitle("Navigate to this point?");
    builder.setInverseBackgroundForced(true);
    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        ArduinoWifiAPConnectionManager.setDestinationPoint(points.get(points.size() - 1));
//        ArduinoWifiAPConnectionManager.sendDestinationPointListToArduino();
        dialog.dismiss();
      }
    });
    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        mMap.clear();
        if (ArduinoWifiAPConnectionManager.getDestinationPoint() != null) {
          mMap.addMarker(new MarkerOptions().position(ArduinoWifiAPConnectionManager.getDestinationPoint()));
          drawRouteOnMap(getRoutePoints(ArduinoWifiAPConnectionManager.getDestinationPoint()));
        }
        dialog.dismiss();
      }
    });
    final AlertDialog alert = builder.create();
    alert.show();
  }

  private void getAndDisplayCurrentLocation() {
    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      return;
    }
    mMap.setMyLocationEnabled(true);
    GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
      @Override
      public void onMyLocationChange(Location location) {
        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16.0f));
      }
    };
    mMap.setOnMyLocationChangeListener(myLocationChangeListener);
  }

  private ArrayList<LatLng> getRoutePoints(LatLng destinationPoint) {
    GMapV2Direction md = new GMapV2Direction();
    Document doc = md.getDocument(currentLocation, destinationPoint);

    ArrayList<LatLng> directionPoints = md.getDirection(doc);
    return directionPoints;
  }

  private void drawRouteOnMap(ArrayList<LatLng> routePointsList) {
    PolylineOptions rectLine = new PolylineOptions().width(10).color(
        Color.RED);

    for (int i = 0; i < routePointsList.size(); i++) {
      rectLine.add(routePointsList.get(i));
    }
    Polyline polylin = mMap.addPolyline(rectLine);
  }

  public void setAzimuthMode(View view) {
    mapMode = MapMode.AZIMUTH;
  }

  public void setRouteMode(View view) {
    mapMode = MapMode.ROUTE;
    getAndDisplayCurrentLocation();
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
      if (mapMode == MapMode.ROUTE) {
        getAndDisplayCurrentLocation();
        ArrayList<LatLng> directionPoints = getRoutePoints(ArduinoWifiAPConnectionManager.getDestinationPoint());
        drawRouteOnMap(directionPoints);
      }
    }
    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
      @Override
      public void onMapClick(LatLng point) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(point));
        if (mapMode == MapMode.AZIMUTH) {
          displayConfirmationDialogBox(point);
        } else {
          ArrayList<LatLng> directionPoints = getRoutePoints(point);
          drawRouteOnMap(directionPoints);
          displayConfirmationDialogBox(directionPoints);
        }
      }
    });
  }
}