package com.twtech.fleetviewapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.Iterator;

public class CaptureLocation {

    private LocationManager locationManager = null;
    private LocationListener locationListener = null;
    private GpsStatus.NmeaListener nmeaListener = null;
    private GpsStatus.Listener gpsStatusListener = null;
    int locupdMinTime, locupdMinDist;
    GlobalVariable globalVariable;
    CanDatabase canDatabase;
    int incidentLimit;

    //GlobalVariable globalVariable;
    // String pSpeed;

    Context mContext;
    DatabaseOperation databaseOperation;

    // Handler mHandler;
    // Thread thread;

    public CaptureLocation(Context mContext) {
        this.mContext = mContext;
        globalVariable = (GlobalVariable) mContext.getApplicationContext();
        databaseOperation = new DatabaseOperation(mContext);

        canDatabase = new CanDatabase(mContext);
        canDatabase.openCanDatabase();
        String incidentlimit = canDatabase.getValue("IncidentLimit");
        //incidentLimit = Integer.parseInt(canDatabase.getValue("IncidentLimit"));
        // new MyLogger().storeMassage("IncidentLimit is",""+incidentLimit);
        incidentLimit = Integer.parseInt(incidentlimit);
        locupdMinTime = Integer.parseInt(canDatabase.getValue("LocUpdMinTime"));
        locupdMinDist = Integer.parseInt(canDatabase.getValue("LocUpdMinDist"));
        canDatabase.closeCanDatabase();
        // locupdMinTime= Integer.parseInt(locMintime);
        // locupdMinDist= Integer.parseInt(locMinDist);
       // new MyLogger().storeMassage("locupdMinTime", +locupdMinTime + "locupdMinDist" + locupdMinDist);

        //setupGpsListener(mContext);
        getData(mContext);
        registerListener();

    }

    public void getData(final Context context) {

        Log.e("CaptureLocation", "setupGpsListener method Called");
       // new MyLogger().storeMassage("CaptureLocation", "setupGpsListener method Called");
        // registerHandler();
        registerListener();
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,locupdMinTime,locupdMinDist, locationListener);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.addNmeaListener(nmeaListener);
        //locationManager.removeUpdates(locationListener);
        //locationManager.removeNmeaListener(nmeaListener);
    }

    private void registerListener() {
        //new MyLogger().storeMassage("registerListener", "Method called");

        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location loc) {
                // TODO Auto-generated method stub

                Log.d("GPS-NMEA", loc.getLatitude() + "," + loc.getLongitude());
               // new MyLogger().storeMassage("On Location Changed"," Latitude : "+loc.getLatitude() + " Longitude : "+loc.getLongitude());
               // Log.e("On Location Changed"," Latitude : "+loc.getLatitude() + " Longitude : "+loc.getLongitude());
            }

            @Override
            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // TODO Auto-generated method stub
                Log.d("GPS-NMEA", provider + "");

                switch (status) {
                    case LocationProvider.OUT_OF_SERVICE:
                        Log.d("GPS-NMEA", "OUT_OF_SERVICE");
                        break;
                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        Log.d("GPS-NMEA", " TEMPORARILY_UNAVAILABLE");
                        break;
                    case LocationProvider.AVAILABLE:
                        Log.d("GPS-NMEA", "" + provider + "");

                        break;
                }
            }
        };

        nmeaListener = new GpsStatus.NmeaListener() {

            public void onNmeaReceived(long timestamp, String nmea) {
                // new MyLogger().storeMassage("timestamp", "nmea");
                //check nmea's checksum

                   // Log.e("NMEA String"," : "+nmea);
                    if (nmea.contains("$GPRMC")||nmea.contains("$GNRMC")) {

                       Log.e("Nmea", "String : "+nmea);
                       // new MyLogger().storeMassage("nmea ",": "+nmea);

                        globalVariable.setStringGPRMC(nmea);
                       // new MyLogger().storeMassage("GPRMC String ",""+nmea);
                      //  Log.e("GPRMC",""+nmea);
                        String[] NmeaStringArray = nmea.split(",");
                        String StrCurrentSpeed = NmeaStringArray[7];

                        Float FloatCurrentSpeed = Float.valueOf(0);
                        try {
                            float f = Float.parseFloat(StrCurrentSpeed);
                            FloatCurrentSpeed = (float) (f * 1.852);
                        } catch (Exception e) {
                           // Log.e("capturespeedException",""+e);
                        }
                    try {
                        if (FloatCurrentSpeed >= incidentLimit) {
                            databaseOperation.storeIncidentData(nmea);
                        }
                    }catch(Exception e){
                            Log.e("storingIncidentExceptio",""+e);
                        }
                }

                gpsStatusListener = new GpsStatus.Listener()
                {
                    public void onGpsStatusChanged(int event) {
                        // TODO Auto-generated method stub
                        GpsStatus gpsStatus;
                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        gpsStatus = locationManager.getGpsStatus(null);

                        switch (event) {
                            case GpsStatus.GPS_EVENT_FIRST_FIX:
                                //
                                gpsStatus.getTimeToFirstFix();
                                Log.d("GPS-NMEA", "GPS_EVENT_FIRST_FIX");
                                break;
                            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:

                                Iterable<GpsSatellite> allSatellites = gpsStatus.getSatellites();
                                Iterator<GpsSatellite> it = allSatellites.iterator();

                                int count = 0;
                                while (it.hasNext()) {
                                    GpsSatellite gsl = (GpsSatellite) it.next();

                                    if (gsl.getSnr() > 0.0) {
                                        count++;
                                    }
                                }
                                break;
                            case GpsStatus.GPS_EVENT_STARTED:
                                //Event sent when the GPS system has started.
                                Log.d("GPS-NMEA", "GPS_EVENT_STARTED");
                                break;
                            case GpsStatus.GPS_EVENT_STOPPED:
                                //Event sent when the GPS system has stopped.
                                Log.d("GPS-NMEA", "GPS_EVENT_STOPPED");
                                break;
                            default:
                                break;
                        }
                    }
                };
            }
        };
    }
}














