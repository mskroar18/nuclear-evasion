package edu.fsu.cs.nuclear;
//Authors of package:  Matthew Smith-Kennedy (Design, data flow, content, and ooding Main Activity, Launch, Setup and Info Fragments),
//Luis H.:  distance calculator and GitMaster.  Ahmad R.: Effects/shelters work. Madison: Map Fragment and help with design scheme/coding on other frags.
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);

        if(savedInstanceState == null){ //launches mainfragment
            LaunchFragment fragment = new LaunchFragment();
            String tag = LaunchFragment.class.getCanonicalName();
            getFragmentManager().beginTransaction().add(R.id.fragContainer, fragment, tag).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();
        if(id == R.id.mainMenu){
            LaunchFragment fragment = new LaunchFragment();
            String tag = LaunchFragment.class.getCanonicalName();
            getFragmentManager().beginTransaction().replace(R.id.fragContainer, fragment, tag).commit();
        }
        else if(id == R.id.exitApp){ //https://stackoverflow.com/questions/6330200/how-to-quit-android-application-programmatically
            this.finishAffinity();
        }
        return super.onOptionsItemSelected(item);
    }



}
