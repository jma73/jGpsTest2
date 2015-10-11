package dk.jpeace.jan.jgpstest2;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Button;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {

    private LocationManager locationManager;
    private TextView textView;
    private TextView textView1;
    private TextView textView2;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        scrollView = (ScrollView) findViewById(R.id.scrollView);
        // textView1 = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);
        Button buttonUpdate = (Button) findViewById(R.id.buttonUpdate);
buttonUpdate.setOnClickListener(this);


        textView = new TextView(this);
        textView.setText("JJJ");
        textView.setTextColor(Color.GREEN);

        scrollView.addView(textView);
        //scrollView.addView(textView);
        //scrollView.addView(textView2);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        ShowLocation();


    }

    private void ShowLocation() {
        //locationManager.getLastKnownLocation()

        for (String udbyder : locationManager.getAllProviders()) {
            LocationProvider lp = locationManager.getProvider(udbyder);

//            }

            locationManager.requestLocationUpdates(udbyder, 60000, 20, this);

            Location sted = locationManager.getLastKnownLocation(udbyder);


            textView.setTextColor(Color.CYAN);
            textView.setText(udbyder + " - tændt: " + locationManager.isProviderEnabled(udbyder)
                    + "\n præcision=" + lp.getAccuracy() + " strømforbrug=" + lp.getPowerRequirement()
                    + "\n kræver satellit=" + lp.requiresSatellite() + " kræver net=" + lp.requiresNetwork()
                    + "\n sted=" + sted + "\n\n");


 // textView2.setText("getAltitude" + sted.getAltitude());

            if (sted != null) { // forsøg at finde nærmeste adresse
                try {
                    Geocoder geocoder = new Geocoder(this);
                    List<Address> adresser = geocoder.getFromLocation(sted.getLatitude(), sted.getLongitude(), 1);
                    if (adresser != null && adresser.size() > 0) {
                        Address adresse = adresser.get(0);
                        textView.append("NÆRMESTE ADRESSE: " + adresse + "\n\n");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            else
            {
                textView.append("in ShowLocation()");
                textView.append("NÆRMESTE ADRESSE: Kunne ikke findes..." + "\n\n");
                GetAndShowLocationJakob();
            }


        }
    }

    @Override
    public void onClick(View v) {
        ShowLocation();
    }




    @Override
    protected void onResume() {
        super.onResume();

        textView.append("in onResume...");
        GetAndShowLocationJakob();
    }

    private void GetAndShowLocationJakob() {
        Criteria kriterium = new Criteria();
        kriterium.setAccuracy(Criteria.ACCURACY_FINE);
        String udbyder = locationManager.getBestProvider(kriterium, true); // giver "gps" hvis den er slået til

        textView.append("\n\n========= Lytter til udbyder: " + udbyder + "\n\n");

        if (udbyder == null) {
            textView.append("\n\nUps, der var ikke tændt for nogen udbyder. Tænd for GPS eller netværksbaseret stedplacering og prøv igen.");
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            return;
        }

        //  Bed om opdateringer, der går mindst 60. sekunder og mindst 20. meter mellem hver
        locationManager.requestLocationUpdates(udbyder, 60000, 20, this);

        Location sted = locationManager.getLastKnownLocation(udbyder);

        if (sted != null) { // forsøg at finde nærmeste adresse
            try {
                Geocoder geocoder = new Geocoder(this);
                List<Address> adresser = geocoder.getFromLocation(sted.getLatitude(), sted.getLongitude(), 1);
                if (adresser != null && adresser.size() > 0) {
                    Address adresse = adresser.get(0);
                    textView.append("NÆRMESTE ADRESSE: " + adresse + "\n\n");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    // Metode specificeret i LocationListener
    public void onLocationChanged(Location sted) {
        textView.append("in onLocationChanged:");
        // orig:
        textView.append(sted + "\n\n");
        scrollView.scrollTo(0, textView.getHeight()); // rul ned i bunden

        // jans:
        textView2.setTextColor(Color.MAGENTA);
        textView2.append(sted + "\n\n");

    }




    /**
     * Called when the provider status changes. This method is called when
     * a provider is unable to fetch a location or if the provider has recently
     * become available after a period of unavailability.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     * @param status   {@link LocationProvider#OUT_OF_SERVICE} if the
     *                 provider is out of service, and this is not expected to change in the
     *                 near future; {@link LocationProvider#TEMPORARILY_UNAVAILABLE} if
     *                 the provider is temporarily unavailable but is expected to be available
     *                 shortly; and {@link LocationProvider#AVAILABLE} if the
     *                 provider is currently available.
     * @param extras   an optional Bundle which will contain provider specific
     *                 status variables.
     *                 <p/>
     *                 <p> A number of common key/value pairs for the extras Bundle are listed
     *                 below. Providers that use any of the keys on this list must
     *                 provide the corresponding value as described below.
     *                 <p/>
     *                 <ul>
     *                 <li> satellites - the number of satellites used to derive the fix
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    /**
     * Called when the provider is enabled by the user.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     */
    @Override
    public void onProviderEnabled(String provider) {

    }

    /**
     * Called when the provider is disabled by the user. If requestLocationUpdates
     * is called on an already disabled provider, this method is called
     * immediately.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     */
    @Override
    public void onProviderDisabled(String provider) {

    }
}
