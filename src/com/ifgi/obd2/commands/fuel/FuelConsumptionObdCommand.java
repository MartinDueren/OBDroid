/*
 * TODO put header 
 */
package com.ifgi.obd2.commands.fuel;

import android.util.Log;

import com.ifgi.obd2.commands.ObdCommand;
import com.ifgi.obd2.enums.AvailableCommandNames;

/**
 * TODO put description
 */
public class FuelConsumptionObdCommand extends ObdCommand {

	private float fuelRate = -1.0f;

	public FuelConsumptionObdCommand() {
		super("01 5E");
	}

	public FuelConsumptionObdCommand(ObdCommand other) {
		super(other);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.lighthouselabs.obd.commands.ObdCommand#getFormattedResult()
	 */
	@Override
	public String getFormattedResult() {

		// Log.e("obd2", "get result");

		if (!"NODATA".equals(getResult())) {

			Log.e("obd2", "found data");

			// ignore first two bytes [hh hh] of the response

			Log.e("fuel", "length " + String.valueOf(buffer.size()));
			int c = buffer.get(0);
			Log.e("fuel", "c " + String.valueOf(c));
			int a = buffer.get(1);
			Log.e("fuel", "b " + String.valueOf(a));
			int b = buffer.get(2);
			Log.e("fuel", "a " + String.valueOf(b));
			fuelRate = (a * 256 + b) * 0.05f;

			Log.e("obd2", "fuel rate " + String.valueOf(fuelRate));

		}

		String res = String.format("%.1f%s", fuelRate, "");

		return res;
	}

	public float getLitersPerHour() {
		return fuelRate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.lighthouselabs.obd.commands.ObdCommand#getName()
	 */
	@Override
	public String getName() {
		return AvailableCommandNames.FUEL_CONSUMPTION.getValue();
	}

}
