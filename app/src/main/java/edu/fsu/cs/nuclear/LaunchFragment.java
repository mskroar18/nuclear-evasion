package edu.fsu.cs.nuclear;
/*
shared prefrence notes :  getSharedPreferences
name of preferences = nukeprefs
"location_flag" is an int.  1 means address will be entered. 0 is current location
"address" is a string.  will be "" if location_flag== 0, otherwise stores string as one line
like 341 Sharer Rd. Tallahassee 32305  ## no state
"enemycode" is an int.  1= north korea, 2= russia, 3 = china. otherwise 0
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class LaunchFragment extends Fragment {

    //private OnFragmentInteractionListener mListener;

    public LaunchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_launch, container, false);

        Button buttonselectcurrent = (Button) rootView.findViewById(R.id.curButton);
        buttonselectcurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //goes to map fragment
                while (ContextCompat.checkSelfPermission( getActivity(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION )
                        != PackageManager.PERMISSION_GRANTED){
                    Log.i("MAP", "2");
                    ActivityCompat.requestPermissions(getActivity(), new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, 123);
                }
                SetupFragment fragment = new SetupFragment();
                String tag = SetupFragment.class.getCanonicalName();
                SharedPreferences prefs = getActivity().getSharedPreferences("nukeprefs",0);
                Integer flag = 0; //0 for current location
                prefs.edit().putInt("location_flag",flag).apply();
                getFragmentManager().beginTransaction().replace(R.id.fragContainer, fragment, tag).commit();
            }
        });

        Button buttonselectaddress = (Button) rootView.findViewById(R.id.selectButton);
        buttonselectaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetupFragment fragment = new SetupFragment();
                String tag = SetupFragment.class.getCanonicalName();
                SharedPreferences prefs = getActivity().getSharedPreferences("nukeprefs",0);
                Integer flag = 1; //1 for address
                prefs.edit().putInt("location_flag",flag).apply();
                getFragmentManager().beginTransaction().replace(R.id.fragContainer, fragment, tag).commit();
            }
        });

        return rootView;
    }


}
