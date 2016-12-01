package oski.thesis.vibranavigation.arduinoutils;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Oskar Kowalski on 30.10.2016.
 */

public abstract class ArduinoWifiAPConnectionManager {
  private static InetAddress arduinoDeviceIP;
  private static LatLng destinationPoint;

  private static int LISTEN_PORT_NUMBER = 3333;

  public static void sendDestinationPointToArduino() {
    Socket socket = new Socket();
    InetSocketAddress socketAddress = new InetSocketAddress(arduinoDeviceIP, LISTEN_PORT_NUMBER);
    Log.d("TCP", "C: Connecting...");

    try {
      Log.d("TCP", "C: Sending: '" + destinationPoint.toString() + "'" + " to " + arduinoDeviceIP + ":" + LISTEN_PORT_NUMBER);
      socket.connect(socketAddress);
      Log.e("Connect", "");
      DataOutputStream DataOut = new DataOutputStream(socket.getOutputStream());
      DataOut.flush();
      DataOut.writeBytes(destinationPoint.toString());
      DataOut.flush();
      socket.close();
    } catch (Exception e) {
      Log.e("Arduino connection", "", e);
    }
  }

  public static void sendCancellationMessage() {
    Socket socket = new Socket();
    InetSocketAddress socketAddress = new InetSocketAddress(arduinoDeviceIP, LISTEN_PORT_NUMBER);
    String cancellationString = "CANCEL";
    Log.d("TCP", "C: Connecting...");

    try {
      Log.d("TCP", "C: Sending: '" + cancellationString + "'" + " to " + arduinoDeviceIP + ":" + LISTEN_PORT_NUMBER);
      socket.connect(socketAddress);
      Log.e("Connect", "");
      DataOutputStream DataOut = new DataOutputStream(socket.getOutputStream());
      DataOut.flush();
      DataOut.writeBytes(cancellationString);
      DataOut.flush();
      socket.close();
    } catch (Exception e) {
      Log.e("Arduino connection", "", e);
    }
  }

  public static void setArduinoDeviceIP(InetAddress arduinoDeviceIP) {
    ArduinoWifiAPConnectionManager.arduinoDeviceIP = arduinoDeviceIP;
  }

  public static void setDestinationPoint(LatLng destinationPoint) {
    ArduinoWifiAPConnectionManager.destinationPoint = destinationPoint;
  }

  public static LatLng getDestinationPoint() {
    return destinationPoint;
  }
}
