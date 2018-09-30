package com.koenidv.camtools;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewStub;

public class CalculatorActivity extends AppCompatActivity {

    public static CollapsingToolbarLayout mImageToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        mImageToolbar = findViewById(R.id.toolbar_layout);

        Intent mIntent = getIntent();

        int imageId = getResources().getIdentifier(
                mIntent.getStringExtra("image"),
                "drawable",
                getPackageName()
        );
        mImageToolbar.setBackground(getDrawable(imageId));
        toolbar.setTitle(mIntent.getStringExtra("title"));

        setSupportActionBar(toolbar);

        int fragmentId = getResources().getIdentifier(
                mIntent.getStringExtra("layout"),
                "layout",
                getPackageName()
        );
        ViewStub stub = (ViewStub) findViewById(R.id.calculate_stub);
        stub.setLayoutResource(fragmentId);
        View inflated = stub.inflate();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
