package edu.fsu.cs.nuclear;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    public GoogleMap mGoogleMap;
    private LocationManager locationManager;
    public MapView mv;
    private int zone1, zone2, zone3, zone4, zoneFlag, country, addressFlag;
    private double lat, longi, TLat, TLong, zoneDist;
    private String address;
    private Button button1;
    private Button button2;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        SharedPreferences prefs = getActivity().getSharedPreferences("nukeprefs", 0);
        country = prefs.getInt("enemycode", -1);
        addressFlag = prefs.getInt("location_flag", -1);
        address = prefs.getString("address", null);
        //Toast.makeText(getActivity(), String.valueOf(addressFlag) + " " + country + " " + address, Toast.LENGTH_SHORT).show();

        if (addressFlag != 0) {
            getAddressLocation(address);
        } else {
            getLocation(); //gets lat and long from current loc
        }

        prefs.getString("lat", Double.toString(lat));
        prefs.getString("long", Double.toString(longi));


        //set bomb radii based on country flag
        findTargets();
        Log.i("MAP", lat+" "+longi+" vs "+TLat+" "+TLong);
        //Toast.makeText(getActivity(), "Tlat: "+TLat+" Tlong: "+TLong, Toast.LENGTH_LONG).show();
        zoneDist = DistanceCalculator.checkDistance(TLat, TLong, lat, longi)*(1609.34);
        if(zoneDist <= zone1){ //fireball
            zoneFlag=4;
        }
        else if(zoneDist <= zone2){//
            zoneFlag=3;
        }
        else if(zoneDist <= zone3){
            zoneFlag=2;
        }
        else if(zoneDist <= zone4){
            zoneFlag=1; //within radiation zone
        }
        else{
            zoneFlag=0; //safe or Fallout zone
        }
        //Toast.makeText(getActivity(), "Zone flag is: "+zoneFlag, Toast.LENGTH_SHORT).show();
        prefs.edit().putInt("zone_flag",zoneFlag).apply();


        mv = (MapView) view.findViewById(R.id.map);
        mv.onCreate(savedInstanceState);
        mv.getMapAsync(this);


        Button shelterButton = (Button) view.findViewById(R.id.needs);
        shelterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InfoFragment info = new InfoFragment();
                String tag = InfoFragment.class.getCanonicalName();
                getFragmentManager().beginTransaction().replace(R.id.fragContainer, info, tag).commit();
            }
        });

        button2 = view.findViewById(R.id.exit_button1);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go to fragment
                VideoFragment vFrag = new VideoFragment();
                String tag = VideoFragment.class.getCanonicalName();
                getFragmentManager().beginTransaction().replace(R.id.fragContainer, vFrag, tag).commit();
            }
        });

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(lat, longi)));
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, longi), 12));
        addCircles(googleMap, TLat, TLong);
    }

    public void addCircles(GoogleMap googleMap, double la, double lo) {
        mGoogleMap = googleMap;
        Circle circle1 = mGoogleMap.addCircle(new CircleOptions() //https://developers.google.com/maps/documentation/android-sdk/shapes
                .center(new LatLng(la, lo))
                .radius(zone4) //2.87 km
                .strokeWidth(1)
                .fillColor(Color.argb(50, 255, 222, 57))
                .zIndex(0)
        );
        //radiation radius
        Circle circle2 = mGoogleMap.addCircle(new CircleOptions()
                .center(new LatLng(la, lo))
                .radius(zone3) //1.64 km
                .strokeWidth(1)
                .fillColor(Color.argb(50, 255, 189, 57))
                .zIndex(1)
        );
        // thermal/blast 1
        Circle circle3 = mGoogleMap.addCircle(new CircleOptions()
                .center(new LatLng(la, lo))
                .radius(zone2)
                .strokeWidth(1)
                .fillColor(Color.argb(60, 235, 123, 24))
                .zIndex(2)
        );
        //fireball
        Circle circle4 = mGoogleMap.addCircle(new CircleOptions()
                .center(new LatLng(la, lo))
                .radius(zone1) //
                .strokeWidth(1)
                .fillColor(Color.argb(70, 255, 57, 57))
                .zIndex(3)
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        mv.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mv.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mv.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mv.onSaveInstanceState(outState);
    }

    public void getLocation() { //https://stackoverflow.com/questions/843675/how-do-i-find-out-if-the-gps-of-an-android-device-is-enabled
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) { //gps not enabled then let user enable it
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Please enable GPS location")
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        alert.dismiss();
                    }
                }
            });
        }
        Log.i("MAP", "4");
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.i("MAP", "6");
            long time = 1;
            float dist = 1;
            while (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, 123);
            }
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location != null){
                lat = location.getLatitude();
                longi = location.getLongitude();
            }else{
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, time, dist, mLocationListener);
            }

        }
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            lat = location.getLatitude();
            longi = location.getLongitude();
            Log.i("LATLONG", lat+" "+longi);
        }
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }
        @Override
        public void onProviderEnabled(String s) {
        }
        @Override
        public void onProviderDisabled(String s) {
        }
    };


    public void getAddressLocation(String strAddress){ //https://stackoverflow.com/questions/17835426/get-latitude-longitude-from-address-in-android
        Geocoder gc = new Geocoder(getActivity());
        List<Address> add;
        try {
            add = gc.getFromLocationName(strAddress, 5);

            if(add == null){
                return;
            }
            Address location = add.get(0);
            lat = location.getLatitude();
            longi = location.getLongitude();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void findTargets(){
        //0-9 all enemies, 10-99 china/rus, 100+ only rus
        int[] ids = getResources().getIntArray(R.array.ids);
        String[] lats = getResources().getStringArray(R.array.latitudes);
        String[] longs = getResources().getStringArray(R.array.longitudes);
        boolean first = true;
        if(country == 1){ //korea 50
            zone1 = 390;
            zone2 = 800;
            zone3 = 1640;
            zone4 = 2870;

            for(int i=0; i<ids.length; i++){
                if(ids[i]<=9 && ids[i]>=0){
                    if(first){
                        first = false;
                        TLat = Double.parseDouble(lats[i]);
                        TLong = Double.parseDouble(longs[i]);
                    }
                    else{
                        if(DistanceCalculator.checkDistance(TLat, TLong, lat, longi) >=
                                DistanceCalculator.checkDistance(Double.parseDouble(lats[i]), Double.parseDouble(longs[i]), lat, longi)){
                            TLat = Double.parseDouble(lats[i]);
                            TLong = Double.parseDouble(longs[i]);
                        }
                    }
                }
            }

        }
        else if(country == 2){ //russia 550 kt
            zone1 = 950;
            zone2 = 1730;
            zone3 = 3630;
            zone4 = 7910;
            for(int i=0; i<ids.length; i++){
                if(ids[i]>=0){
                    if(first){
                        first = false;
                        TLat = Double.parseDouble(lats[i]);
                        TLong = Double.parseDouble(longs[i]);
                    }
                    else{
                        if(DistanceCalculator.checkDistance(TLat, TLong, lat, longi) >=
                                DistanceCalculator.checkDistance(Double.parseDouble(lats[i]), Double.parseDouble(longs[i]), lat, longi)){
                            TLat = Double.parseDouble(lats[i]);
                            TLong = Double.parseDouble(longs[i]);
                        }
                    }
                }
            }
        }
        else if(country == 3){ //china 5
            zone1 = 2380;
            zone2 = 3720;
            zone3 = 3050;
            zone4 = 21300;
            for(int i=0; i<ids.length; i++){
                if(ids[i]<=99 && ids[i]>=0){
                    if(first){
                        first = false;
                        TLat = Double.parseDouble(lats[i]);
                        TLong = Double.parseDouble(longs[i]);
                    }
                    else{
                        if(DistanceCalculator.checkDistance(TLat, TLong, lat, longi) >=
                                DistanceCalculator.checkDistance(Double.parseDouble(lats[i]), Double.parseDouble(longs[i]), lat, longi)){
                            TLat = Double.parseDouble(lats[i]);
                            TLong = Double.parseDouble(longs[i]);
                        }
                    }
                }
            }
        }
    }
}

