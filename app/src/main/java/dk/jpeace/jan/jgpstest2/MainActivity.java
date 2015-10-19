package dk.jpeace.jan.jgpstest2;

import android.content.Context;
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
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Button;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {

    private LocationManager locationManager;
    Button buttonUpdate, buttonShowLocations, buttonSend;

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
        buttonSend = (Button) findViewById(R.id.buttonSend);
        buttonSend.setOnClickListener(this);

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

            // locationManager.requestLocationUpdates(udbyder, 60000, 20, this);
            // Ændret til hvert 10. sekund eller 2 meter... For at få oftere ændringer.
            locationManager.requestLocationUpdates(udbyder, 2000, 1, this);

            Location sted = locationManager.getLastKnownLocation(udbyder);

            textView.setTextColor(Color.BLACK);
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
            ShowAllLocations();
        }
        if(v==buttonSend)
        {
            SendAllLocations();
        }
    }

    private void SendAllLocations() {
        String allLocations = GetAllLocations();
        SaveToFile(allLocations);

        textViewAppend.append(getFilesDir().getPath());
        SendEmailIntent(allLocations);
    }

    /*
      Her vil jeg lave en oversigt over alle opsamlede Locations.

     */
    private void ShowAllLocations() {
        textViewAppend.setText("All positions:\n");
        textViewAppend.setTextColor(Color.BLUE);
        //PrintLocation(locationArrayList);
        final int size = locationArrayList.size();
        for (int i = 0; i < size; i++)
        {
            Date date = new Date(locationArrayList.get(i).getTime());
            DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
            String dateFormatted = formatter.format(date);

            // textViewAppend.append("" + locationArrayList.get(i) + "\n");
            textViewAppend.append("" + locationArrayList.get(i).getSpeed() + ", " + dateFormatted);
            textViewAppend.append(" ::: " + TryGetLocationAddress(locationArrayList.get(i)));
            textViewAppend.append("\n");
            textViewAppend.append("" + locationArrayList.get(i).getLatitude() + ", " + locationArrayList.get(i).getLatitude() + "\n");
            textViewAppend.append(" - - - - - - - - - - - ");
            textViewAppend.append("\n");
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
        //locationManager.requestLocationUpdates(udbyder, 60000, 20, this);
        locationManager.requestLocationUpdates(udbyder, 2000, 1, this);

        Location sted = locationManager.getLastKnownLocation(udbyder);

        String address = TryGetLocationAddress(sted);
        textView.append(address);
    }

    /*
    private String TryGetLocationAddressToTextView(Location sted) {
        if (sted != null) { // forsøg at finde nærmeste adresse
            try {
                Geocoder geocoder = new Geocoder(this);
                List<Address> adresser = geocoder.getFromLocation(sted.getLatitude(), sted.getLongitude(), 1);
                String adresseTekst = getAdresseTekst(adresser);
                return adresseTekst;

            } catch (IOException ex) {

                ex.printStackTrace();
            }
        }
        return "Location N/A.";
    }
    */

    private String getAdresseTekst(List<Address> adresser) {
        String adresseTekst = "";
        if (adresser != null && adresser.size() > 0) {
            Address adresse = adresser.get(0);
            adresseTekst = "::::NÆRMESTE ADRESSE:::: " + adresse + "\n\n";
        }
        return adresseTekst;
    }


    private String TryGetLocationAddress(Location sted) {
        String adresseTekst = "Location N/A.";

        if (sted != null) { // forsøg at finde nærmeste adresse
            try {
                Geocoder geocoder = new Geocoder(this);
                List<Address> adresser = geocoder.getFromLocation(sted.getLatitude(), sted.getLongitude(), 1);
                adresseTekst = getAdresseTekst(adresser);
                return adresseTekst;
                //textView.append(adresseTekst);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return adresseTekst;
    }

    private String getFirstAdresse(List<Address> adresser) {
        String adresseTekst = "";
        if (adresser != null && adresser.size() > 0) {
            Address adresse = adresser.get(0);
            adresseTekst = adresse.toString();
        }
        return adresseTekst;
    }


    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);

        String allLocations = GetAllLocations();
        SaveToFile(allLocations);
    }

    // Metode specificeret i LocationListener
    public void onLocationChanged(Location location) {

        saveKoordinates(location);

        textViewAppend.append("in onLocationChanged:");
        // orig:
        textViewAppend.append(location + "\n");
        scrollViewBottom.scrollTo(0, textViewAppend.getHeight()); // rul ned i bunden

        // jans:
        textViewOnChanged.setTextColor(Color.MAGENTA);
        textViewOnChanged.setText(location + "\n");

    }

    public void saveKoordinates(Location location)
    {
        locationArrayList.add(location);
    }


    public void PrintLocations()
    {
        final int size = locationArrayList.size();
        for (int i = 0; i < size; i++)
        {
            Date date = new Date(locationArrayList.get(i).getTime());
            DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
            String dateFormatted = formatter.format(date);

            textViewAppend.append("" + locationArrayList.get(i) + "\n");
            textViewAppend.append("\n");

            textViewAppend.append("" + locationArrayList.get(i).getSpeed() + ", " + dateFormatted   + "\n");
            String latlon = "" + locationArrayList.get(i).getLatitude() + ", " + locationArrayList.get(i).getLatitude() + "\n";
            textViewAppend.append(latlon);
            textViewAppend.append("\n");


            String content = dateFormatted + ", " + latlon;
            Log.d("JJ", content);
            // SaveToFile(content);
        }
    }

    public String GetAllLocations()
    {
        String content = "Gps locations - Test from jGPS \n";
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SSS");

        final int size = locationArrayList.size();
        for (int i = 0; i < size; i++)
        {
            Date date = new Date(locationArrayList.get(i).getTime());
            String locationDate = dateFormat.format(date);
            String latlonKoordinates = "" + locationArrayList.get(i).getLatitude() + ", " + locationArrayList.get(i).getLatitude();

            content += locationDate + ", " + latlonKoordinates;
            content += "\n";
            Log.d("JJJ", content);
        }
        return content;
    }


    public void SaveToFile(String content)
    {
        //
        // Se evt.
        //  D:\jan\SourceGit\Android\android-eksempler\AndroidElementer\app\src\main\java\dk\nordfalk\aktivitetsliste\Aktivitetsdata.java
        //      final File cachefil = new File(app.getCacheDir(), "Aktivitetslistecache.ser");
        //          objektstrøm = new ObjectInputStream(new FileInputStream(cachefil));
        // .


        String FILENAME = "hello_file.txt";
        //String string = "hello world!";

        FileOutputStream fos = null;
        try {
            fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
        }
        catch (FileNotFoundException exception)
        {
            Log.d("jj", "SaveFile Failed on openFileOutput... " + exception.getMessage() + "<n getStackTrace::" + exception.getStackTrace());
            exception.printStackTrace();
        }
        try {
            fos.write(content.getBytes());
        } catch (IOException exception) {
                Log.d("jj", "SaveFile Failed on write... " + exception.getMessage() + "<n getStackTrace::" + exception.getStackTrace());
                exception.printStackTrace();
        }
        try {
            fos.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

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


    public void SendEmailIntent(String content)
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_EMAIL, "jma73@hotmail.com");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Jan");
        intent.putExtra(Intent.EXTRA_TEXT, "I'm email body. Jan.\n\n\nGPS data\n\n\n" + content );
        //intent.putExtra(Intent.ACTION_ATTACH_DATA, "I'm email body. Jan." + content );

        startActivity(Intent.createChooser(intent, "Send Email"));

    }


}
