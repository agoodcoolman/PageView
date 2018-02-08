package com.example.testaudio;

import android.location.Location;
import android.location.LocationManager;

/**
 * Created by jin on 2018/1/26.
 */

public class X {
    public static boolean MODE = false;

    X() {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLongitude(116.43129);
        location.setLatitude(39.946583);
    }

}
