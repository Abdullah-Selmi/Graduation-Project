package com.abdullah.graduationproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
    }

    public void ReviewButtonClicked(View view) {
        Toast.makeText(this, "the review added successfully", Toast.LENGTH_SHORT).show();
    }
}