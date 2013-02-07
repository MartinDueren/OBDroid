/*
 * TODO put header 
 */
package com.ifgi.obd2;

import java.text.NumberFormat;
import java.util.Locale;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ParseException;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.ifgi.obd2.adapter.DbAdapter;
import com.ifgi.obd2.adapter.DbAdapterLocal;
import com.ifgi.obd2.adapter.Measurement;
import com.ifgi.obd2.commands.SpeedObdCommand;
import com.ifgi.obd2.commands.engine.EngineLoadObdCommand;
import com.ifgi.obd2.commands.engine.MassAirFlowObdCommand;
import com.ifgi.obd2.commands.engine.ThrottlePositionObdCommand;
import com.ifgi.obd2.commands.fuel.FuelConsumptionObdCommand;
import com.ifgi.obd2.commands.fuel.FuelTrimObdCommand;
import com.ifgi.obd2.commands.pressure.IntakeManifoldPressureObdCommand;
import com.ifgi.obd2.commands.temperature.AirIntakeTemperatureObdCommand;
import com.ifgi.obd2.enums.AvailableCommandNames;
import com.ifgi.obd2.enums.FuelTrim;
import com.ifgi.obd2.exception.LocationInvalidException;
import com.ifgi.obd2.io.ObdCommandJob;


/**
 * TODO put description
 */
public class IPostListener implements LocationListener{

	
	private Measurement measurement = null;
	private int speedMeasurement;
	private int rpmMeasurement;
	private double throttlePositionMeasurement;
	private double shortTermTrimBank1Measurement;
	private double longTermTrimBank1Measurement;
	private int intakePressureMeasurement;
	private int intakeTemperatureMeasurement;
	private double fuelConsumptionMeasurement;
	private double mafMeasurement;
	private double engineLoadMeasurement;
	private int speed = 1;
//	private double maf = 1;
	@SuppressWarnings("unused")
	private float ltft = 0;

	private float locationLatitude;
	private float locationLongitude;
	private LocationManager locationManager;

	private long lastInsertTime = 0;
	private DbAdapter dbAdapter;
	private static final String TAG = "IPostListener";	
	public Context context;
	
	public IPostListener(Context context){
		this.context = context;
	}
	
	
	public void stateUpdate(ObdCommandJob job) {
		
		initLocationManager();

		initDbAdapter();



		
		String cmdName = job.getCommand().getName();
		String cmdResult = job.getCommand().getFormattedResult();

		 Log.d(TAG, FuelTrim.LONG_TERM_BANK_1.getBank() + " equals "
		 + cmdName + "?");

		if (AvailableCommandNames.ENGINE_RPM.getValue().equals(cmdName)) {
//			TextView tvRpm = (TextView) findViewById(R.id.rpm_text);
//			tvRpm.setText(cmdResult + " rpm");
			rpmMeasurement = Integer.valueOf(cmdResult);
		} else if (AvailableCommandNames.SPEED.getValue().equals(
				cmdName)) {
//			TextView tvSpeed = (TextView) findViewById(R.id.spd_text);
//			tvSpeed.setText(cmdResult + " km/h");
			speed = ((SpeedObdCommand) job.getCommand())
					.getMetricSpeed();
			speedMeasurement = speed;
			// } else if
			// (AvailableCommandNames.MAF.getValue().equals(cmdName))
			// {
			// maf = ((MassAirFlowObdCommand)
			// job.getCommand()).getMAF();
			// addTableRow(cmdName, cmdResult);
		} else if (FuelTrim.SHORT_TERM_BANK_1.getBank().equals(cmdName)) {
//			TextView shortTermTrimTextView = (TextView) findViewById(R.id.shortTrimText);
			String shortTermTrim = ((FuelTrimObdCommand) job
					.getCommand()).getFormattedResult();
//			shortTermTrimTextView.setText("Short Term Trim: "
//					+ shortTermTrim + " %");
			try {
				NumberFormat format = NumberFormat
						.getInstance(Locale.GERMAN);
				Number number;
				number = format.parse(shortTermTrim);
				shortTermTrimBank1Measurement = number.doubleValue();
			} catch (ParseException e) {
				Log.e("obd", "parse exception short term");
				e.printStackTrace();
			} catch (java.text.ParseException e) {
				Log.e("obd", "parse exception short term");
				e.printStackTrace();
			}
			// shortTermTrimBank1Measurement = Double
			// .valueOf(shortTermTrim);
		} else if (FuelTrim.LONG_TERM_BANK_1.getBank().equals(cmdName)) {
//			TextView longTermTrimTextView = (TextView) findViewById(R.id.longTrimText);
			ltft = ((FuelTrimObdCommand) job.getCommand()).getValue();
			String longTermTrim = ((FuelTrimObdCommand) job
					.getCommand()).getFormattedResult();
//			longTermTrimTextView.setText("Long Term Trim: "
//					+ longTermTrim + " %");

			try {
				NumberFormat format = NumberFormat
						.getInstance(Locale.GERMAN);
				Number number;
				number = format.parse(longTermTrim);
				longTermTrimBank1Measurement = number.doubleValue();
			} catch (ParseException e) {
				Log.e("obd", "parse exception long term");
				e.printStackTrace();
			} catch (java.text.ParseException e) {
				Log.e("obd", "parse exception long term");
				e.printStackTrace();
			}
			// longTermTrimBank1Measurement =
			// Double.valueOf(longTermTrim);
		} else if (AvailableCommandNames.AIR_INTAKE_TEMP.getValue()
				.equals(cmdName)) {
//			TextView intakeTempTextView = (TextView) findViewById(R.id.intakeTempText);
			String intakeTemp = ((AirIntakeTemperatureObdCommand) job
					.getCommand()).getFormattedResult();
//			intakeTempTextView.setText("Intake Temp: " + intakeTemp
//					+ " C");
			try {
				intakeTemperatureMeasurement = Integer
						.valueOf(intakeTemp);
			} catch (NumberFormatException e) {
				Log.e("obd", "intake temp parse exception");
				e.printStackTrace();
			}

			// } else if
			// (AvailableCommandNames.EQUIV_RATIO.getValue().equals(
			// cmdName)) {
			// equivRatio = ((CommandEquivRatioObdCommand) job
			// .getCommand()).getRatio();
			// addTableRow(cmdName, cmdResult);
		} else if (AvailableCommandNames.THROTTLE_POS.getValue()
				.equals(cmdName)) {
//			TextView throttlePositionTextView = (TextView) findViewById(R.id.throttle);
			String throttlePos = ((ThrottlePositionObdCommand) job
					.getCommand()).getFormattedResult();
//			throttlePositionTextView.setText("T. Pos: " + throttlePos);

			try {
				NumberFormat format = NumberFormat
						.getInstance(Locale.GERMAN);
				Number number;
				number = format.parse(throttlePos);
				throttlePositionMeasurement = number.doubleValue();
			} catch (ParseException e) {
				Log.e("obd", "parse exception throttle");
				e.printStackTrace();
			} catch (java.text.ParseException e) {
				Log.e("obd", "parse exception throttle");
				e.printStackTrace();
			}

			// try {
			// throttlePositionMeasurement = Double
			// .valueOf(throttlePos);
			// Log.e("obd", "cast successful");
			// } catch (NumberFormatException e) {
			// Log.e("obd2", "number format exception");
			// e.printStackTrace();
			// }

		} else if (AvailableCommandNames.FUEL_CONSUMPTION.getValue()
				.equals(cmdName)) {
//			TextView fuelConsumptionTextView = (TextView) findViewById(R.id.fuelconsumption);
			String fuelCon = ((FuelConsumptionObdCommand) job
					.getCommand()).getFormattedResult();
//			fuelConsumptionTextView.setText(fuelCon + " l/h");
			 Log.d(TAG, "Fuel consumption");
			 Log.d(TAG, fuelCon);

			try {
				NumberFormat format = NumberFormat
						.getInstance(Locale.GERMAN);
				Number number;
				number = format.parse(fuelCon);
				fuelConsumptionMeasurement = number.doubleValue();
			} catch (ParseException e) {
				Log.e("obd", "parse exception fuelCon");
				e.printStackTrace();
			} catch (java.text.ParseException e) {
				Log.e("obd", "parse exception fuelCon");
				e.printStackTrace();
			}
			// fuelConsumptionMeasurement = Double.valueOf(fuelCon);
			// } else if (AvailableCommandNames.FUEL_ECONOMY.getValue()
			// .equals(cmdName)) {
			// TextView fuelEconomyTextView = (TextView)
			// findViewById(R.id.fueleconomy);
			// String fuelEco = ((FuelEconomyObdCommand)
			// job.getCommand())
			// .getFormattedResult();
			// fuelEconomyTextView.setText(fuelEco + " l/100km");

			// } else if
			// (AvailableCommandNames.FUEL_TYPE.getValue().equals(
			// cmdName)) {
			// TextView fuelTypeTextView = (TextView)
			// findViewById(R.id.fuelTypeTextDisplay);
			// String fuelType = ((FindFuelTypeObdCommand) job
			// .getCommand()).getFormattedResult();
			// try {
			// fuelTypeTextView.setText("Fuel Type: " + fuelType);
			// } catch (Exception e) {
			// Log.e("obd2", "fuel type exception");
			// e.printStackTrace();
			// }
		} else if (AvailableCommandNames.ENGINE_LOAD.getValue().equals(
				cmdName)) {
//			TextView engineLoadTextView = (TextView) findViewById(R.id.engineLoadText);
			String engineLoad = ((EngineLoadObdCommand) job
					.getCommand()).getFormattedResult();
			Log.e("obd2", "Engine Load: " + engineLoad);
//			engineLoadTextView.setText("Engine load: " + engineLoad);
			try {
				NumberFormat format = NumberFormat
						.getInstance(Locale.GERMAN);
				Number number;
				number = format.parse(engineLoad);
				engineLoadMeasurement = number.doubleValue();
			} catch (ParseException e) {
				Log.e("obd", "parse exception load");
				e.printStackTrace();
			} catch (java.text.ParseException e) {
				Log.e("obd", "parse exception load");
				e.printStackTrace();
			}
			// engineLoadMeasurement = Double.valueOf(engineLoad);
		} else if (AvailableCommandNames.MAF.getValue().equals(cmdName)) {
//			TextView mafTextView = (TextView) findViewById(R.id.mafText);
			String maf = ((MassAirFlowObdCommand) job.getCommand())
					.getFormattedResult();
//			mafTextView.setText("MAF: " + maf + " g/s");
			try {
				NumberFormat format = NumberFormat
						.getInstance(Locale.GERMAN);
				Number number;
				number = format.parse(maf);
				mafMeasurement = number.doubleValue();
			} catch (ParseException e) {
				Log.e("obd", "parse exception maf");
				e.printStackTrace();
			} catch (java.text.ParseException e) {
				Log.e("obd", "parse exception maf");
				e.printStackTrace();
			}
			// mafMeasurement = Double.valueOf(maf);
		} else if (AvailableCommandNames.INTAKE_MANIFOLD_PRESSURE
				.getValue().equals(cmdName)) {
//			TextView intakePressureTextView = (TextView) findViewById(R.id.intakeText);
			String intakePressure = ((IntakeManifoldPressureObdCommand) job
					.getCommand()).getFormattedResult();
//			intakePressureTextView.setText("Intake: " + intakePressure
//					+ "kPa");
			try {
				intakePressureMeasurement = Integer
						.valueOf(intakePressure);
			} catch (NumberFormatException e) {
				Log.e("obd", "intake pressure parse exception");
				e.printStackTrace();
			}
		} else {
			// addTableRow(cmdName, cmdResult);
		}
		updateMeasurement();
	}
	
	public void updateMeasurement() {

		if (measurement == null) {
			try {
				measurement = new Measurement(locationLatitude,
						locationLongitude);
			} catch (LocationInvalidException e) {
				e.printStackTrace();
			}
		}
		if (measurement != null) {

			if (Math.abs(measurement.getMeasurementTime()
					- System.currentTimeMillis()) < 5000) {

				measurement.setSpeed(speedMeasurement);
				measurement.setRpm(rpmMeasurement);
				measurement.setThrottlePosition(throttlePositionMeasurement);
				measurement.setEngineLoad(engineLoadMeasurement);
				measurement.setFuelConsumption(fuelConsumptionMeasurement);
				measurement.setIntakePressure(intakePressureMeasurement);
				measurement.setIntakeTemperature(intakeTemperatureMeasurement);
				measurement
						.setShortTermTrimBank1(shortTermTrimBank1Measurement);
				measurement.setLongTermTrimBank1(longTermTrimBank1Measurement);
				measurement.setMaf(mafMeasurement);
				Log.e("obd2", "new measurement");
				Log.e("obd2", measurement.toString());
				Toast.makeText(context.getApplicationContext(), measurement.toString(), Toast.LENGTH_SHORT).show();

				insertMeasurement(measurement);

			} else {
				try {
					measurement = new Measurement(locationLatitude,
							locationLongitude);
				} catch (LocationInvalidException e) {
					e.printStackTrace();
				}
			}
		}

	}
	
	
	private void insertMeasurement(Measurement measurement2) {

		if (Math.abs(lastInsertTime - measurement2.getMeasurementTime()) > 5000) {

			lastInsertTime = measurement2.getMeasurementTime();

			dbAdapter.insertMeasurement(measurement2);

			Toast.makeText(context.getApplicationContext(), measurement2.toString(),
					Toast.LENGTH_SHORT).show();

		}

	}
	
	private void initDbAdapter() {
		dbAdapter = new DbAdapterLocal(context.getApplicationContext());
		dbAdapter.open();
	}
	
	/**
	 * Init the location Manager with the user's choice of the location method
	 */
	private void initLocationManager() {

		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, this);
//
		// changeLocationMethod();
	}
	
	public void onLocationChanged(Location location) {

		locationLatitude = (float) location.getLatitude();

//		locationLatitudeTextView.setText(String.valueOf(locationLatitude));
		
		locationLongitude = (float) location.getLongitude();
		Log.d(TAG, "Lat/Lon: " + locationLatitude + "/" + locationLongitude);
//		locationLongitudeTextView.setText(String.valueOf(locationLongitude));

	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(context.getApplicationContext(), "Gps Disabled",
				Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(context.getApplicationContext(), "Gps Enabled",
				Toast.LENGTH_SHORT).show();

	}
    
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}
	
}