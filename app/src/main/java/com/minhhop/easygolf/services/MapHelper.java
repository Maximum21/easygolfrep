package com.minhhop.easygolf.services;

import android.location.Location;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polygon;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;
import com.minhhop.easygolf.utils.AppUtil;

import java.util.List;

public class MapHelper {

    private static final double UNIT_YARD = 1.0936133;

    public static double getDistanceLatLng(LatLng pointA, LatLng pointB, boolean toYard) {
        double holder = SphericalUtil.computeDistanceBetween(pointA, pointB);
        return toYard? holder * UNIT_YARD: holder;
    }



    public static LatLng computeCentroid(LatLng pointA, LatLng pointB) {
        return SphericalUtil.interpolate(pointA,pointB,0.5);
    }

    public static int convertMeterToYard(int value){
        return (int) Math.round(value * UNIT_YARD);
    }

    public static int convertYardToMeter(int value){
       return (int) Math.round(value / UNIT_YARD);
    }


    public static boolean isInHolderPolygon(LatLng target,Polygon point){

        return PolyUtil.containsLocation(target,point.getPoints(),true);

    }


    public static LatLng getMinPoint(List<LatLng> points,LatLng pointA){

        int positionMin = 0;
        double distanceMin = getDistanceLatLng(points.get(0),pointA,false);
        int size = points.size();
        for (int i = 1;i < size; i++){
            double distanceCurrent = getDistanceLatLng(points.get(i),pointA,false);
            if(distanceMin > distanceCurrent){
                distanceMin = distanceCurrent;
                positionMin = i;
            }
        }
        return points.get(positionMin);
    }


    public static long convertMeterToYard(double meter){
        return  Math.round(meter * UNIT_YARD);

    }



    public static LatLng getDestinationPoint(LatLng source, double bearing, double dist) {

        double EARTH_RADIUS = AppUtil.EARTH_RADIUS/1000;
        dist = dist / EARTH_RADIUS;
        bearing = Math.toRadians(bearing);

        double lat1 = Math.toRadians(source.latitude), long1 = Math.toRadians(source.longitude);
        double lat2 = Math.asin(Math.sin(lat1) * Math.cos(dist) +
                Math.cos(lat1) * Math.sin(dist) * Math.cos(bearing));
        double lon2 = long1 + Math.atan2(Math.sin(bearing) * Math.sin(dist) *
                        Math.cos(lat1),
                Math.cos(dist) - Math.sin(lat1) *
                        Math.sin(lat2));
        if (Double.isNaN(lat2) || Double.isNaN(lon2)) {
            return null;
        }
        return new LatLng(Math.toDegrees(lat2), Math.toDegrees(lon2));
    }

    public static float getBearingFromLocation(LatLng lat1,LatLng lat2){

        Location startingLocation = new Location("starting point");
        startingLocation.setLatitude(lat1.latitude);
        startingLocation.setLongitude(lat1.longitude);

        //Get the target location
        Location endingLocation = new Location("ending point");
        endingLocation.setLatitude(lat2.latitude);
        endingLocation.setLongitude(lat2.longitude);

        //Find the Bearing from current location to next location
        return startingLocation.bearingTo(endingLocation);

    }


    private static final double LN2 = 0.6931471805599453;
    private static final int WORLD_PX_HEIGHT = 256;
    private static final int WORLD_PX_WIDTH = 256;
    private static final int ZOOM_MAX = 20;

    public static int getBoundsZoomLevel(LatLngBounds bounds, View viewRoot){

        int mapWidthPx = viewRoot.getMeasuredWidth();
        int mapHeightPx = viewRoot.getMeasuredHeight();

        LatLng ne = bounds.northeast;
        LatLng sw = bounds.southwest;

        double latFraction = (latRad(ne.latitude) - latRad(sw.latitude)) / Math.PI;

        double lngDiff = ne.longitude - sw.longitude;
        double lngFraction = ((lngDiff < 0) ? (lngDiff + 360) : lngDiff) / 360;

        double latZoom = zoom(mapHeightPx, WORLD_PX_HEIGHT, latFraction);
        double lngZoom = zoom(mapWidthPx, WORLD_PX_WIDTH, lngFraction);

        int result = Math.min((int)latZoom, (int)lngZoom);
        return Math.min(result, ZOOM_MAX);
    }

    private static double latRad(double lat) {
        double sin = Math.sin(lat * Math.PI / 180);
        double radX2 = Math.log((1 + sin) / (1 - sin)) / 2;
        return Math.max(Math.min(radX2, Math.PI), -Math.PI) / 2;
    }
    private static double zoom(int mapPx, int worldPx, double fraction) {
        return Math.floor(Math.log(mapPx / worldPx / fraction) / LN2);
    }

}
