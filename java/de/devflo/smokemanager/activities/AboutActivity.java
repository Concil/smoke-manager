package de.devflo.smokemanager.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import de.devflo.smokemanager.BuildConfig;
import de.devflo.smokemanager.R;

public class AboutActivity extends AppCompatActivity {

    String versionName = BuildConfig.VERSION_NAME;
    int versionCode = BuildConfig.VERSION_CODE;

    // [VIEWS] //
    TextView appDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().setElevation(0);


        appDetails = findViewById(R.id.appDetails);
        appDetails.setText(String.format("App Version: %s\nBuild: %d", versionName, versionCode));
    }
}
