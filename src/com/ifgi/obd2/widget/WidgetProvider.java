package com.ifgi.obd2.widget;

import com.ifgi.obd2.IPostListener;
import com.ifgi.obd2.R;
import com.ifgi.obd2.commands.SpeedObdCommand;
import com.ifgi.obd2.commands.engine.EngineLoadObdCommand;
import com.ifgi.obd2.commands.engine.EngineRPMObdCommand;
import com.ifgi.obd2.commands.engine.MassAirFlowObdCommand;
import com.ifgi.obd2.commands.engine.ThrottlePositionObdCommand;
import com.ifgi.obd2.commands.fuel.FuelConsumptionObdCommand;
import com.ifgi.obd2.commands.fuel.FuelTrimObdCommand;
import com.ifgi.obd2.commands.pressure.IntakeManifoldPressureObdCommand;
import com.ifgi.obd2.commands.temperature.AirIntakeTemperatureObdCommand;
import com.ifgi.obd2.enums.FuelTrim;
import com.ifgi.obd2.io.ObdCommandJob;
import com.ifgi.obd2.io.ObdGatewayService;
import com.ifgi.obd2.io.ObdGatewayServiceConnection;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class WidgetProvider extends AppWidgetProvider{

	private static final String TAG = "WidgetProvider";
	public static String START_TRIP = "START_TRIP";
	public static String UPLOAD_TRIP = "UPLOAD_TRIP";
	RemoteViews views = null;
	public static boolean tripStarted = false;
	private Handler mHandler = new Handler();
	private Intent mServiceIntent = null;
	private IPostListener mListener = null;
	private ObdGatewayServiceConnection mServiceConnection = null;

	
	private Runnable mQueueCommands = new Runnable() {
		public void run() {
			/*
			 * If values are not default, then we have values to calculate MPG
			 */
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

			if (mServiceConnection.isRunning())
				queueCommands();

			// run again in 2s
			mHandler.postDelayed(mQueueCommands, 2000);
		}
	};
	
	
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    	
    	super.onUpdate(context, appWidgetManager, appWidgetIds);

		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];
			updateAppWidget(context, appWidgetManager, appWidgetId, null);
        }
    }
    
    
    public static void updateAppWidget(Context context,
			AppWidgetManager appWidgetManager, int appWidgetId, String titlePrefix) {
    	
		RemoteViews updateViews = new RemoteViews(context.getPackageName(),
				R.layout.widget_layout);
		
		
		// Create an Intent to launch ExampleActivity
        Intent configIntent = new Intent(context, com.ifgi.obd2.activity.ConfigActivity.class);
        PendingIntent configPending = PendingIntent.getActivity(context, 0, configIntent, 0);

        // Get the layout for the App Widget and attach an on-click listener
        // to the button
        updateViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        updateViews.setOnClickPendingIntent(R.id.wrench, configPending);
        
        
        Intent tripStartIntent = new Intent(WidgetProvider.START_TRIP);
        PendingIntent tripStartPending = PendingIntent.getBroadcast(context, 0, tripStartIntent, 0);
        
        updateViews.setOnClickPendingIntent(R.id.play, tripStartPending);
        
        Intent uploadIntent = new Intent(WidgetProvider.UPLOAD_TRIP);
        PendingIntent uploadPending = PendingIntent.getBroadcast(context, 0, uploadIntent, 0);
        
        updateViews.setOnClickPendingIntent(R.id.upload, uploadPending);
		
		
		
		if(tripStarted){
			updateViews.setImageViewResource(R.id.play, R.drawable.pause);
		}else{
			updateViews.setImageViewResource(R.id.play, R.drawable.play);
		}

		appWidgetManager.updateAppWidget(appWidgetId, updateViews);
    
    }
    
    public void customUpdate(Context context){
		AppWidgetManager appWidgetManager = AppWidgetManager
		.getInstance(context);
		ComponentName thisAppWidget = new ComponentName(
				context.getPackageName(),
				WidgetProvider.class.getName());
		int[] appWidgetIds = appWidgetManager
		.getAppWidgetIds(thisAppWidget);

		onUpdate(context, appWidgetManager, appWidgetIds);
	
	}
    
    public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);

		if (START_TRIP.equals(intent.getAction())) {
			try {
				if(!tripStarted){
					Toast.makeText(context.getApplicationContext(), "Start Trip", Toast.LENGTH_LONG).show();
					Log.d(TAG, "Starting Trip recording");
					initPrerequisites(context);
					startLiveData(context);
					tripStarted = true;
					customUpdate(context);
				}else{
					tripStarted = false;
					customUpdate(context);
					Toast.makeText(context.getApplicationContext(), "Stop Trip" + tripStarted, Toast.LENGTH_LONG).show();
					Log.d(TAG, "Stopping Trip recording");
					stopLiveData(context);
					
				}
			} catch (Exception e) {
				Log.e(TAG, "Trip Start failed");
				e.printStackTrace();
			}
		}else if(UPLOAD_TRIP.equals(intent.getAction())){
			//TODO			
		}
	}
    
    private void startLiveData(Context context) {
		Log.d(TAG, "Starting live data..");
		Log.d(TAG, "mServCon: "+mServiceConnection);
		if (!mServiceConnection.isRunning()) {
			Log.d(TAG, "Service is not running. Going to start it..");
			context.startService(mServiceIntent);
		}

		// start command execution
		Log.d(TAG, "Post Comm Queue");
		mHandler.post(mQueueCommands);
	}

	private void stopLiveData(Context context) {
		Log.d(TAG, "Stopping live data..");

		if (mServiceConnection.isRunning())
			context.stopService(mServiceIntent);

		// remove runnable
		mHandler.removeCallbacks(mQueueCommands);
	}
	
	
	
	private void queueCommands() {
		final ObdCommandJob speed = new ObdCommandJob(new SpeedObdCommand());
		final ObdCommandJob rpm = new ObdCommandJob(new EngineRPMObdCommand());
		final ObdCommandJob ltft1 = new ObdCommandJob(new FuelTrimObdCommand(FuelTrim.LONG_TERM_BANK_1));
		final ObdCommandJob stft1 = new ObdCommandJob(new FuelTrimObdCommand(FuelTrim.SHORT_TERM_BANK_1));;
		final ObdCommandJob throttle = new ObdCommandJob(new ThrottlePositionObdCommand());
		final ObdCommandJob fuelconsumption = new ObdCommandJob(new FuelConsumptionObdCommand());
		final ObdCommandJob engineLoad = new ObdCommandJob(new EngineLoadObdCommand());
		final ObdCommandJob maf = new ObdCommandJob(new MassAirFlowObdCommand());
		final ObdCommandJob intakePressure = new ObdCommandJob(new IntakeManifoldPressureObdCommand());
		final ObdCommandJob intakeTemperature = new ObdCommandJob(new AirIntakeTemperatureObdCommand());
		
		mServiceConnection.addJobToQueue(speed);
		mServiceConnection.addJobToQueue(rpm);
		mServiceConnection.addJobToQueue(ltft1);
		mServiceConnection.addJobToQueue(throttle);
		mServiceConnection.addJobToQueue(fuelconsumption);
		mServiceConnection.addJobToQueue(engineLoad);
		mServiceConnection.addJobToQueue(maf);
		mServiceConnection.addJobToQueue(intakePressure);
		mServiceConnection.addJobToQueue(stft1);
		mServiceConnection.addJobToQueue(intakeTemperature);
	}
	
	public void initPrerequisites(Context context){
		Log.d(TAG, "Initializing Prereqs");
		/*
		 * Prepare service and its connection
		 */
		mListener = new IPostListener(context);
		mServiceIntent = new Intent(context.getApplicationContext(), ObdGatewayService.class);
		mServiceConnection = new ObdGatewayServiceConnection();
		mServiceConnection.setServiceListener(mListener);		
	}
	
	public boolean bluetoothCheck(Context context){
		
		final BluetoothAdapter mBtAdapter = BluetoothAdapter
				.getDefaultAdapter();
		if (mBtAdapter == null) {
//			preRequisites = false;
//			showDialog(NO_BLUETOOTH_ID);
			Toast.makeText(context.getApplicationContext(), "Keine Bluetooth ID", Toast.LENGTH_LONG).show();
			return false;
		} else if (!mBtAdapter.isEnabled()) {
//				preRequisites = false;
//				showDialog(BLUETOOTH_DISABLED);
			Toast.makeText(context.getApplicationContext(), "Kein Bluetooth, bitte aktivieren", Toast.LENGTH_LONG).show();
			return false;
		} else return true;		
	}
	
	
}
	

