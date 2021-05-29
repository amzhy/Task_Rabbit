package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class UserGuide extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_guide);
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
                startActivity(new Intent(UserGuide.this, Notfications.class));
                return true;
            } case R.id.settings_about: {
                startActivity(new Intent(UserGuide.this, AboutUs.class));
                return true;
            } case R.id.settings_guide: {
                return true;
            } default:
                return super.onOptionsItemSelected(item);
        }
    }
}