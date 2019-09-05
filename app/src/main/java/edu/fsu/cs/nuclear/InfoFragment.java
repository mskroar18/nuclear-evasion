package edu.fsu.cs.nuclear;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.app.Fragment;
import android.util.Pair;
import android.widget.TextView;

//import androidx.fragment.app.Fragment;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

public class InfoFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap gMap;
    private LocationManager locationManager;
    private MapView mv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)  {
        View v = inflater.inflate(R.layout.info_layout, container, false);

        SharedPreferences prefs = getActivity().getSharedPreferences("nukeprefs",0);

        // Gets the MapView from the XML layout and creates it
        mv = (MapView) v.findViewById(R.id.mapView);
        mv.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        mv.getMapAsync(this);

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }

// Updates the location and zoom of the MapView

//        gMap.animateCamera(cameraUpdate);

        int advTypt = prefs.getInt("enemycode", 0);
        int zonetype = prefs.getInt("zone_flag", 0);
        Button returntomapButton = (Button) v.findViewById(R.id.returntomap);
        returntomapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapFragment map = new MapFragment();
                String tag = MapFragment.class.getCanonicalName();
                getFragmentManager().beginTransaction().replace(R.id.fragContainer, map, tag).commit();
            }
        });


        setBodyText(zonetype, advTypt, v);
        return v;
    }

    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        gMap.getUiSettings().setZoomControlsEnabled(true);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            JSONObject jObject  = DistanceCalculator.locationFind();
            JSONArray jArray = jObject.getJSONArray("features");

            Pair<String, String>[] coordinates = new Pair[jArray.length()];

            for(int i = 0; i <= jArray.length(); i++){
                JSONObject jtemp = jArray.getJSONObject(i).getJSONObject("attributes");
                String LatTemp = jtemp.getString("LATITUDE");
                String LongTemp = jtemp.getString("LONGITUDE");
                String tempName = jtemp.getString("NAME");
                gMap.addMarker(new MarkerOptions().title(tempName).position(new LatLng(Double.parseDouble(LatTemp), Double.parseDouble(LongTemp))));


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        SharedPreferences prefs = getActivity().getSharedPreferences("nukeprefs",0);
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(30.450960, -84.315050), 7));
        mv.onResume();
    }



    @Override
    public void onResume() {
        super.onResume();
        mv.onResume();
    }



    private void setBodyText(int zone, int adv, View view) {

        //Need Zone result and adversary selection

        //switch on adversary to determine what info to display in bomb_type text view
        //switch on Zone, fill text views accordingly

        TextView bombtype = view.findViewById(R.id.bombType);
        String[] bomb_types = getResources().getStringArray(R.array.Bombs);
        switch (adv){
            //Bomb type selection switch
            //Russian
            case 2:
                bombtype.setText(bomb_types[0]);
                break;
            //Chinese
            case 3:
                bombtype.setText(bomb_types[1]);
                break;

            //Korean
            case 1:
                bombtype.setText(bomb_types[2]);
                break;
        }

        TextView effectsReport = view.findViewById(R.id.Effects_Report);
        TextView Zone = view.findViewById(R.id.header_text);

        String[] zones = getResources().getStringArray(R.array.Zones);
        String[] narratives = getResources().getStringArray(R.array.narratives);


        TextView suppliesReport = view.findViewById(R.id.Supplies_Report);

        switch(zone) {
            //Zone selection switch

            case 0:
                Zone.setText(zones[0]);
                effectsReport.setText(narratives[0]);
                break;
            case 4:
                Zone.setText(zones[1]);
                effectsReport.setText(narratives[1]);
                break;
            case 3:
                Zone.setText(zones[2]);
                effectsReport.setText(narratives[2]);
                break;
            case 2:
                Zone.setText(zones[3]);
                effectsReport.setText(narratives[3]);
                break;

            case 1:
                Zone.setText(zones[4]);
                effectsReport.setText(narratives[4]);
                break;

        }
        //Citation for html text formatting https://stackoverflow.com/questions/4992794/how-to-add-bulleted-list-to-android-application
        String nodata="Important supplies you should get: <br/>&#8226;Nonperishable foods and water\n <br/>&#8226; Potassium Iodine to prevent thyroid cancer\n <br/>&#8226;Decontamination supplies (soap, water, towelettes)\n<br/>&#8226;Disposable gloves\n<br/>&#8226;N95 masks\n<br/>&#8226;Radiation Detection card or device";
        suppliesReport.setText(Html.fromHtml(nodata));
    }

}