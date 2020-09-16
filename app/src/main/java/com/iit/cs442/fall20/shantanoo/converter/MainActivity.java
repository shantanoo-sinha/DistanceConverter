package com.iit.cs442.fall20.shantanoo.converter;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;

/**
 * Created by Shantanoo on 9/7/2020.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivityTag";
    private static final String EMPTY_STRING = "";

    private TextView conversionFromTV;
    private TextView conversionToTV;
    private TextView conversionHistory;

    private EditText conversionFromET;
    private EditText conversionToET;

    private RadioGroup converterRadioGroup;

    private Unit fromUnit;
    private Unit toUnit;

    private DecimalFormat decimalFormat;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "create: Creating App Layout");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        decimalFormat = new DecimalFormat("0.0");

        conversionFromTV = findViewById(R.id.conversionFromLabel);
        conversionToTV = findViewById(R.id.conversionToLabel);
        conversionHistory = findViewById(R.id.conversionHistory);
        conversionFromET = findViewById(R.id.conversionFromEditText);
        conversionToET = findViewById(R.id.conversionToEditText);
        converterRadioGroup = findViewById(R.id.converterRadioGroup);

        // Setting conversation history text view scrollable
        conversionHistory.setMovementMethod(new ScrollingMovementMethod());

        // Loading preferences
        Log.d(TAG, "create: before preference=>");
        sharedPreferences = getSharedPreferences(getString(R.string.distance_converter_preferences), Context.MODE_PRIVATE);
        String defaultPreferenceUnit = sharedPreferences.getString(getString(R.string.conversion_preference), Unit.MILE.name());
        Log.d(TAG, "create: defaultPreferenceUnit=>" + defaultPreferenceUnit);

        int radioButtonCheckId;
        switch (Unit.valueOf(defaultPreferenceUnit)) {
            case MILE:
                radioButtonCheckId = R.id.milesToKM;
                break;
            case KILOMETER:
                radioButtonCheckId = R.id.kmToMiles;
                break;
            default:
                //Log.e(TAG, "defaultPreferenceUnit=>" + defaultPreferenceUnit);
                throw new IllegalStateException("Unexpected value: " + defaultPreferenceUnit);
        }
        converterRadioGroup.check(radioButtonCheckId);
        setLayout(findViewById(radioButtonCheckId));
    }

    // Conversion logic
    public void convert(View view) {
        Log.d(TAG, "convert: converting data");

        String fromText = conversionFromET.getText().toString().trim();
        Log.d(TAG, "convert: fromText=>" + fromText);

        if (TextUtils.isEmpty(fromText))
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
        Log.d(TAG, "onRadioButtonClicked: Radio Button selection changed");
        setLayout(view);
        conversionToET.setText(EMPTY_STRING);
    }

    private void setLayout(View view) {
        Log.d(TAG, "setLayout: Updating layout values");
        boolean isChecked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.milesToKM:
                if (isChecked)
                    updateSelection(Unit.MILE, Unit.KILOMETER);
                break;
            case R.id.kmToMiles:
                if (isChecked)
                    updateSelection(Unit.KILOMETER, Unit.MILE);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    private void updateSelection(Unit fromUnit, Unit toUnit) {
        Log.d(TAG, "updateSelection: Updating Conversion Selection");
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
        Log.d(TAG, "clearConversionHistory: Clearing Conversion History");
        conversionHistory.setText(EMPTY_STRING);

        // Display message
        Toast.makeText(this, R.string.clear_conversion_history_message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: Saving Instance State");

        outState.putInt("RADIO_BUTTON_ID", converterRadioGroup.getCheckedRadioButtonId());
        outState.putString("FROM_VALUE", conversionFromET.getText().toString().trim());
        outState.putString("TO_VALUE", conversionToET.getText().toString().trim());
        outState.putString("FROM_TEXT", conversionFromTV.getText().toString());
        outState.putString("TO_TEXT", conversionToTV.getText().toString());
        outState.putString("CONVERSION_HISTORY", conversionHistory.getText().toString().trim());

        // Call super last
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        Log.d(TAG, "onRestoreInstanceState: Restoring Instance State");

        // Call super first
        super.onRestoreInstanceState(savedInstanceState);

        converterRadioGroup.check(savedInstanceState.getInt("RADIO_BUTTON_ID"));
        conversionFromET.setText(savedInstanceState.getString("FROM_VALUE"));
        conversionToET.setText(savedInstanceState.getString("TO_VALUE"));
        conversionFromTV.setText(savedInstanceState.getString("FROM_TEXT"));
        conversionToTV.setText(savedInstanceState.getString("TO_TEXT"));
        conversionHistory.setText(savedInstanceState.getString("CONVERSION_HISTORY"));
    }
}