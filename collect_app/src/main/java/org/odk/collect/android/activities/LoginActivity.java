package org.odk.collect.android.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.odk.collect.android.R;
import org.odk.collect.android.tasks.LoginResult;
import org.odk.collect.android.utilities.HttpUtility;
import org.odk.collect.android.utilities.SharedPrefs;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private PostAsync mAuthTask = null;

    //inputs for login activity
    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUsernameView = findViewById(R.id.username);

        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });

        mUsernameView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });

        Button mSignInButton = findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(view -> attemptLogin());

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        //Reset errors
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        //Store value at the time of the login attempt
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            //There was an error; do not attempt to login and focus the first
            //form field with an error
            focusView.requestFocus();
        } else {
            //Show a progress spinner, and kick off a background task to
            //perform the user login attempt
            showProgress(true);
            mAuthTask = new PostAsync(username, password);
            mAuthTask.execute();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        //On HoneyComb MR2 we have the ViewPropertyAnimator APIs, which allow
        //for very easy animations. If available, use these APIs to fade-in
        //the progress spinner
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private class PostAsync extends AsyncTask<String, String, JSONObject> {

        private HashMap<String, String> parameters;

        private ProgressDialog pDialog;
        private static final String urlForEco = "https://lgxmldemo.azurewebsites.net/LogixEco";

        PostAsync(String username, String password) {
            parameters = (HashMap<String, String>) GetLoginStruct(username, password);
        }

        HttpUtility httpUtility = new HttpUtility();

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Attempting login...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected JSONObject doInBackground(String... params) {

            JSONObject json = httpUtility.httpPost(
                    urlForEco + "/api/Login", "POST", parameters, "");
            JSONObject res = null;
            try {
                //Simulate network access
                Thread.sleep(1500);

                Log.d("request", "starting");

                if (json != null) {
                    Log.d("JSON result", json.toString());

                    res = json;
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return res;
        }

        protected void onPostExecute(JSONObject res) {

            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if (res != null) {
                try {
                    LoginResult.setUserToken(res.getString("UserToken"));
                    LoginResult.setSuccess_Flag(res.getBoolean("Success_Flag"));
                    LoginResult.setMessage(res.getString("Message"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if(LoginResult.getSuccess_Flag()){
                SharedPrefs.saveSharedSetting(LoginActivity.this, "NatheanPref", "false");
                Intent i = new Intent(getApplicationContext(), MainMenuActivity.class);
                startActivity(i);

                //another way to get usertoken from one activity to another
                //i.putExtra("UserToken", LoginResult.getUserToken());
                finish();
            } else{
                // "restarts" the login form if incorrect password or username is entered
                if(Build.VERSION.SDK_INT >= 11){
                    recreate();
                    Toast.makeText(getApplicationContext(), "Invalid username or password",
                            Toast.LENGTH_LONG).show();
                } else{
                    finish();
                    startActivity(getIntent());
                    Toast.makeText(getApplicationContext(), "Invalid username or password",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private Map<String, String> GetLoginStruct(String username, String password) {
        String AuthKey = "Logix";
        String SystemCode = "LGX1";
        HashMap<String, String> parameters = new HashMap<>();

        parameters.put("AuthenticateKey", AuthKey);
        parameters.put("SystemCode", SystemCode);
        parameters.put("UserID", username);
        parameters.put("Password", password);

        return parameters;
    }
}

