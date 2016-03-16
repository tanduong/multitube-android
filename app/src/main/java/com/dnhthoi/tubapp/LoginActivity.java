package com.dnhthoi.tubapp;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.dnhthoi.tubapp.Adapter.YouTubeUrlAdapter;
import com.google.android.gms.auth.api.Auth;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;


public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {
    public static final String MODE = "MODE";
    private static final String TAG = "SignInActivity";
    private static final String GMAIL = "gmail";
    private static final String LOGIN = "LOGIN";
    private static final int RC_SIGN_IN = 9001;
    private RecyclerView mListUrl;
    private YouTubeUrlAdapter mAdapter;
    public  GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private LinearLayout mContener;
    private LinearLayout mSignInContener;

    private  MenuItem mItemSignOut;
    private  MenuItem mItemDiconnect;
    MyReceiver receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContener = (LinearLayout) findViewById(R.id.contener);
        mSignInContener= (LinearLayout) findViewById(R.id.sign_button_contenner);
        mListUrl = (RecyclerView) findViewById(R.id.listLink);
        mAdapter = new YouTubeUrlAdapter(this);
        mListUrl.setAdapter(mAdapter);
        mListUrl.setItemAnimator(new DefaultItemAnimator());
        mListUrl.setLayoutManager(new LinearLayoutManager(this));

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
        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);

        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_client]

        // [START customize_button]
        // Customize sign-in button. The sign-in button can be displayed in
        // multiple sizes and color schemes. It can also be contextually
        // rendered based on the requested scopes. For example. a red button may
        // be displayed when Google+ scopes are requested, but a white button
        // may be displayed when only basic profile is requested. Try adding the
        // Scopes.PLUS_LOGIN scope to the GoogleSignInOptions to see the
        // difference.
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
        // [END customize_button]

         receiver = new MyReceiver(new Handler()); // Create the receiver
         // Register receiver
    }
    @Override
    public void onResume(){
        super.onResume();
        registerReceiver(receiver, new IntentFilter("UpdateUI.RecycleViewItem"));
    }
    @Override
    public void onPause(){
        super.onPause();
        //unregisterReceiver(receiver);
        //mAdapter.reloadData();
    }


    public RecyclerView getmListUrl() {
        return mListUrl;
    }
    @Override
    public void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        unregisterReceiver(receiver);
    }
    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mItemSignOut = menu.findItem(R.id.action_settings);
        mItemDiconnect = menu.findItem(R.id.action_disconnect);
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
            signOut();
            return true;
        }

        if (id == R.id.action_disconnect){
            revokeAccess();
            return true;
        }
        if (id == R.id.action_reload){
            mAdapter.reloadData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // [END onActivityResult]

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit().putBoolean(LOGIN, true).commit();
            GoogleSignInAccount acct = result.getSignInAccount();
            updateUI(true);
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit().putString(GMAIL, acct.getEmail()).commit();
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }
    // [END handleSignInResult]

    // [START signIn]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]

    // [START signOut]
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END signOut]
    // [START revokeAccess]
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);

        Toast.makeText(LoginActivity.this, "Sign in to Google failed, Please check your internet connection!", Toast.LENGTH_SHORT).show();
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {

            mSignInContener.animate().translationY(mSignInContener.getHeight()).setDuration(300);
            mSignInContener.setVisibility(View.GONE);

            mContener.animate().translationY(0).setDuration(300);
            mContener.setVisibility(View.VISIBLE);
            if(null != mItemDiconnect && null != mItemSignOut) {
                mItemSignOut.setVisible(true);
                mItemDiconnect.setVisible(true);
            }
        } else {

            mContener.animate().translationY(mContener.getHeight()).setDuration(300);
            mContener.setVisibility(View.GONE);

            mSignInContener.animate().translationY(0).setDuration(300);
            mSignInContener.setVisibility(View.VISIBLE);

            if(null != mItemDiconnect && null != mItemSignOut) {
                mItemSignOut.setVisible(false);
                mItemDiconnect.setVisible(false);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    public  class MyReceiver extends BroadcastReceiver {

        private final Handler handler; // Handler used to execute code on the UI thread

        public MyReceiver(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void onReceive(final Context context, Intent intent) {
            // Post the UI updating code to our Handler
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mAdapter.reloadData();
                    Log.e("BroadcastReceiver","Update UI from onReceive");
                }
            });
        }
    }
}
