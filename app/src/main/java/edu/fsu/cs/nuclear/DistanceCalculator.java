package edu.fsu.cs.nuclear;

import android.location.Location;
import android.util.JsonReader;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

class DistanceCalculator {

    protected DistanceCalculator()
    {

    }

    //Calculates distance between the current location and the given location in miles
    static protected double checkDistance(double targetLatitude, double targetLongitude, Location location)
    {
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        if(targetLatitude == currentLatitude && targetLongitude == currentLongitude)
            return 0;

        double latChange = targetLatitude - currentLatitude;
        double longChange = targetLongitude - currentLongitude;
        double dist = Math.sin(Math.toRadians(latChange)/2) * Math.sin(Math.toRadians(latChange)/2) + Math.cos(Math.toRadians(targetLatitude))
                * Math.cos(Math.toRadians(currentLatitude)) * Math.sin(Math.toRadians(longChange)/2) * Math.sin(Math.toRadians(longChange)/2);
        dist = 2 * Math.atan2(Math.sqrt(dist), Math.sqrt(1 - dist));
        dist = 3958.8 * dist;
        return dist;
    }

    //Calculates distance between two given points in miles
    static protected double checkDistance(double targetLatitude, double targetLongitude, double currentLatitude, double currentLongitude)
    {
        if(targetLatitude == currentLatitude && targetLongitude == currentLongitude)
            return 0;

        double latChange = targetLatitude - currentLatitude;
        double longChange = targetLongitude - currentLongitude;
        double dist = Math.sin(Math.toRadians(latChange)/2) * Math.sin(Math.toRadians(latChange)/2) + Math.cos(Math.toRadians(targetLatitude))
                * Math.cos(Math.toRadians(currentLatitude)) * Math.sin(Math.toRadians(longChange)/2) * Math.sin(Math.toRadians(longChange)/2);
        dist = 2 * Math.atan2(Math.sqrt(dist), Math.sqrt(1 - dist));
        dist = 3958.8 * dist;
        return dist;
    }

    //Gets json information for shelter locations
    static protected JSONObject locationFind()
    {
        HttpURLConnection conn = null;
        try
        {
            URL url = new URL("https://services1.arcgis.com/Hp6G80Pky0om7QvQ/arcgis/rest/services/National_Shelter_System_Facilities/FeatureServer/0/query?where=1%3D1&outFields=NAME,ADDRESS,ADDRESS2,CITY,STATE,ZIP,ZIP4,TYPE,COUNTY,COUNTYFIPS,COUNTRY,LATITUDE,LONGITUDE,VAL_DATE&outSR=4326&f=json");
            conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            //if(conn.getResponseCode() != 200)
            //{
             //   throw new RuntimeException("Failed : HTTP error code : "
               //     + conn.getResponseCode());
            //}
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            //new code from here
            String jsonData = "";
            String temp = null;
            while((temp = br.readLine()) != null)
                    jsonData = jsonData + temp;
            JSONObject jObject = new JSONObject(jsonData);

            return jObject;


        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
