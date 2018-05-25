package ro.pub.cs.systems.eim.practicaltest02;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class PracticalTest02MainActivity extends AppCompatActivity {

    private EditText serverPortEditText = null;
    private Button connectButton = null;

    private EditText clientAddressEditText = null;
    private EditText clientPortEditText = null;
    private EditText cityEditText = null;
    private Spinner informationTypeSpinner = null;
    private Button getWeatherForecastButton = null;
    private EditText weatherForecastTextView = null;

    private ServerThread serverThread = null;
    private ClientThread clientThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);

        serverPortEditText = (EditText)findViewById(R.id.server_port_edit_text);
        connectButton = (Button)findViewById(R.id.start_server_button);

        clientAddressEditText = (EditText)findViewById(R.id.client_address_edit_text);
        clientPortEditText = (EditText)findViewById(R.id.client_port_edit_text);
        cityEditText = (EditText)findViewById(R.id.city_edit_text);
        informationTypeSpinner = (Spinner)findViewById(R.id.information_type_spinner);
        getWeatherForecastButton = (Button)findViewById(R.id.get_weather_button);
        weatherForecastTextView = (EditText) findViewById(R.id.response_edit_text);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String serverPort = serverPortEditText.getText().toString();

                if (serverPort == null || serverPort.isEmpty()) {
                    Toast.makeText(getApplicationContext() ,"must specify port", Toast.LENGTH_LONG).show();
                    return;
                }
                serverThread = new ServerThread(Integer.parseInt(serverPort));
                if (serverThread.getServerSocket() == null) {
                    Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                    return;
                }
                serverThread.start();
            }
        });

        getWeatherForecastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String clientAddress = clientAddressEditText.getText().toString();
                String clientPort = clientPortEditText.getText().toString();
                String city = cityEditText.getText().toString();
                String informationType = informationTypeSpinner.getSelectedItem().toString();

                if (clientAddress == null || clientPort == null || city == null || informationType == null ||
                        clientAddress.isEmpty() || clientPort.isEmpty() || city.isEmpty() ||
                        informationType.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "incomplete info", Toast.LENGTH_LONG).show();
                    return;
                }

                weatherForecastTextView.setText("");

                clientThread = new ClientThread(
                        clientAddress, Integer.parseInt(clientPort), city, informationType, weatherForecastTextView
                );
                clientThread.start();
            }
        });

    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method has been invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }
}
