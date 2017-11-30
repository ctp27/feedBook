package com.sdpm.feedly.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by clinton on 11/25/17.
 */

public class FeedlyLocationListener implements LocationListener {

    private static final String TAG = FeedlyLocationListener.class.getSimpleName();
    private static final int TWO_MINUTES = 1000 * 60 * 2;

    private static  Map<String, String> states;


    private LocationChangedListener theListener;
    private Context context;
    private LocationManager locationManager;


    public interface LocationChangedListener {
        void onFoundCurrentLocation(String location);
    }

    public FeedlyLocationListener(Context context, LocationManager locationManager, LocationChangedListener theListener) {
        this.context = context;
        this.theListener = theListener;
        this.locationManager = locationManager;
        populateHashMap();

    }

    @Override
    public void onLocationChanged(Location location) {
        String address = null;
        Location lastKnownLocation = getLastLocation();
        if(isBetterLocation(location,lastKnownLocation)){

            try {
                address = getAddressFromLocation(location);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                address = getAddressFromLocation(lastKnownLocation);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG,"Not a better location");
        }
        if(address !=null) {
            theListener.onFoundCurrentLocation(address);
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }



    private String getAddressFromLocation(Location location) throws IOException{
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
       String locality = null;

        List<Address> addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        if (addresses.size() > 0) {
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            if(states.containsKey(state)){
                state = states.get(state);
                locality = city+","+state.replaceAll("\\s+","");
            }
            else {
                locality = city;
            }

            Log.d(TAG,locality);
        }
        else {
            Log.d(TAG,"In Here");
        }

        return locality;
    }

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param lastBestLocation  The current Location fix, to which you want to compare the new one
     */
    private boolean isBetterLocation(Location location, Location lastBestLocation) {
        if (lastBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - lastBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - lastBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                lastBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    private Location getLastLocation() throws SecurityException{
        Location tempLastNetworkLocation=null;
        Location tempLastGpsLocation=null;

        tempLastNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        tempLastGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if(tempLastGpsLocation == null && tempLastNetworkLocation==null){
            return  null;
        }
        else{
            if(tempLastNetworkLocation==null){
                return tempLastGpsLocation;
            }
            else if(tempLastGpsLocation == null){
                return tempLastNetworkLocation;
            }
            else{
                if(isBetterLocation(tempLastGpsLocation,tempLastGpsLocation)){
                    return tempLastGpsLocation;
                }
                else {
                    return tempLastNetworkLocation;
                }
            }
        }
    }

    private void populateHashMap(){

        states = new HashMap<String, String>();
        states.put("Alabama","AL");
        states.put("Alaska","AK");
        states.put("Alberta","AB");
        states.put("American Samoa","AS");
        states.put("Arizona","AZ");
        states.put("Arkansas","AR");
        states.put("Armed Forces (AE)","AE");
        states.put("Armed Forces Americas","AA");
        states.put("Armed Forces Pacific","AP");
        states.put("British Columbia","BC");
        states.put("California","CA");
        states.put("Colorado","CO");
        states.put("Connecticut","CT");
        states.put("Delaware","DE");
        states.put("District Of Columbia","DC");
        states.put("Florida","FL");
        states.put("Georgia","GA");
        states.put("Guam","GU");
        states.put("Hawaii","HI");
        states.put("Idaho","ID");
        states.put("Illinois","IL");
        states.put("Indiana","IN");
        states.put("Iowa","IA");
        states.put("Kansas","KS");
        states.put("Kentucky","KY");
        states.put("Louisiana","LA");
        states.put("Maine","ME");
        states.put("Manitoba","MB");
        states.put("Maryland","MD");
        states.put("Massachusetts","MA");
        states.put("Michigan","MI");
        states.put("Minnesota","MN");
        states.put("Mississippi","MS");
        states.put("Missouri","MO");
        states.put("Montana","MT");
        states.put("Nebraska","NE");
        states.put("Nevada","NV");
        states.put("New Brunswick","NB");
        states.put("New Hampshire","NH");
        states.put("New Jersey","NJ");
        states.put("New Mexico","NM");
        states.put("New York","NY");
        states.put("Newfoundland","NF");
        states.put("North Carolina","NC");
        states.put("North Dakota","ND");
        states.put("Northwest Territories","NT");
        states.put("Nova Scotia","NS");
        states.put("Nunavut","NU");
        states.put("Ohio","OH");
        states.put("Oklahoma","OK");
        states.put("Ontario","ON");
        states.put("Oregon","OR");
        states.put("Pennsylvania","PA");
        states.put("Prince Edward Island","PE");
        states.put("Puerto Rico","PR");
        states.put("Quebec","QC");
        states.put("Rhode Island","RI");
        states.put("Saskatchewan","SK");
        states.put("South Carolina","SC");
        states.put("South Dakota","SD");
        states.put("Tennessee","TN");
        states.put("Texas","TX");
        states.put("Utah","UT");
        states.put("Vermont","VT");
        states.put("Virgin Islands","VI");
        states.put("Virginia","VA");
        states.put("Washington","WA");
        states.put("West Virginia","WV");
        states.put("Wisconsin","WI");
        states.put("Wyoming","WY");
        states.put("Yukon Territory","YT");
    }
}
