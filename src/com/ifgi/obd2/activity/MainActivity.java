///*
// * TODO put header
// */
//package com.ifgi.obd2.activity;
//
//import java.text.NumberFormat;
//import java.util.ArrayList;
//import java.util.Locale;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.bluetooth.BluetoothAdapter;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.hardware.Sensor;
//import android.hardware.SensorManager;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.net.ParseException;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.PowerManager;
//import android.preference.PreferenceManager;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.ifgi.obd2.IPostListener;
//import com.ifgi.obd2.R;
//import com.ifgi.obd2.adapter.DbAdapter;
//import com.ifgi.obd2.adapter.DbAdapterLocal;
//import com.ifgi.obd2.adapter.Measurement;
//import com.ifgi.obd2.commands.SpeedObdCommand;
//import com.ifgi.obd2.commands.engine.EngineLoadObdCommand;
//import com.ifgi.obd2.commands.engine.EngineRPMObdCommand;
//import com.ifgi.obd2.commands.engine.MassAirFlowObdCommand;
//import com.ifgi.obd2.commands.engine.ThrottlePositionObdCommand;
//import com.ifgi.obd2.commands.fuel.FindFuelTypeObdCommand;
//import com.ifgi.obd2.commands.fuel.FuelConsumptionObdCommand;
//import com.ifgi.obd2.commands.fuel.FuelEconomyObdCommand;
//import com.ifgi.obd2.commands.fuel.FuelEconomyWithMAFObdCommand;
//import com.ifgi.obd2.commands.fuel.FuelTrimObdCommand;
//import com.ifgi.obd2.commands.pressure.IntakeManifoldPressureObdCommand;
//import com.ifgi.obd2.commands.temperature.AirIntakeTemperatureObdCommand;
//import com.ifgi.obd2.enums.AvailableCommandNames;
//import com.ifgi.obd2.enums.FuelTrim;
//import com.ifgi.obd2.enums.FuelType;
//import com.ifgi.obd2.exception.LocationInvalidException;
//import com.ifgi.obd2.io.ObdCommandJob;
//import com.ifgi.obd2.io.ObdGatewayService;
//import com.ifgi.obd2.io.ObdGatewayServiceConnection;
//
///**
// * The main activity.
// */
//
//// TODO: Diesel or gasoline?
//
//public class MainActivity extends Activity implements LocationListener {
//
//	private static final String TAG = "MainActivity";
//
//	/*
//	 * TODO put description
//	 */
//	static final int NO_BLUETOOTH_ID = 0;
//	static final int BLUETOOTH_DISABLED = 1;
//	static final int NO_GPS_ID = 2;
//	static final int START_LIVE_DATA = 3;
//	static final int STOP_LIVE_DATA = 4;
//	static final int SETTINGS = 5;
//	static final int COMMAND_ACTIVITY = 6;
//	static final int TABLE_ROW_MARGIN = 7;
//	static final int NO_ORIENTATION_SENSOR = 8;
//	static final int START_LIST_VIEW = 9;
//
//	private Handler mHandler = new Handler();
//
//	/**
//	 * Callback for ObdGatewayService to update UI.
//	 */
//	private IPostListener mListener = null;
//	private Intent mServiceIntent = null;
//	private ObdGatewayServiceConnection mServiceConnection = null;
//
//	private SensorManager sensorManager = null;
//	private Sensor orientSensor = null;
//	private SharedPreferences prefs = null;
//
//	private PowerManager powerManager = null;
//	private PowerManager.WakeLock wakeLock = null;
//
//	private boolean preRequisites = true;
//
//	private int speed = 1;
//	private double maf = 1;
//	private float ltft = 0;
//	private double equivRatio = 1;
//	private float locationLatitude;
//	private float locationLongitude;
//	private LocationManager locationManager;
//	private TextView locationLatitudeTextView;
//	private TextView locationLongitudeTextView;
//	private DbAdapter dbAdapter;
//
//	private Measurement measurement = null;
//	// private boolean throttleSet = false;
//	// private boolean rpmSet = false;
//	// private boolean speedSet = false;
//	private int speedMeasurement;
//	private int rpmMeasurement;
//	private double throttlePositionMeasurement;
//	private double shortTermTrimBank1Measurement;
//	private double longTermTrimBank1Measurement;
//	private int intakePressureMeasurement;
//	private int intakeTemperatureMeasurement;
//	private double fuelConsumptionMeasurement;
//	private double mafMeasurement;
//	private double engineLoadMeasurement;
//
//	private long lastInsertTime = 0;
//
//	// private final SensorEventListener orientListener = new
//	// SensorEventListener() {
//	// public void onSensorChanged(SensorEvent event) {
//	// float x = event.values[0];
//	// String dir = "";
//	// if (x >= 337.5 || x < 22.5) {
//	// dir = "N";
//	// } else if (x >= 22.5 && x < 67.5) {
//	// dir = "NE";
//	// } else if (x >= 67.5 && x < 112.5) {
//	// dir = "E";
//	// } else if (x >= 112.5 && x < 157.5) {
//	// dir = "SE";
//	// } else if (x >= 157.5 && x < 202.5) {
//	// dir = "S";
//	// } else if (x >= 202.5 && x < 247.5) {
//	// dir = "SW";
//	// } else if (x >= 247.5 && x < 292.5) {
//	// dir = "W";
//	// } else if (x >= 292.5 && x < 337.5) {
//	// dir = "NW";
//	// }
//	// TextView compass = (TextView) findViewById(R.id.compass_text);
//	// updateTextView(compass, dir);
//	// }
//	//
//	// public void onAccuracyChanged(Sensor sensor, int accuracy) {
//	// // TODO Auto-generated method stub
//	// }
//	// };
//
//	public void updateTextView(final TextView view, final String txt) {
//		new Handler().post(new Runnable() {
//			public void run() {
//				view.setText(txt);
//			}
//		});
//	}
//
//	public void updateMeasurement() {
//
//		if (measurement == null) {
//			try {
//				measurement = new Measurement(locationLatitude,
//						locationLongitude);
//			} catch (LocationInvalidException e) {
//				e.printStackTrace();
//			}
//		}
//		if (measurement != null) {
//
//			if (Math.abs(measurement.getMeasurementTime()
//					- System.currentTimeMillis()) < 5000) {
//
//				measurement.setSpeed(speedMeasurement);
//				measurement.setRpm(rpmMeasurement);
//				measurement.setThrottlePosition(throttlePositionMeasurement);
//				measurement.setEngineLoad(engineLoadMeasurement);
//				measurement.setFuelConsumption(fuelConsumptionMeasurement);
//				measurement.setIntakePressure(intakePressureMeasurement);
//				measurement.setIntakeTemperature(intakeTemperatureMeasurement);
//				measurement
//						.setShortTermTrimBank1(shortTermTrimBank1Measurement);
//				measurement.setLongTermTrimBank1(longTermTrimBank1Measurement);
//				measurement.setMaf(mafMeasurement);
//				Log.e("obd2", "new measurement");
//				Log.e("obd2", measurement.toString());
//				// Toast.makeText(getApplicationContext(),
//				// measurement.toString(),
//				// Toast.LENGTH_SHORT).show();
//
//				insertMeasurement(measurement);
//
//			} else {
//				try {
//					measurement = new Measurement(locationLatitude,
//							locationLongitude);
//				} catch (LocationInvalidException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//
//	}
//
//	private void insertMeasurement(Measurement measurement2) {
//
//		if (Math.abs(lastInsertTime - measurement2.getMeasurementTime()) > 5000) {
//
//			lastInsertTime = measurement2.getMeasurementTime();
//
//			dbAdapter.insertMeasurement(measurement2);
//
//			Toast.makeText(getApplicationContext(), measurement2.toString(),
//					Toast.LENGTH_SHORT).show();
//
//		}
//
//	}
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//
//		/*
//		 * TODO clean-up this upload thing
//		 * 
//		 * ExceptionHandler.register(this,
//		 * "http://www.whidbeycleaning.com/droid/server.php");
//		 */
//		setContentView(R.layout.main);
//
//		locationLatitudeTextView = (TextView) findViewById(R.id.latitudeText);
//		locationLongitudeTextView = (TextView) findViewById(R.id.longitudeText);
//
//		try {
//			measurement = new Measurement(locationLatitude, locationLongitude);
//		} catch (LocationInvalidException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		mListener = new IPostListener() {
//
//			public void stateUpdate(ObdCommandJob job) {
//				String cmdName = job.getCommand().getName();
//				String cmdResult = job.getCommand().getFormattedResult();
//
//				// Log.d(TAG, FuelTrim.LONG_TERM_BANK_1.getBank() + " equals "
//				// + cmdName + "?");
//
//				if (AvailableCommandNames.ENGINE_RPM.getValue().equals(cmdName)) {
//					TextView tvRpm = (TextView) findViewById(R.id.rpm_text);
//					tvRpm.setText(cmdResult + " rpm");
//					rpmMeasurement = Integer.valueOf(cmdResult);
//				} else if (AvailableCommandNames.SPEED.getValue().equals(
//						cmdName)) {
//					TextView tvSpeed = (TextView) findViewById(R.id.spd_text);
//					tvSpeed.setText(cmdResult + " km/h");
//					speed = ((SpeedObdCommand) job.getCommand())
//							.getMetricSpeed();
//					speedMeasurement = speed;
//					// } else if
//					// (AvailableCommandNames.MAF.getValue().equals(cmdName))
//					// {
//					// maf = ((MassAirFlowObdCommand)
//					// job.getCommand()).getMAF();
//					// addTableRow(cmdName, cmdResult);
//				} else if (FuelTrim.SHORT_TERM_BANK_1.getBank().equals(cmdName)) {
//					TextView shortTermTrimTextView = (TextView) findViewById(R.id.shortTrimText);
//					String shortTermTrim = ((FuelTrimObdCommand) job
//							.getCommand()).getFormattedResult();
//					shortTermTrimTextView.setText("Short Term Trim: "
//							+ shortTermTrim + " %");
//					try {
//						NumberFormat format = NumberFormat
//								.getInstance(Locale.GERMAN);
//						Number number;
//						number = format.parse(shortTermTrim);
//						shortTermTrimBank1Measurement = number.doubleValue();
//					} catch (ParseException e) {
//						Log.e("obd", "parse exception short term");
//						e.printStackTrace();
//					} catch (java.text.ParseException e) {
//						Log.e("obd", "parse exception short term");
//						e.printStackTrace();
//					}
//					// shortTermTrimBank1Measurement = Double
//					// .valueOf(shortTermTrim);
//				} else if (FuelTrim.LONG_TERM_BANK_1.getBank().equals(cmdName)) {
//					TextView longTermTrimTextView = (TextView) findViewById(R.id.longTrimText);
//					ltft = ((FuelTrimObdCommand) job.getCommand()).getValue();
//					String longTermTrim = ((FuelTrimObdCommand) job
//							.getCommand()).getFormattedResult();
//					longTermTrimTextView.setText("Long Term Trim: "
//							+ longTermTrim + " %");
//
//					try {
//						NumberFormat format = NumberFormat
//								.getInstance(Locale.GERMAN);
//						Number number;
//						number = format.parse(longTermTrim);
//						longTermTrimBank1Measurement = number.doubleValue();
//					} catch (ParseException e) {
//						Log.e("obd", "parse exception long term");
//						e.printStackTrace();
//					} catch (java.text.ParseException e) {
//						Log.e("obd", "parse exception long term");
//						e.printStackTrace();
//					}
//					// longTermTrimBank1Measurement =
//					// Double.valueOf(longTermTrim);
//				} else if (AvailableCommandNames.AIR_INTAKE_TEMP.getValue()
//						.equals(cmdName)) {
//					TextView intakeTempTextView = (TextView) findViewById(R.id.intakeTempText);
//					String intakeTemp = ((AirIntakeTemperatureObdCommand) job
//							.getCommand()).getFormattedResult();
//					intakeTempTextView.setText("Intake Temp: " + intakeTemp
//							+ " C");
//					try {
//						intakeTemperatureMeasurement = Integer
//								.valueOf(intakeTemp);
//					} catch (NumberFormatException e) {
//						Log.e("obd", "intake temp parse exception");
//						e.printStackTrace();
//					}
//
//					// } else if
//					// (AvailableCommandNames.EQUIV_RATIO.getValue().equals(
//					// cmdName)) {
//					// equivRatio = ((CommandEquivRatioObdCommand) job
//					// .getCommand()).getRatio();
//					// addTableRow(cmdName, cmdResult);
//				} else if (AvailableCommandNames.THROTTLE_POS.getValue()
//						.equals(cmdName)) {
//					TextView throttlePositionTextView = (TextView) findViewById(R.id.throttle);
//					String throttlePos = ((ThrottlePositionObdCommand) job
//							.getCommand()).getFormattedResult();
//					throttlePositionTextView.setText("T. Pos: " + throttlePos);
//
//					try {
//						NumberFormat format = NumberFormat
//								.getInstance(Locale.GERMAN);
//						Number number;
//						number = format.parse(throttlePos);
//						throttlePositionMeasurement = number.doubleValue();
//					} catch (ParseException e) {
//						Log.e("obd", "parse exception throttle");
//						e.printStackTrace();
//					} catch (java.text.ParseException e) {
//						Log.e("obd", "parse exception throttle");
//						e.printStackTrace();
//					}
//
//					// try {
//					// throttlePositionMeasurement = Double
//					// .valueOf(throttlePos);
//					// Log.e("obd", "cast successful");
//					// } catch (NumberFormatException e) {
//					// Log.e("obd2", "number format exception");
//					// e.printStackTrace();
//					// }
//
//				} else if (AvailableCommandNames.FUEL_CONSUMPTION.getValue()
//						.equals(cmdName)) {
//					TextView fuelConsumptionTextView = (TextView) findViewById(R.id.fuelconsumption);
//					String fuelCon = ((FuelConsumptionObdCommand) job
//							.getCommand()).getFormattedResult();
//					fuelConsumptionTextView.setText(fuelCon + " l/h");
//					// Log.e("obd2", "Fuel consumption");
//					// Log.e("obd2", fuelCon);
//
//					try {
//						NumberFormat format = NumberFormat
//								.getInstance(Locale.GERMAN);
//						Number number;
//						number = format.parse(fuelCon);
//						fuelConsumptionMeasurement = number.doubleValue();
//					} catch (ParseException e) {
//						Log.e("obd", "parse exception fuelCon");
//						e.printStackTrace();
//					} catch (java.text.ParseException e) {
//						Log.e("obd", "parse exception fuelCon");
//						e.printStackTrace();
//					}
//					// fuelConsumptionMeasurement = Double.valueOf(fuelCon);
//					// } else if (AvailableCommandNames.FUEL_ECONOMY.getValue()
//					// .equals(cmdName)) {
//					// TextView fuelEconomyTextView = (TextView)
//					// findViewById(R.id.fueleconomy);
//					// String fuelEco = ((FuelEconomyObdCommand)
//					// job.getCommand())
//					// .getFormattedResult();
//					// fuelEconomyTextView.setText(fuelEco + " l/100km");
//
//					// } else if
//					// (AvailableCommandNames.FUEL_TYPE.getValue().equals(
//					// cmdName)) {
//					// TextView fuelTypeTextView = (TextView)
//					// findViewById(R.id.fuelTypeTextDisplay);
//					// String fuelType = ((FindFuelTypeObdCommand) job
//					// .getCommand()).getFormattedResult();
//					// try {
//					// fuelTypeTextView.setText("Fuel Type: " + fuelType);
//					// } catch (Exception e) {
//					// Log.e("obd2", "fuel type exception");
//					// e.printStackTrace();
//					// }
//				} else if (AvailableCommandNames.ENGINE_LOAD.getValue().equals(
//						cmdName)) {
//					TextView engineLoadTextView = (TextView) findViewById(R.id.engineLoadText);
//					String engineLoad = ((EngineLoadObdCommand) job
//							.getCommand()).getFormattedResult();
//					Log.e("obd2", "Engine Load: " + engineLoad);
//					engineLoadTextView.setText("Engine load: " + engineLoad);
//					try {
//						NumberFormat format = NumberFormat
//								.getInstance(Locale.GERMAN);
//						Number number;
//						number = format.parse(engineLoad);
//						engineLoadMeasurement = number.doubleValue();
//					} catch (ParseException e) {
//						Log.e("obd", "parse exception load");
//						e.printStackTrace();
//					} catch (java.text.ParseException e) {
//						Log.e("obd", "parse exception load");
//						e.printStackTrace();
//					}
//					// engineLoadMeasurement = Double.valueOf(engineLoad);
//				} else if (AvailableCommandNames.MAF.getValue().equals(cmdName)) {
//					TextView mafTextView = (TextView) findViewById(R.id.mafText);
//					String maf = ((MassAirFlowObdCommand) job.getCommand())
//							.getFormattedResult();
//					mafTextView.setText("MAF: " + maf + " g/s");
//					try {
//						NumberFormat format = NumberFormat
//								.getInstance(Locale.GERMAN);
//						Number number;
//						number = format.parse(maf);
//						mafMeasurement = number.doubleValue();
//					} catch (ParseException e) {
//						Log.e("obd", "parse exception maf");
//						e.printStackTrace();
//					} catch (java.text.ParseException e) {
//						Log.e("obd", "parse exception maf");
//						e.printStackTrace();
//					}
//					// mafMeasurement = Double.valueOf(maf);
//				} else if (AvailableCommandNames.INTAKE_MANIFOLD_PRESSURE
//						.getValue().equals(cmdName)) {
//					TextView intakePressureTextView = (TextView) findViewById(R.id.intakeText);
//					String intakePressure = ((IntakeManifoldPressureObdCommand) job
//							.getCommand()).getFormattedResult();
//					intakePressureTextView.setText("Intake: " + intakePressure
//							+ "kPa");
//					try {
//						intakePressureMeasurement = Integer
//								.valueOf(intakePressure);
//					} catch (NumberFormatException e) {
//						Log.e("obd", "intake pressure parse exception");
//						e.printStackTrace();
//					}
//				} else {
//					// addTableRow(cmdName, cmdResult);
//				}
//				updateMeasurement();
//			}
//
//		};
//
//		/*
//		 * Validate Bluetooth service.
//		 */
//		// Bluetooth device exists?
//		final BluetoothAdapter mBtAdapter = BluetoothAdapter
//				.getDefaultAdapter();
//		if (mBtAdapter == null) {
//			preRequisites = false;
//			showDialog(NO_BLUETOOTH_ID);
//		} else {
//			// Bluetooth device is enabled?
//			if (!mBtAdapter.isEnabled()) {
//				preRequisites = false;
//				showDialog(BLUETOOTH_DISABLED);
//			}
//		}
//
//		/*
//		 * Get Orientation sensor.
//		 */
//		// sensorManager = (SensorManager)
//		// getSystemService(Context.SENSOR_SERVICE);
//		// List<Sensor> sens = sensorManager
//		// .getSensorList(Sensor.TYPE_ORIENTATION);
//		// if (sens.size() <= 0) {
//		// showDialog(NO_ORIENTATION_SENSOR);
//		// } else {
//		// orientSensor = sens.get(0);
//		// }
//
//		// validate app pre-requisites
//		if (preRequisites) {
//			/*
//			 * Prepare service and its connection
//			 */
//			mServiceIntent = new Intent(this, ObdGatewayService.class);
//			mServiceConnection = new ObdGatewayServiceConnection();
//			mServiceConnection.setServiceListener(mListener);
//
//			// bind service
//			Log.d(TAG, "Binding service..");
//			bindService(mServiceIntent, mServiceConnection,
//					Context.BIND_AUTO_CREATE);
//		}
//	}
//
//	/**
//	 * Test method for some stuff
//	 */
//	private void doTests() {
//
//		/*
//		 * DB-Adapter
//		 */
//
//		try {
//			Measurement testMeasurement = new Measurement((float) 0.0,
//					(float) 0.0);
//			Log.e("obd2", testMeasurement.toString());
//		} catch (LocationInvalidException e) {
//			e.printStackTrace();
//		}
//
//		Measurement testMeasurement2 = null;
//
//		try {
//			testMeasurement2 = new Measurement((float) 52.9542, (float) 7.6598);
//			testMeasurement2.setRpm(1000);
//			testMeasurement2.setSpeed(100);
//			testMeasurement2.setThrottlePosition(50.0);
//			testMeasurement2.setEngineLoad(7.4);
//			testMeasurement2.setFuelConsumption(13.1);
//			testMeasurement2.setIntakePressure(5);
//			testMeasurement2.setIntakeTemperature(43);
//			testMeasurement2.setShortTermTrimBank1(1.5);
//			testMeasurement2.setLongTermTrimBank1(4.5);
//			testMeasurement2.setMaf(13.2);
//
//			Log.e("obd2", testMeasurement2.toString());
//		} catch (LocationInvalidException e) {
//			e.printStackTrace();
//		}
//
//		if (testMeasurement2 != null) {
//			dbAdapter.insertMeasurement(testMeasurement2);
//		}
//
//		ArrayList<Measurement> allMeasurements = dbAdapter.getAllMeasurements();
//
//		for (Measurement measurement : allMeasurements) {
//			Log.e("obd2", measurement.toString());
//		}
//
//		// dbAdapter.deleteAllMeasurements();
//
//		// ArrayList<Measurement> allMeasurements2 = dbAdapter
//		// .getAllMeasurements();
//		//
//		// Log.e("obd2", String.valueOf(allMeasurements2.size()));
//		//
//		// for (Measurement measurement : allMeasurements2) {
//		// Log.e("obd2", measurement.toString());
//		// }
//
//		dbAdapter.close();
//	}
//
//	private void initDbAdapter() {
//		dbAdapter = new DbAdapterLocal(getApplicationContext());
//		dbAdapter.open();
//	}
//
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//
//		dbAdapter.close();
//
//		releaseWakeLockIfHeld();
//		mServiceIntent = null;
//		mServiceConnection = null;
//		mListener = null;
//		mHandler = null;
//
//	}
//
//	@Override
//	protected void onPause() {
//		super.onPause();
//		locationManager.removeUpdates(this);
//		dbAdapter.close();
//		Log.d(TAG, "Pausing..");
//		releaseWakeLockIfHeld();
//	}
//
//	/**
//	 * If lock is held, release. Lock will be held when the service is running.
//	 */
//	private void releaseWakeLockIfHeld() {
//		if (wakeLock.isHeld()) {
//			wakeLock.release();
//		}
//	}
//
//	/**
//	 * Init the location Manager with the user's choice of the location method
//	 */
//	private void initLocationManager() {
//
//		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
//				0, this);
//
//		// changeLocationMethod();
//	}
//
//	protected void onResume() {
//		super.onResume();
//
//		initLocationManager();
//
//		initDbAdapter();
//
//		/*
//		 * Tests
//		 */
//
//		// doTests();
//
//		/*
//		 * End Tests
//		 */
//
//		Log.d(TAG, "Resuming..");
//
//		// sensorManager.registerListener(orientListener, orientSensor,
//		// SensorManager.SENSOR_DELAY_UI);
//		prefs = PreferenceManager.getDefaultSharedPreferences(this);
//		powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
//		wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
//				"ObdReader");
//	}
//
//	private void updateConfig() {
//		Intent configIntent = new Intent(this, ConfigActivity.class);
//		startActivity(configIntent);
//	}
//
//	public boolean onCreateOptionsMenu(Menu menu) {
//		menu.add(0, START_LIVE_DATA, 0, "Start Live Data");
//		// menu.add(0, COMMAND_ACTIVITY, 0, "Run Command");
//		menu.add(0, STOP_LIVE_DATA, 0, "Stop");
//		menu.add(0, START_LIST_VIEW, 0, "List");
//		menu.add(0, SETTINGS, 0, "Settings");
//		return true;
//	}
//
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//		case START_LIVE_DATA:
//			startLiveData();
//			return true;
//		case STOP_LIVE_DATA:
//			stopLiveData();
//			return true;
//		case SETTINGS:
//			updateConfig();
//			return true;
//			// case COMMAND_ACTIVITY:
//			// staticCommand();
//			// return true;
//		case START_LIST_VIEW:
//			startListView();
//			return true;
//		}
//		return false;
//	}
//
//	private void startListView() {
//		Intent listIntent = new Intent(this, ListMeasurementsActivity.class);
//		startActivity(listIntent);
//
//	}
//
//	// private void staticCommand() {
//	// Intent commandIntent = new Intent(this, ObdReaderCommandActivity.class);
//	// startActivity(commandIntent);
//	// }
//
//	private void startLiveData() {
//		Log.d(TAG, "Starting live data..");
//
//		if (!mServiceConnection.isRunning()) {
//			Log.d(TAG, "Service is not running. Going to start it..");
//			startService(mServiceIntent);
//		}
//
//		// start command execution
//		mHandler.post(mQueueCommands);
//
//		// screen won't turn off until wakeLock.release()
//		wakeLock.acquire();
//	}
//
//	private void stopLiveData() {
//		Log.d(TAG, "Stopping live data..");
//
//		if (mServiceConnection.isRunning())
//			stopService(mServiceIntent);
//
//		// remove runnable
//		mHandler.removeCallbacks(mQueueCommands);
//
//		releaseWakeLockIfHeld();
//	}
//
//	protected Dialog onCreateDialog(int id) {
//		AlertDialog.Builder build = new AlertDialog.Builder(this);
//		switch (id) {
//		case NO_BLUETOOTH_ID:
//			build.setMessage("Sorry, your device doesn't support Bluetooth.");
//			return build.create();
//		case BLUETOOTH_DISABLED:
//			build.setMessage("You have Bluetooth disabled. Please enable it!");
//			return build.create();
//			// case NO_GPS_ID:
//			// build.setMessage("Sorry, your device doesn't support GPS.");
//			// return build.create();
//			// case NO_ORIENTATION_SENSOR:
//			// build.setMessage("Orientation sensor missing?");
//			// return build.create();
//		}
//		return null;
//	}
//
//	public boolean onPrepareOptionsMenu(Menu menu) {
//		MenuItem startItem = menu.findItem(START_LIVE_DATA);
//		MenuItem stopItem = menu.findItem(STOP_LIVE_DATA);
//		MenuItem settingsItem = menu.findItem(SETTINGS);
//		// MenuItem commandItem = menu.findItem(COMMAND_ACTIVITY);
//
//		// validate if preRequisites are satisfied.
//		if (preRequisites) {
//			if (mServiceConnection.isRunning()) {
//				startItem.setEnabled(false);
//				stopItem.setEnabled(true);
//				settingsItem.setEnabled(false);
//				// commandItem.setEnabled(false);
//			} else {
//				stopItem.setEnabled(false);
//				startItem.setEnabled(true);
//				settingsItem.setEnabled(true);
//				// commandItem.setEnabled(false);
//			}
//		} else {
//			startItem.setEnabled(false);
//			stopItem.setEnabled(false);
//			settingsItem.setEnabled(false);
//			// commandItem.setEnabled(false);
//		}
//
//		return true;
//	}
//
//	// private void addTableRow(String key, String val) {
//	// TableLayout tl = (TableLayout) findViewById(R.id.data_table);
//	// TableRow tr = new TableRow(this);
//	// MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
//	// LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//	// params.setMargins(TABLE_ROW_MARGIN, TABLE_ROW_MARGIN, TABLE_ROW_MARGIN,
//	// TABLE_ROW_MARGIN);
//	// tr.setLayoutParams(params);
//	// tr.setBackgroundColor(Color.BLACK);
//	// TextView name = new TextView(this);
//	// name.setGravity(Gravity.RIGHT);
//	// name.setText(key + ": ");
//	// TextView value = new TextView(this);
//	// value.setGravity(Gravity.LEFT);
//	// value.setText(val);
//	// tr.addView(name);
//	// tr.addView(value);
//	// tl.addView(tr, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
//	// LayoutParams.WRAP_CONTENT));
//	//
//	// /*
//	// * TODO remove this hack
//	// *
//	// * let's define a limit number of rows
//	// */
//	// if (tl.getChildCount() > 10)
//	// tl.removeViewAt(0);
//	// }
//
//	/**
//	 * 
//	 */
//	private Runnable mQueueCommands = new Runnable() {
//		public void run() {
//			/*
//			 * If values are not default, then we have values to calculate MPG
//			 */
//			Log.d(TAG, "SPD:" + speed + ", MAF:" + maf + ", LTFT:" + ltft);
//			if (speed > 1 && maf > 1 && ltft != 0) {
//				FuelEconomyWithMAFObdCommand fuelEconCmd = new FuelEconomyWithMAFObdCommand(
//						FuelType.DIESEL, speed, maf, ltft, false /* TODO */);
//				// TextView tvMpg = (TextView)
//				// findViewById(R.id.fuel_econ_text);
//				String liters100km = String.format("%.2f",
//						fuelEconCmd.getLitersPer100Km());
//				// tvMpg.setText("" + liters100km);
//				Log.d(TAG, "FUELECON:" + liters100km);
//			}
//
//			if (mServiceConnection.isRunning())
//				queueCommands();
//
//			// run again in 2s
//			mHandler.postDelayed(mQueueCommands, 2000);
//		}
//	};
//
//	/**
//	 * 
//	 */
//	private void queueCommands() {
//		// final ObdCommandJob airTemp = new ObdCommandJob(
//		// new AmbientAirTemperatureObdCommand());
//		final ObdCommandJob speed = new ObdCommandJob(new SpeedObdCommand());
//		// final ObdCommandJob fuelEcon = new ObdCommandJob(
//		// new FuelEconomyObdCommand());
//		final ObdCommandJob rpm = new ObdCommandJob(new EngineRPMObdCommand());
//		// final ObdCommandJob maf = new ObdCommandJob(new
//		// MassAirFlowObdCommand());
//		// final ObdCommandJob fuelLevel = new ObdCommandJob(
//		// new FuelLevelObdCommand());
//		final ObdCommandJob ltft1 = new ObdCommandJob(new FuelTrimObdCommand(
//				FuelTrim.LONG_TERM_BANK_1));
//		// final ObdCommandJob ltft2 = new ObdCommandJob(new FuelTrimObdCommand(
//		// FuelTrim.LONG_TERM_BANK_2));
//		final ObdCommandJob stft1 = new ObdCommandJob(new FuelTrimObdCommand(
//				FuelTrim.SHORT_TERM_BANK_1));
//		// final ObdCommandJob stft2 = new ObdCommandJob(new FuelTrimObdCommand(
//		// FuelTrim.SHORT_TERM_BANK_2));
//		// final ObdCommandJob equiv = new ObdCommandJob(
//		// new CommandEquivRatioObdCommand());
//		final ObdCommandJob throttle = new ObdCommandJob(
//				new ThrottlePositionObdCommand());
//		final ObdCommandJob fuelconsumption = new ObdCommandJob(
//				new FuelConsumptionObdCommand());
//		// final ObdCommandJob fueleconomy = new ObdCommandJob(
//		// new FuelEconomyObdCommand());
//		// final ObdCommandJob fuelTypeCommand = new ObdCommandJob(
//		// new FindFuelTypeObdCommand());
//		final ObdCommandJob engineLoad = new ObdCommandJob(
//				new EngineLoadObdCommand());
//		final ObdCommandJob maf = new ObdCommandJob(new MassAirFlowObdCommand());
//		final ObdCommandJob intakePressure = new ObdCommandJob(
//				new IntakeManifoldPressureObdCommand());
//		final ObdCommandJob intakeTemperature = new ObdCommandJob(
//				new AirIntakeTemperatureObdCommand());
//
//		// mServiceConnection.addJobToQueue(airTemp);
//		mServiceConnection.addJobToQueue(speed);
//		// mServiceConnection.addJobToQueue(fuelEcon);
//		mServiceConnection.addJobToQueue(rpm);
//		// mServiceConnection.addJobToQueue(maf);
//		// mServiceConnection.addJobToQueue(fuelLevel);
//		// // mServiceConnection.addJobToQueue(equiv);
//		mServiceConnection.addJobToQueue(ltft1);
//		mServiceConnection.addJobToQueue(throttle);
//		mServiceConnection.addJobToQueue(fuelconsumption);
//		// mServiceConnection.addJobToQueue(fueleconomy);
//		// mServiceConnection.addJobToQueue(fuelTypeCommand);
//		mServiceConnection.addJobToQueue(engineLoad);
//		mServiceConnection.addJobToQueue(maf);
//		mServiceConnection.addJobToQueue(intakePressure);
//		// mServiceConnection.addJobToQueue(ltft2);
//		mServiceConnection.addJobToQueue(stft1);
//		// mServiceConnection.addJobToQueue(stft2);
//		mServiceConnection.addJobToQueue(intakeTemperature);
//	}
//
//	@Override
//	public void onLocationChanged(Location location) {
//
//		locationLatitude = (float) location.getLatitude();
//
//		locationLatitudeTextView.setText(String.valueOf(locationLatitude));
//
//		locationLongitude = (float) location.getLongitude();
//
//		locationLongitudeTextView.setText(String.valueOf(locationLongitude));
//
//	}
//
//	@Override
//	public void onProviderDisabled(String provider) {
//		Toast.makeText(getApplicationContext(), "Gps Disabled",
//				Toast.LENGTH_SHORT).show();
//
//	}
//
//	@Override
//	public void onProviderEnabled(String provider) {
//		Toast.makeText(getApplicationContext(), "Gps Enabled",
//				Toast.LENGTH_SHORT).show();
//
//	}
//
//	@Override
//	public void onStatusChanged(String provider, int status, Bundle extras) {
//
//	}
//
//}