package com.dnhthoi.tubapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.dnhthoi.tubapp.Adapter.YouTubeUrlAdapter;
import com.dnhthoi.tubapp.data.YouTubeData;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class MainActivity extends AppCompatActivity {

    public static final String MODE = "MODE";
    private RecyclerView mListUrl;
    private YouTubeUrlAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mListUrl = (RecyclerView) findViewById(R.id.listLink);
        mAdapter = new YouTubeUrlAdapter(this);
        mListUrl.setAdapter(mAdapter);
        mListUrl.setItemAnimator(new DefaultItemAnimator());
        mListUrl.setLayoutManager(new LinearLayoutManager(this));
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        ToggleButton togg = (ToggleButton) findViewById(R.id.radioMode);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        togg.setChecked(prefs.getBoolean(MODE, true));
        togg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(MODE, isChecked);
                editor.commit();
                Log.e("prefs.getBoolean ", "" + prefs.getBoolean(MODE, true));
            }
        });
    }
    public RecyclerView getmListUrl() {
        return mListUrl;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // if true open with youtube imdite
            logOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void logOut(){

//        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
//                new ResultCallback<Status>() {
//                    @Override
//                    public void onResult(Status status) {
//                        // [START_EXCLUDE]
//                        Intent intentLogIn = new Intent(MainActivity.this, LoginActivity.class);
//                        startActivity(intentLogIn);
//                        finish();
//                        // [END_EXCLUDE]
//                    }
//                });
    }
}
