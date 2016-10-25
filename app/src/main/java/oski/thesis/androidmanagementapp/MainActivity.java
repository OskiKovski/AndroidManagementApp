package oski.thesis.androidmanagementapp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.view.WindowManager;

import java.lang.reflect.Method;

/**
 * Created by Oskar Kowalski on 20.10.2016.
 */

public class MainActivity extends AppCompatActivity {

  public WifiManager wifiManager;
  public Context context;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  public void setupHotSpot(View view) {
    if (createWifiAccessPoint()) {
      System.out.println("CONNECTED");
    }
    ;
    WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
    recreate();
  }

  public void connectWithWiFi(View view) {

  }

  public void openMapView(View view) {
    Intent intent = new Intent(this, MapsActivity.class);

    PendingIntent pendingIntent =
        TaskStackBuilder.create(this)
            .addNextIntentWithParentStack(intent)
            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

    NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
    builder.setContentIntent(pendingIntent);

    startActivity(intent);
  }

  private boolean createWifiAccessPoint() {
    WifiManager wifiManager = (WifiManager) getBaseContext().getSystemService(Context.WIFI_SERVICE);
    if (wifiManager.isWifiEnabled()) {
      wifiManager.setWifiEnabled(false);
    }
    wifiManager.setWifiEnabled(false);
    Method[] mMethods = wifiManager.getClass().getDeclaredMethods();
    for (Method mMethod : mMethods) {
      if (mMethod.getName().equals("setWifiApEnabled")) {
        try {
          mMethod.invoke(wifiManager, null, true);
          return true;
        } catch (Exception ex) {
        }
        break;
      }
    }
    return false;
  }
}
