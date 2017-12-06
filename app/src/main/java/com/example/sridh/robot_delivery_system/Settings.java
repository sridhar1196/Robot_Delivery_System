package com.example.sridh.robot_delivery_system;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class Settings extends AppCompatActivity {
    ArrayList<String> algorithm = new ArrayList<String>(Arrays.asList("A star","Greedy"));
    String algorithmSelected = "A star";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar actions = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(actions);

        final Spinner algorithmSpinner = (Spinner) findViewById(R.id.algorithm);
        final ArrayAdapter<String> algorithmAdaptor = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,algorithm);
        algorithmAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        algorithmSpinner.setAdapter(algorithmAdaptor);
        algorithmSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                algorithmSelected = adapterView.getItemAtPosition(i).toString().trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        findViewById(R.id.cancel_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.save_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText maximumLoad = (EditText) findViewById(R.id.max_limit);
                if(Integer.parseInt(maximumLoad.getText().toString()) > 0){
                    Intent intent=new Intent();
                    intent.putExtra("MAXIMUM",Integer.parseInt(maximumLoad.getText().toString()));
                    intent.putExtra("ALGORITHM",algorithmSelected);
                    setResult(RESULT_OK,intent);
                    finish();
                } else {
                    Toast.makeText(Settings.this,"Maximum load cannot be less than or equal to zero",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_location, menu);
        return true;
    }
}
