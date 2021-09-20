package com.minhhop.easygolf.services;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class CurrentLocation implements LocationListener {

    Context context;
    LocationManager locationManager;
    String provider;


    public CurrentLocation(Context context) {

        this.context = context;
        location();
    }

    public void location() {
        // Getting EasyGolfLocation object
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // anruag getting last location
        //  Location location = locationManager.getLastKnownLocation(EasyGolfLocation.GPS_PROVIDER);

        // Creating an empty criteria object
        Criteria criteria = new Criteria();

        // Getting the name of the provider that meets the criteria
        provider = locationManager.getBestProvider(criteria, true);

        if (provider != null && !provider.equals(" ")) {

            // Get the location from the given provider
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            Location location = locationManager.getLastKnownLocation(provider);

            locationManager.requestLocationUpdates(provider, 20000, 1, this);

            if (location != null)
                onLocationChanged(location);
            else {

            }
             Toast.makeText(context, "Location can't be retrieved", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(context, "No Provider Found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        // Log.e("Location", location.getProvider() + "==" + location.getAccuracy() + "==" + location.getAltitude() + "==" + location.getLatitude() + "==" + location.getLongitude());
        String message = String.format(
                "New Location \n Longitude: %1$s \n Latitude: %2$s",
                location.getLongitude(), location.getLatitude());

        Log.e("WOW",message);
//        ConstantValues.UPlat = String.valueOf(location.getLatitude());
//        ConstantValues.UPlng = String.valueOf(location.getLongitude());

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.e("onStatusChanged", "==" + s);
        Log.e("WOW","==" + s);
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.e("onProviderEnabled", "==" + s);
        Log.e("WOW","==" + s);
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.e("onProviderDisabled", "==" + s);
        Log.e("WOW","==" + s);
        // alertbox("GPS STATUS", "Your GPS is: OFF");
        // Toast.makeText(context, "Please turn on the GPS to get current location.", Toast.LENGTH_SHORT).show();

        try {

//            ConstantValues.showDialogOK("Please turn on the GPS to get current location.", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    switch (i) {
//                        case DialogInterface.BUTTON_POSITIVE:
//                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                            context.startActivity(myIntent);
//                            dialogInterface.dismiss();
//                            break;
//                        case DialogInterface.BUTTON_NEGATIVE:
//                            dialogInterface.dismiss();
//                            break;
//                    }
//                }
//            }, context);
        } catch (Exception e) {
            Log.e("exception", e.toString()+"==");
        }

    }

}