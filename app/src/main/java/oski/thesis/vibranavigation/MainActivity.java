package oski.thesis.vibranavigation;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.net.InetAddress;
import java.util.ArrayList;

import oski.thesis.vibranavigation.arduinoutils.ArduinoWifiAPConnectionManager;
import oski.thesis.vibranavigation.wifihotspotutils.ClientScanResult;
import oski.thesis.vibranavigation.wifihotspotutils.FinishScanListener;
import oski.thesis.vibranavigation.wifihotspotutils.WifiApManager;

/**
 * Created by Oskar Kowalski on 20.10.2016.
 */

public class MainActivity extends AppCompatActivity {
  TextView textView;
  WifiApManager wifiApManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    StrictMode.setThreadPolicy(policy);
    setContentView(R.layout.activity_main);

    textView = (TextView) findViewById(R.id.textView);
    wifiApManager = new WifiApManager(this);
  }

  public void openHotSpot(View view) {
    wifiApManager.setWifiApEnabled(null, true);
  }

  public void closeHotSpot(View view) {
    wifiApManager.setWifiApEnabled(null, false);
  }

  public void refreshClients(View view) {
    scan();
  }

  public void cancelDestinationPoint(View view) {
    ArduinoWifiAPConnectionManager.setDestinationPoint(null);
    ArduinoWifiAPConnectionManager.sendCancellationMessage();
  }

  public void openMapView(View view) {
    scan();
    Intent intent = new Intent(this, MapsActivity.class);
    PendingIntent pendingIntent = TaskStackBuilder.create(this)
        .addNextIntentWithParentStack(intent)
        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
    builder.setContentIntent(pendingIntent);
    startActivity(intent);
  }

  private void scan() {
    wifiApManager.getClientList(false, new FinishScanListener() {

      @Override
      public void onFinishScan(final ArrayList<ClientScanResult> clients) {

        textView.setText("WifiApState: " + wifiApManager.getWifiApState() + "\n\n");
        textView.append("Clients: \n");
        for (ClientScanResult clientScanResult : clients) {
          textView.append("####################\n");
          textView.append("IpAddr: " + clientScanResult.getIpAddr() + "\n");
          textView.append("Device: " + clientScanResult.getDevice() + "\n");
          textView.append("HWAddr: " + clientScanResult.getHWAddr() + "\n");
          textView.append("isReachable: " + clientScanResult.isReachable() + "\n");
          if (clientScanResult.getHWAddr().equals("5c:cf:7f:8b:af:55")) {
            try {
              ArduinoWifiAPConnectionManager.setArduinoDeviceIP(InetAddress.getByName(clientScanResult.getIpAddr()));
            } catch (Exception e) {
              Log.e(this.getClass().toString(), "", e);
            }
          }
        }
      }
    });
  }
}
