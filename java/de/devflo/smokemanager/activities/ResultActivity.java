package de.devflo.smokemanager.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import net.steamcrafted.materialiconlib.MaterialMenuInflater;

import de.devflo.smokemanager.R;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        getSupportActionBar().setElevation(0);
        setTitle("Statistik");
    }
}
