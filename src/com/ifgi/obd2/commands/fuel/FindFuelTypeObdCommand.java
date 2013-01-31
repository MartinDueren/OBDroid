/*
 * TODO put header 
 */
package com.ifgi.obd2.commands.fuel;

import com.ifgi.obd2.commands.ObdCommand;
import com.ifgi.obd2.commands.utils.ObdUtils;
import com.ifgi.obd2.enums.AvailableCommandNames;

/**
 * This command is intended to determine the vehicle fuel type.
 */
public class FindFuelTypeObdCommand extends ObdCommand {

	private int fuelType = 0;

	/**
	 * Default ctor.
	 */
	public FindFuelTypeObdCommand() {
		super("01 51");
	}

	/**
	 * Copy ctor
	 * 
	 * @param other
	 */
	public FindFuelTypeObdCommand(ObdCommand other) {
		super(other);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.lighthouselabs.obd.command.ObdCommand#getFormattedResult()
	 */
	@Override
	public String getFormattedResult() {
		String res = getResult();

		if (!"NODATA".equals(res)) {
			// ignore first two bytes [hh hh] of the response
			fuelType = buffer.get(2);
			res = getFuelTypeName();
		}

		return res;
	}

	/**
	 * @return Fuel type name.
	 */
	public final String getFuelTypeName() {
		return ObdUtils.getFuelTypeName(fuelType);
	}

	@Override
	public String getName() {
		return AvailableCommandNames.FUEL_TYPE.getValue();
	}

}