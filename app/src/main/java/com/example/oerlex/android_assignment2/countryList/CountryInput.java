package com.example.oerlex.android_assignment2.countryList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.oerlex.android_assignment2.R;

public class CountryInput extends AppCompatActivity {

    private EditText editName;
    private EditText editDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_input);
        editName = (EditText) findViewById(R.id.editName);
        editDate = (EditText) findViewById(R.id.editDate);
    }

    public void submit(View view) {
        String name = editName.getText().toString();
        String date = editDate.getText().toString();

        if (name.matches("") || date.matches("")) {
            Toast.makeText(this, "Please fill out the form", Toast.LENGTH_SHORT).show();
        } else {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("name", editName.getText().toString());
            returnIntent.putExtra("date", editDate.getText().toString());
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
    }

}
