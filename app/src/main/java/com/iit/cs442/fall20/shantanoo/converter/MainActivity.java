package com.iit.cs442.fall20.shantanoo.converter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivityTag";
    private static final String EMPTY_STRING = "";

    private TextView conversionFromTV;
    private TextView conversionToTV;
    private TextView conversionHistory;
    private EditText conversionFromET;
    private EditText conversionToET;

    private Unit fromUnit;
    private Unit toUnit;

    private DecimalFormat decimalFormat;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "on create=>");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        decimalFormat = new DecimalFormat("0.0");

        conversionFromTV = findViewById(R.id.conversionFromLabel);
        conversionToTV = findViewById(R.id.conversionToLabel);
        conversionHistory = findViewById(R.id.conversionHistory);
        conversionFromET = findViewById(R.id.conversionFromEditText);
        conversionToET = findViewById(R.id.conversionToEditText);

        // Setting conversation history text view scrollable
        conversionHistory.setMovementMethod(new ScrollingMovementMethod());

        RadioGroup converterRadioGroup = findViewById(R.id.converterRadioGroup);

        Log.d(TAG, "before preference=>");
        // Loading preferences
        sharedPreferences = getSharedPreferences(getString(R.string.distance_converter_preferences), Context.MODE_PRIVATE);
        String defaultPreferenceUnit = sharedPreferences.getString(getString(R.string.conversion_preference), Unit.MILE.name());
        Log.d(TAG, "defaultPreferenceUnit=>" + defaultPreferenceUnit);
        int radioButtonCheckId;
        switch (Unit.valueOf(defaultPreferenceUnit)) {
            case MILE:
                radioButtonCheckId = R.id.milesToKM;
                break;
            case KILOMETER:
                radioButtonCheckId = R.id.kmToMiles;
                break;
            default:
                Log.e(TAG, "defaultPreferenceUnit=>" + defaultPreferenceUnit);
                throw new IllegalStateException("Unexpected value: " + defaultPreferenceUnit);
        }
        converterRadioGroup.check(radioButtonCheckId);
        setLayout(findViewById(radioButtonCheckId));
    }

    // Conversion logic
    public void convert(View view) {
        String fromText = conversionFromET.getText().toString().trim();
        Log.d(TAG, "convert: fromText=>" + fromText);
        if(TextUtils.isEmpty(fromText))
            return;

        // Conversion
        double fromValue = Double.parseDouble(fromText);
        Log.d(TAG, "convert: parsed fromText=>" + fromValue);
        double result = fromValue * fromUnit.getConversionFactor();
        String resultText = decimalFormat.format(result);
        Log.d(TAG, "convert: result" + resultText);
        conversionToET.setText(resultText);

        // Updating Conversion History
        String currentHistory = conversionHistory.getText().toString().trim();
        Log.d(TAG, "convert: currentHistory=>" + currentHistory);
        StringBuilder newHistory = new StringBuilder();
        newHistory.append(fromValue)
                .append(" ")
                .append(fromUnit.getUnit())
                .append(" ==> ")
                .append(resultText)
                .append(" ")
                .append(toUnit.getUnit())
                .append(System.lineSeparator())
                .append(currentHistory);
        Log.d(TAG, "convert: newHistory=>" + newHistory);

        conversionHistory.setText(newHistory);
        conversionFromET.setText(EMPTY_STRING);
    }

    public void onRadioButtonClicked(View view) {
        setLayout(view);
        conversionToET.setText(EMPTY_STRING);
    }

    private void setLayout(View view) {
        Log.d(TAG, "setLayout: view=>" + view);
        boolean isChecked = ((RadioButton)view).isChecked();
        switch (view.getId()) {
            case R.id.milesToKM:
                if(isChecked)
                    updateSelection(Unit.MILE, Unit.KILOMETER);
                break;
            case R.id.kmToMiles:
                if(isChecked)
                    updateSelection(Unit.KILOMETER, Unit.MILE);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    private void updateSelection(Unit fromUnit, Unit toUnit) {
        this.fromUnit = fromUnit;
        this.toUnit = toUnit;

        conversionFromTV.setText(String.format("%s %s", fromUnit.getLabel(), getString(R.string.value)));
        conversionToTV.setText(String.format("%s %s", toUnit.getLabel(), getString(R.string.value)));

        // Updating preferences
        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
        preferencesEditor.putString(getString(R.string.conversion_preference), fromUnit.name());
        preferencesEditor.apply();
    }
    public void clearConversionHistory(View view) {
        conversionHistory.setText(EMPTY_STRING);

        // Display message
        Toast.makeText(this, R.string.clear_conversion_history_message, Toast.LENGTH_SHORT).show();
    }
}