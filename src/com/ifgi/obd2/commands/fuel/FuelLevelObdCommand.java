/*
 * TODO put header 
 */
package com.ifgi.obd2.commands.fuel;

import com.ifgi.obd2.commands.ObdCommand;
import com.ifgi.obd2.enums.AvailableCommandNames;

/**
 * Get fuel level in percentage
 */
public class FuelLevelObdCommand extends ObdCommand {

	private float fuelLevel = 0f;

	/**
	 * @param command
	 */
	public FuelLevelObdCommand() {
		super("01 2F");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.lighthouselabs.obd.commands.ObdCommand#getFormattedResult()
	 */
	@Override
	public String getFormattedResult() {
		if (!"NODATA".equals(getResult())) {
			// ignore first two bytes [hh hh] of the response
			fuelLevel = 100.0f * buffer.get(2) / 255.0f;
		}

		return String.format("%.1f%s", fuelLevel, "%");
	}

	@Override
	public String getName() {
		return AvailableCommandNames.FUEL_LEVEL.getValue();
	}

}