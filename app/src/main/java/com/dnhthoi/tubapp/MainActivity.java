package com.dnhthoi.tubapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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

public class MainActivity extends AppCompatActivity {

    public static final String MODE = "MODE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final String arr[]={"https://www.youtube.com/watch?v=TflglOoMU8s",
                "https://www.youtube.com/watch?v=_-S3YwU6QuM",
                "https://www.youtube.com/watch?v=65_kf3KX9Oc"};
        ListView lv=(ListView) findViewById(R.id.listLink);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, arr);
        lv.setAdapter(adapter);
        final TextView txt=(TextView) findViewById(R.id.textHello);
        lv.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> arg0,
                                            View arg1,
                                            int arg2,
                                            long arg3) {
                        txt.setText("position :" + arg2 + " ; value =" + arr[arg2]);
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(arr[arg2])));
                    }
                });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        ToggleButton togg = (ToggleButton)findViewById(R.id.radioMode);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        togg.setChecked(prefs.getBoolean(MODE, true));
        togg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(MODE, isChecked);
                    editor.commit();
                    Log.e("prefs.getBoolean ",""+prefs.getBoolean(MODE, true));


            }
        });
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

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
