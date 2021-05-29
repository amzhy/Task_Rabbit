package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.jetbrains.annotations.NotNull;

public class Notfications extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notfications);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater1 = getMenuInflater();
        inflater1.inflate(R.menu.profile_menu, menu);
        MenuItem p = menu.findItem(R.id.profile_upload_photo);
        p.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_notifications: {
                return true;
            } case R.id.settings_about: {
                startActivity(new Intent(Notfications.this, AboutUs.class));
                return true;
            } case R.id.settings_guide: {
                startActivity(new Intent(Notfications.this, UserGuide.class));
                return true;
            } default:
                return super.onOptionsItemSelected(item);
        }
    }
}