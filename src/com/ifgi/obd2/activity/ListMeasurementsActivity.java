package com.ifgi.obd2.activity;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ifgi.obd2.R;
import com.ifgi.obd2.adapter.DbAdapterLocal;
import com.ifgi.obd2.adapter.Measurement;
import com.ifgi.obd2.adapter.MeasurementAdapterLocal;

public class ListMeasurementsActivity extends ListActivity {

	private DbAdapterLocal dbAdapter;
	private MeasurementAdapterLocal measurementAdapter;
	private ArrayList<Measurement> allMeasurementsList;
	private String[] measurements_short;
	private final int DELETE_ALL = 3;
	private final int UPLOAD_ALL = 4;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		initDbAdapter();

		fillListWithMeasurements();

		measurementAdapter = new MeasurementAdapterLocal();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, DELETE_ALL, 0, "Delete All");
		menu.add(0, UPLOAD_ALL, 0, "Upload All");
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_ALL:
			dbAdapter.deleteAllMeasurements();
			fillListWithMeasurements();
			return true;
		case UPLOAD_ALL:
			uploadAllMeasurements();
			dbAdapter.deleteAllMeasurements();
			fillListWithMeasurements();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void uploadAllMeasurements() {
		Log.e("obd2", "size: " + allMeasurementsList.size());
		for (Measurement measurement : allMeasurementsList) {
			Log.e("obd2", measurement.toString());
			try {
				measurementAdapter.uploadMeasurement(measurement);
			} catch (ClientProtocolException e) {
				Log.e("odb2", "upload failed");
				e.printStackTrace();
			} catch (IOException e) {
				Log.e("odb2", "upload failed 2");
				e.printStackTrace();
			}
		}

	}

	private void fillListWithMeasurements() {

		allMeasurementsList = dbAdapter.getAllMeasurements();

		// for (Measurement measurement : allMeasurementsList) {
		// Log.e("obd2", measurement.toString());
		// }

		measurements_short = new String[allMeasurementsList.size()];

		for (int i = 0; i < measurements_short.length; i++) {
			measurements_short[i] = "Own Measurement "
					+ allMeasurementsList.get(i).getId();
		}

		if (allMeasurementsList != null) {

			// Generate List

			setListAdapter(new ArrayAdapter<String>(this,
					R.layout.listmeasurements, measurements_short));
			ListView lv = getListView();
			lv.setTextFilterEnabled(true);

			lv.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					String selectedId = ((TextView) view).getText().toString();

					String[] split = selectedId.split(" ");

					selectedId = split[split.length - 1];

					Log.e("obd2", selectedId);

					for (Measurement measurement : allMeasurementsList) {

						Log.e("obd", "gehe durch");

						if (Integer.valueOf(selectedId) == measurement.getId()) {

							Log.e("obd2", "gefunden");

							Intent i;
							i = new Intent(ListMeasurementsActivity.this,
									MeasurementDisplay.class);
							Bundle bundle = new Bundle();
							bundle.putString("id",
									String.valueOf(measurement.getId()));
							i.putExtras(bundle);
							startActivity(i);
						}
					}
				}
			});

		}

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
