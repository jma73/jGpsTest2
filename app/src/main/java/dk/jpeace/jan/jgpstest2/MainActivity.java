package dk.jpeace.jan.jgpstest2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private LocationManager locationManager;
    private TextView textView;
    private TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);
        Button buttonUpdate = (Button) findViewById(R.id.buttonUpdate);
buttonUpdate.setOnClickListener(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        ShowLocation();


    }

    private void ShowLocation() {
        //locationManager.getLastKnownLocation()

        for (String udbyder : locationManager.getAllProviders()) {
            LocationProvider lp = locationManager.getProvider(udbyder);

//            }
            Location sted = locationManager.getLastKnownLocation(udbyder);


            textView.setText(udbyder + " - tændt: " + locationManager.isProviderEnabled(udbyder)
                    + "\n præcision=" + lp.getAccuracy() + " strømforbrug=" + lp.getPowerRequirement()
                    + "\n kræver satellit=" + lp.requiresSatellite() + " kræver net=" + lp.requiresNetwork()
                    + "\n sted=" + sted + "\n\n");

textView2.setText("getAltitude" + sted.getAltitude());

        }
    }

    @Override
    public void onClick(View v) {
        ShowLocation();
    }
}
