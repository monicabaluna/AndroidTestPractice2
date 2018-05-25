package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import cz.msebera.android.httpclient.client.ClientProtocolException;

/**
 * Created by monica on 25.05.2018.
 */

public class ServerThread extends Thread {
    private int port;
    private ServerSocket serverSocket;
    private HashMap<String, WeatherForecastInformation> data;

    public ServerThread(int port){
        this.port = port;
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
        }
        this.data = new HashMap<>();
    }

    public ServerSocket getServerSocket(){
        return serverSocket;
    }

    public synchronized void setData(String city, WeatherForecastInformation weatherForecastInformation) {
        this.data.put(city, weatherForecastInformation);
    }

    public synchronized HashMap<String, WeatherForecastInformation> getData() {
        return data;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Log.i(Constants.TAG, "[Server thread] Waiting for client");
                Socket socket = serverSocket.accept();

                CommunicationThread communicationThread = new CommunicationThread(this, socket);
                communicationThread.start();

            }
        } catch (ClientProtocolException clientProtocolexception) {
            Log.e(Constants.TAG, "exception");
        } catch (IOException exception) {
            Log.e(Constants.TAG, "exception");
        }
    }

    public void stopThread() {
        interrupt();
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "[SERVER THREAD] An exception has occurred: " + ioException.getMessage());
            }
        }

    }
}
