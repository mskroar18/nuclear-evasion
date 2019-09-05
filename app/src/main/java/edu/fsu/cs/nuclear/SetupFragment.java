package edu.fsu.cs.nuclear;
//shared prefrence notes :  getSharedPreferences
// name of preferences = nukeprefs
//"location_flag" is an int.  1 means address will be entered. 0 is current location
// "address" is a string.  will be "" if location_flag== 0, otherwise stores string as one line
//like 341 Sharer Rd. Tallahassee 32305  ## no state
//"enemycode" is an int.  1= north korea, 2= russia, 3 = china. otherwise 0

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


public class SetupFragment extends Fragment {

    private RadioButton russia, korea, china;
    private TextView countrytext;
    private TextView address;
    private ImageView image;
    SharedPreferences prefs;

    public SetupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_setup, container, false);
        russia = rootView.findViewById(R.id.russiabutton);  //code 2
        korea = rootView.findViewById(R.id.northkoreabutton); //code 1
        china = rootView.findViewById(R.id.chinabutton);  //code 3
        image = rootView.findViewById(R.id.imageView);
        countrytext= rootView.findViewById(R.id.textforenemy);
        address= rootView.findViewById(R.id.addresstext);
        prefs = getActivity().getSharedPreferences("nukeprefs",0);
        int flag=0;
        flag=prefs.getInt("location_flag", -1);
        if (flag==0) {
            address.setText(getResources().getString(R.string.current_selected));
            address.setEnabled(false);
        }
        else
        {
            address.setText("");
            address.setHint(getResources().getString(R.string.enter_address));
            address.setEnabled(true);
        }

        russia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image.setImageResource(R.drawable.russia);
            }
        });
        korea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image.setImageResource(R.drawable.korea);
            }
        });
        china.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image.setImageResource(R.drawable.china);
            }
        });


        Button submitbutton = (Button) rootView.findViewById(R.id.submitfromsetup);
        submitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean error= false;
                int type=0;
                if (!korea.isChecked() && !russia.isChecked() && !china.isChecked())
                {
                    error=true;
                    TextView countrytext= rootView.findViewById(R.id.textforenemy);
                    countrytext.setError("Please Select Adversary!");
                }
                int flag=0;
                flag=prefs.getInt("location_flag", -1);
                if (flag==1 && address.getText().toString().matches(""))
                {
                    address.setError("Error:  Please Enter Address");
                    error = true;
                }
                if (error==false)
                {
                    if (flag==1)
                    {
                        prefs.edit().putString("address",address.getText().toString()).apply();
                    }
                    else
                    {
                        prefs.edit().putString("address","").apply();
                    }
                    if (russia.isChecked())
                    {
                        type=2;
                        prefs.edit().putInt("enemycode",type).apply();
                    }
                    else if (china.isChecked())
                    {
                        type=3;
                        prefs.edit().putInt("enemycode",type).apply();
                    }
                    else if (korea.isChecked())
                    {
                        type=1;
                        prefs.edit().putInt("enemycode",type).apply();
                    }

                }

                if(error == false){
                    //go to map fragment
                    MapFragment mapF = new MapFragment();
                    String tag = MapFragment.class.getCanonicalName();
                    getFragmentManager().beginTransaction().replace(R.id.fragContainer, mapF, tag).commit();
                }


            }
        });

        return rootView;
    }

}