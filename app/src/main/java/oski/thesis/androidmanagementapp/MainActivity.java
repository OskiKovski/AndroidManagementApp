package oski.thesis.androidmanagementapp;

import android.app.PendingIntent;

import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  public void setupHotSpot() {

  }

  public void connectWithWiFi() {

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
}
