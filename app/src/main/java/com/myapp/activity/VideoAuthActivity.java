package com.myapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.myapp.R;
import com.voiceit.voiceit2.VoiceItAPI2;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class VideoAuthActivity extends AppCompatActivity {

    private VoiceItAPI2 myVoiceIt;
    private String [] userId = {"usr_1", "usr_2"};
    private int userIdIndex = 0;
    private String groupId = "GROUP_ID";
    private String phrase = "Your phrase";
    private String contentLanguage = "contentLanguage";
    private boolean doLivenessCheck = false;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    private  Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_auth);
        context = getApplicationContext();
        // If using user tokens, replace API_KEY below with the user token,
        // and leave the second argument as an empty string
        myVoiceIt = new VoiceItAPI2("Your key","Your token");
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String user = sharedpreferences.getString("user",null);
        if (user == null) {
            // the key does not exist
            createUser();
        } else {
            userId[userIdIndex] = user;
            // handle the value
            encapsulatedVideoVerification();
        }
    }

    public void createUser(){
        myVoiceIt.createUser(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String userID = response.getString("userId");
                    userId[userIdIndex] =  userID;
                }catch (Exception ex) {
                }
                encapsulatedVideoEnrollment();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                checkResponse(errorResponse);
                if (errorResponse != null) {
                    System.out.println("createUser onFailure Result : " + errorResponse.toString());
                }
            }

        });
    }

    public void encapsulatedVideoEnrollment() {
        myVoiceIt.encapsulatedVideoEnrollment(this, userId[userIdIndex], contentLanguage, phrase, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println("encapsulatedVideoEnrollment onSuccess Result : " + response.toString());

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("user", userId[userIdIndex]);
                editor.commit();
                encapsulatedVideoVerification();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                checkResponse(errorResponse);
                if (errorResponse != null) {
                    System.out.println("encapsulatedVideoEnrollment onFailure Result : " + errorResponse.toString());
                }
            }
        });
    }


    public void encapsulatedVideoVerification() {
        myVoiceIt.encapsulatedVideoVerification(this, userId[userIdIndex], contentLanguage, phrase, doLivenessCheck, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println("encapsulatedVideoVerification onSuccess Result : " + response.toString());
                Intent intent = new Intent(VideoAuthActivity.this, GoToActivity.class);
                // intent.putExtra("selected","pay");
                // Intent intent = new Intent(getApplicationContext(), BotActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                checkResponse(errorResponse);
                if (errorResponse != null) {
                    System.out.println("encapsulatedVideoVerification onFailure Result : " + errorResponse.toString());
                }
            }
        });
    }

    public void checkResponse(JSONObject response) {
        try {
            if (response.getString("responseCode").equals("IFVD")
                    || response.getString("responseCode").equals("ACLR")
                    || response.getString("responseCode").equals("IFAD")
                    || response.getString("responseCode").equals("SRNR")
                    || response.getString("responseCode").equals("UNFD")
                    || response.getString("responseCode").equals("MISP")
                    || response.getString("responseCode").equals("DAID")
                    || response.getString("responseCode").equals("UNAC")
                    || response.getString("responseCode").equals("CLNE")
                    || response.getString("responseCode").equals("INCP")
                    || response.getString("responseCode").equals("NPFC")) {
                Toast.makeText(this, "responseCode: " + response.getString("responseCode")
                        + ", " + getString(com.voiceit.voiceit2.R.string.CHECK_CODE), Toast.LENGTH_LONG).show();
                Log.e("MainActivity","responseCode: " + response.getString("responseCode")
                        + ", " + getString(com.voiceit.voiceit2.R.string.CHECK_CODE));
            }
        } catch (JSONException e) {
            Log.d("MainActivity","JSON exception : " + e.toString());
        }
    }
}
