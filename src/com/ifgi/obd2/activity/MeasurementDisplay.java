package com.ifgi.obd2.activity;

import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.ifgi.obd2.R;
import com.ifgi.obd2.adapter.DbAdapterLocal;
import com.ifgi.obd2.adapter.Measurement;

public class MeasurementDisplay extends Activity {

	private TextView idTextView;
	private TextView latitudeTextView;
	private TextView longitudeTextView;
	private TextView measurementTimeTextView;
	private TextView throttlePositionTextView;
	private TextView rpmTextView;
	private TextView speedTextView;
	private TextView fuelTypeTextView;
	private TextView engineLoadTextView;
	private TextView fuelConsumptionTextView;
	private TextView intakePressureTextView;
	private TextView intakeTemperatureTextView;
	private TextView shortTermTrimBank1TextView;
	private TextView longTermTrimBank1TextView;
	private TextView mafTextView;

	Measurement measurement;
	private DbAdapterLocal dbAdapter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.measurement_display);

		Bundle extras = getIntent().getExtras();
		String id = extras.getString("id");
		Log.e("obd2", id);

		initDbAdapter();
		measurement = dbAdapter.getMeasurement(Integer.valueOf(id));
		dbAdapter.close();

		if (measurement != null) {
			fillTextViews();
		}

	}

	private void fillTextViews() {

		idTextView = (TextView) findViewById(R.id.idTextView);
		latitudeTextView = (TextView) findViewById(R.id.latitudeTextDisplay);
		longitudeTextView = (TextView) findViewById(R.id.longitudeTextDisplay);
		measurementTimeTextView = (TextView) findViewById(R.id.measurementTimeTextDisplay);
		throttlePositionTextView = (TextView) findViewById(R.id.throttlePositionTextDisplay);
		rpmTextView = (TextView) findViewById(R.id.rpmTextDisplay);
		speedTextView = (TextView) findViewById(R.id.speedTextDisplay);
		fuelTypeTextView = (TextView) findViewById(R.id.fuelTypeTextDisplay);
		engineLoadTextView = (TextView) findViewById(R.id.engineLoadTextDisplay);
		fuelConsumptionTextView = (TextView) findViewById(R.id.fuelConsumptionTextDisplay);
		intakePressureTextView = (TextView) findViewById(R.id.intakePressureTextDisplay);
		intakeTemperatureTextView = (TextView) findViewById(R.id.intakeTemperatureTextDisplay);
		shortTermTrimBank1TextView = (TextView) findViewById(R.id.shortTermTrimBank1TextDisplay);
		longTermTrimBank1TextView = (TextView) findViewById(R.id.longTermTrimBank1TextDisplay);
		mafTextView = (TextView) findViewById(R.id.mafTextDisplay);

		idTextView.setText("ID: " + measurement.getId());
		latitudeTextView.setText("Latitude: " + measurement.getLatitude());
		longitudeTextView.setText("Longitude: " + measurement.getLongitude());
		Date date = new Date(measurement.getMeasurementTime());
		measurementTimeTextView.setText("Time: " + date.toString());
		throttlePositionTextView.setText("Throttle Position: "
				+ measurement.getThrottlePosition() + "%");
		rpmTextView.setText("RPM: " + measurement.getRpm());
		speedTextView.setText("Speed: " + measurement.getSpeed() + "km/h");
		fuelTypeTextView.setText("Fuel Type: " + measurement.getFuelType());
		engineLoadTextView.setText("Engine Load: "
				+ measurement.getEngineLoad() + " %");
		fuelConsumptionTextView.setText("Fuel Consumption: "
				+ measurement.getFuelConsumption() + " l/h");
		intakePressureTextView.setText("Intake Pressure: "
				+ measurement.getIntakePressure() + " kPa");
		intakeTemperatureTextView.setText("Intake Temperature: "
				+ measurement.getIntakeTemperature() + "°C");
		shortTermTrimBank1TextView.setText("Short Term Trim Bank 1: "
				+ measurement.getShortTermTrimBank1() + " %");
		longTermTrimBank1TextView.setText("Long Term Trim Bank 1: "
				+ measurement.getLongTermTrimBank1() + " %");
		mafTextView.setText("MAF: " + measurement.getMaf() + " g/s");

	}

	private void initDbAdapter() {
		dbAdapter = new DbAdapterLocal(getApplicationContext());
		dbAdapter.open();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

}
