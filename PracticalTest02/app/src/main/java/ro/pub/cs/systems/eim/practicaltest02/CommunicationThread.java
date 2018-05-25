package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;
import android.widget.EditText;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;

/**
 * Created by monica on 25.05.2018.
 */

public class CommunicationThread extends Thread {

    private Socket socket;
    private ServerThread serverThread;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.socket = socket;
        this.serverThread = serverThread;
    }

    @Override
    public void run() {
        try {
            Log.v(Constants.TAG, "Connection opened with " + socket.getInetAddress() + ":" + socket.getLocalPort());

            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }

            HashMap<String, WeatherForecastInformation> data = serverThread.getData();

            String city = bufferedReader.readLine();
            String informationType = bufferedReader.readLine();
            if (city == null || city.isEmpty() || informationType == null || informationType.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (city / information type!");
                return;
            }

            String result = "";

            WeatherForecastInformation weatherForecastInformation = null;
            if (data.containsKey(city)) {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
                weatherForecastInformation = data.get(city);
            }  else {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS + "?query=" + city);

                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String pageSourceCode = httpClient.execute(httpGet, responseHandler);
                if (pageSourceCode == null) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                    return;
                }
                Document document = Jsoup.parse(pageSourceCode);
                Element element = document.child(0);

                Elements elements = element.getElementsByTag("script");
                for (Element script: elements) {
                    String scriptData = script.data();
                    if (scriptData.contains(Constants.SEARCH_KEY)) {
                        int position = scriptData.indexOf(Constants.SEARCH_KEY) + Constants.SEARCH_KEY.length();
                        scriptData = scriptData.substring(position);
                        JSONObject currentObservation = new JSONObject(scriptData);
                        Log.d(Constants.TAG, currentObservation.toString(2));
//                        Log.d(Constants.TAG, content.toString(2));
//                        JSONObject currentObservation = content.getJSONObject(Constants.CURRENT_OBSERVATION);
                        String temperature = currentObservation.getString(Constants.TEMPERATURE);
                        String windSpeed = currentObservation.getString(Constants.WIND_SPEED);
                        String condition = currentObservation.getString(Constants.CONDITION);
                        String pressure = currentObservation.getString(Constants.PRESSURE);
                        String humidity = currentObservation.getString(Constants.HUMIDITY);
                        weatherForecastInformation = new WeatherForecastInformation(
                                temperature, windSpeed, condition, pressure, humidity
                        );
                        serverThread.setData(city, weatherForecastInformation);
                        break;
                    }
                }
            }

            if (weatherForecastInformation == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Weather Forecast Information is null!");
                return;
            }
            switch(informationType) {
                case Constants.ALL:
                    result = weatherForecastInformation.toString();
                    break;
                case Constants.TEMPERATURE:
                    result = weatherForecastInformation.getTemperature();
                    break;
                case Constants.WIND_SPEED:
                    result = weatherForecastInformation.getWindSpeed();
                    break;
                case Constants.CONDITION:
                    result = weatherForecastInformation.getCondition();
                    break;
                case Constants.HUMIDITY:
                    result = weatherForecastInformation.getHumidity();
                    break;
                case Constants.PRESSURE:
                    result = weatherForecastInformation.getPressure();
                    break;
                default:
                    result = "[COMMUNICATION THREAD] Wrong information type (all / temperature / wind_speed / condition / humidity / pressure)!";
            }
            printWriter.println(result);

            // - get the PrintWriter object in order to write on the socket (use Utilities.getWriter())
            // - print a line containing the text in the serverTextEditText edit text
            printWriter.println(result);

            socket.close();
            Log.v(Constants.TAG, "Connection closed");
        } catch (Exception exception) {
            Log.e(Constants.TAG, "An exception has occurred: " + exception.getMessage());
        }
    }

}
