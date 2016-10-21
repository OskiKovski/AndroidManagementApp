package oski.thesis.androidmanagementapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

  private ListView list;
  private ArrayAdapter<String> adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    list = (ListView) findViewById(R.id.activity_main);

    String commands[] = {"Set up a hotspot", "Connect with WiFi", "Set destination"};

    ArrayList<String> commandList = new ArrayList<String>();
    commandList.addAll(Arrays.asList(commands));

    adapter = new ArrayAdapter<String>(this, R.layout.list_row, commandList);

    list.setAdapter(adapter);
  }
}
