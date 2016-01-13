package com.example.robert.lab8;

        import android.Manifest;
        import android.app.Activity;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.location.Address;
        import android.location.Criteria;
        import android.location.Geocoder;
        import android.location.Location;
        import android.location.LocationListener;
        import android.location.LocationManager;
        import android.os.Bundle;
        import android.provider.Settings;
        import android.support.v4.content.ContextCompat;
        import android.util.Log;
        import android.view.View;
        import android.widget.TextView;

        import java.io.IOException;
        import java.util.List;
        import java.util.Locale;

public class ShowLocation extends Activity implements LocationListener {

    String address = "25 Belvale Ave";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_location);

        setupLocationServices();
    }

    private void setupLocationServices() {

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // request that the user install the GPS provider
            String locationConfig = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
            Intent enableGPS = new Intent(locationConfig);
            startActivity(enableGPS);
        } else {
            // determine the location
            updateLocation();
        }

    }

    /*
       Sample data:
         CN Tower:      43.6426, -79.3871
         Eiffel Tower:  48.8582,   2.2945
     */
    private void updateLocation() {
        if (Manifest.permission.ACCESS_FINE_LOCATION != "") {
            LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

            // request an fine location provider
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setSpeedRequired(false);
            criteria.setCostAllowed(false);
            String recommended = locationManager.getBestProvider(criteria, true);

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);

            Location location = locationManager.getLastKnownLocation(recommended);
            if (location != null) {
                showLocationName(location);
            }
        } else {
            Log.d("LocationSample", "Location provider permission denied, perms: " + ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION));
        }
    }

    final int PERMISSIONS_REQUEST_ACCESS_LOCATION = 410020;
    private void requestLocationPermissions() {


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateLocation();
                } else {
                    // tell the user that the feature will not work
                }
                return;
            }
        }
    }

    public void onProviderEnabled(String provider) {
        Log.d("LocationSample", "onProviderEnabled(" + provider + ")");
    }

    public void onProviderDisabled(String provider) {
        Log.d("LocationSample", "onProviderDisabled(" + provider + ")");
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("LocationSample", "onStatusChanged(" + provider + ", " + status + ", extras)");
    }

    public void onLocationChanged(Location location) {
        Log.d("LocationSample", "onLocationChanged(" + location + ")");

        showLocationName(location);
    }

    private void showLocationName(Location location) {
        Log.d("LocationSample", "showLocationName("+location+")");
        // perform a reverse geocode to get the address
        if (Geocoder.isPresent()) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());

            try {
                // reverse geocode from current GPS position
                List<Address> results = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                if (results.size() > 0) {
                    Address match = results.get(0);
                    String address1 = match.getAddressLine(0);
                    String address2 = match.getAddressLine(1);
                    String locality = match.getLocality();
                    String adminArea = match.getAdminArea();
                    String country = match.getCountryName();
                    String postal = match.getPostalCode();
                    String phone = match.getPhone();
                    String url = match.getUrl();

                    updateText(address1,address2,locality,adminArea,country,postal,phone,url);

                } else {
                    Log.d("LocationSample", "No results found while reverse geocoding GPS location");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("LocationSample", "No geocoder present");
        }
    }

    private void updateText(String address1, String address2, String locality, String adminArea, String country, String postal, String phone, String url) {

        TextView adr1 = (TextView)findViewById(R.id.adr1);
        TextView adr2 = (TextView)findViewById(R.id.adr2);
        TextView city = (TextView)findViewById(R.id.city);
        TextView prov = (TextView)findViewById(R.id.province);
        TextView cnty = (TextView)findViewById(R.id.country);
        TextView post = (TextView)findViewById(R.id.postal);
        TextView phon = (TextView)findViewById(R.id.phone);
        TextView urls = (TextView)findViewById(R.id.url);

        adr1.setText(address1);
        adr2.setText(address2);
        city.setText(locality);
        prov.setText(adminArea);
        cnty.setText(country);
        post.setText(postal);
        phon.setText(phone);
        urls.setText(url);

    }

    private Address geocodeLookup(String address) {
        if (Geocoder.isPresent()) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());

            try {
                // forward geocode from the provided address
                List<Address> results = geocoder.getFromLocationName(address, 1);

                if (results.size() > 0) {
                    return results.get(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void search(View v) {
        Address address = geocodeLookup(this.address);

        Intent showMapIntent = new Intent(this, MapsActivity.class);

        showMapIntent.putExtra("location", this.address);
        if (address != null) {
            showMapIntent.putExtra("latitude", address.getLatitude());
            showMapIntent.putExtra("longitude", address.getLongitude());
        }
        startActivity(showMapIntent);
    }

    public void updateLocation(View view) {
        updateLocation();
    }
}
