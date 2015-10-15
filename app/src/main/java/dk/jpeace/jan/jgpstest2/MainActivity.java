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
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Button;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {

    private LocationManager locationManager;
    Button buttonUpdate, buttonShowLocations;

    private TextView textViewAppend;
    private TextView textView1;
    //private TextView textView2;
    private ScrollView scrollViewBottom;
    private ScrollView scrollViewTop;
    private TextView textViewOnChanged;
    ArrayList<Location> locationArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationArrayList = new ArrayList<Location>();

        scrollViewBottom = (ScrollView) findViewById(R.id.scrollViewBottom);
        scrollViewTop = (ScrollView) findViewById(R.id.scrollViewTop);
        textViewAppend = (TextView) findViewById(R.id.textViewAppend);
        textViewOnChanged = (TextView) findViewById(R.id.textViewOnChanged);
      //  textView2 = (TextView) findViewById(R.id.textView2);

        buttonUpdate = (Button) findViewById(R.id.buttonUpdate);
        buttonUpdate.setOnClickListener(this);
        buttonShowLocations = (Button) findViewById(R.id.buttonShowLocations);
        buttonShowLocations.setOnClickListener(this);

        // textViewAppend = new TextView(this);
        textViewAppend.setText("JJJ");
        textViewAppend.setTextSize(8);
        textViewAppend.setTextColor(Color.GREEN);
        // scrollViewBottom.setBackgroundColor(Color.rgb(200,222,200));
        // scrollViewBottom.addView(textViewAppend);

        //textViewOnChanged= new TextView(this);
        textViewOnChanged.setTextColor(Color.MAGENTA);
        textViewOnChanged.setTextSize(9);
        // scrollViewTop.addView(textViewOnChanged);
        scrollViewTop.setBackgroundColor(Color.LTGRAY);
        //scrollViewBottom.addView(textViewAppend);
        //scrollViewBottom.addView(textView2);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        ShowLocation(textViewAppend, true);

    }

    private void ShowLocation(TextView textView, boolean append) {
        //locationManager.getLastKnownLocation()

        for (String udbyder : locationManager.getAllProviders()) {
            LocationProvider lp = locationManager.getProvider(udbyder);

            locationManager.requestLocationUpdates(udbyder, 60000, 20, this);

            Location sted = locationManager.getLastKnownLocation(udbyder);

            textView.setTextColor(Color.BLUE);
            String text = udbyder + " - tændt: " + locationManager.isProviderEnabled(udbyder)
                    + "\n præcision=" + lp.getAccuracy() + " strømforbrug=" + lp.getPowerRequirement()
                    + "\n kræver satellit=" + lp.requiresSatellite() + " kræver net=" + lp.requiresNetwork()
                    + "\n sted=" + sted + "\n\n";
            if(append)
                textView.append(text);
            else
                textView.setText(text);


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
                GetAndShowLocationJakob(textView);
            }


        }
    }

    @Override
    public void onClick(View v) {

        if(v==buttonUpdate)
            ShowLocation(textViewAppend, true);
        if(v==buttonShowLocations)
        {
            textViewAppend.setText("All positions:\n");
            textViewAppend.setTextColor(Color.BLUE);
            final int size = locationArrayList.size();
            for (int i = 0; i < size; i++)
            {
                Date date = new Date(locationArrayList.get(i).getTime());
                DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
                String dateFormatted = formatter.format(date);

                textViewAppend.append("" + locationArrayList.get(i) + "\n");
                textViewAppend.append("" + locationArrayList.get(i).getSpeed() + ", " + dateFormatted   + "\n");
                textViewAppend.append("" + locationArrayList.get(i).getLatitude() + ", " + locationArrayList.get(i).getLatitude() + "\n");

            }
        }
    }




    @Override
    protected void onResume() {
        super.onResume();

        textViewAppend.append("in onResume...");
        GetAndShowLocationJakob(textViewAppend);
    }

    private void GetAndShowLocationJakob(TextView textView) {
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
                    textView.append("NÆRMESTE ADRESSE:::: " + adresse + "\n\n");
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

        saveKoordinates(sted);

        textViewAppend.append("in onLocationChanged:");
        // orig:
        textViewAppend.append(sted + "\n");
        scrollViewBottom.scrollTo(0, textViewAppend.getHeight()); // rul ned i bunden

        // jans:
        textViewOnChanged.setTextColor(Color.MAGENTA);
        textViewOnChanged.setText(sted + "\n");

    }

    public void saveKoordinates(Location location)
    {
        locationArrayList.add(location);
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
