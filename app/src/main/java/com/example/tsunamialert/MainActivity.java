package com.example.tsunamialert;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    TextView locationText, alertLevelText, alertMessageText;
    Button playAlertSound;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationText = findViewById(R.id.location);
        alertLevelText = findViewById(R.id.alertLevel);
        alertMessageText = findViewById(R.id.alertMessage);
        playAlertSound = findViewById(R.id.playAlertSound);

        // Load and parse the JSON dataset
        try {
            JSONObject jsonObject = new JSONObject(loadJSONFromAsset());
            JSONArray alertsArray = jsonObject.getJSONArray("tsunamiAlerts");

            // Pick the first alert (you can modify this logic to cycle through or select based on location)
            JSONObject alert = alertsArray.getJSONObject(0);

            String location = alert.getString("location");
            String alertLevel = alert.getString("alertLevel");
            String message = alert.getString("message");

            // Set the data to the TextViews
            locationText.setText("Location: " + location);
            alertLevelText.setText("Alert Level: " + alertLevel);
            alertMessageText.setText("Message: " + message);

            // Set up the alert sound based on the alert level
            playAlertSound.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (alertLevel) {
                        case "high":
                            playSound(R.raw.severe_alert);   // Use the correct sound resource for high alert
                            break;
                        case "moderate":
                            playSound(R.raw.moderate_alert);
                            break;
                        case "low":
                            playSound(R.raw.mild_alert);
                            break;
                        case "safe":
                            // No sound for safe
                            break;
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Load JSON from assets folder
    private String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("tsunami_alerts.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }

    // Play sound
    private void playSound(int soundResId) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this, soundResId);
        mediaPlayer.start();
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}
