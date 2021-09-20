package com.minhhop.easygolf.listeners;

import android.location.Location;
import android.os.Bundle;

public interface GetLocation {
    void onLocationChanged(Location location);
    void onStatusChanged(String s, int i, Bundle bundle);
    void onProviderEnabled(String s);
    void onProviderDisabled(String s);
}