/*
 * TODO put header
 */
package com.ifgi.obd2.commands.engine;

import com.ifgi.obd2.commands.ObdCommand;
import com.ifgi.obd2.enums.AvailableCommandNames;

/**
 * Displays the current engine revolutions per minute (RPM).
 */
public class EngineRPMObdCommand extends ObdCommand {

	private int _rpm = -1;

	/**
	 * Default ctor.
	 */
	public EngineRPMObdCommand() {
		super("01 0C");
	}

	/**
	 * Copy ctor.
	 * 
	 * @param other
	 */
	public EngineRPMObdCommand(EngineRPMObdCommand other) {
		super(other);
	}

	/**
	 * @return the engine RPM per minute
	 */
	@Override
	public String getFormattedResult() {
		if (!"NODATA".equals(getResult())) {
			// ignore first two bytes [41 0C] of the response
			int a = buffer.get(2);
			int b = buffer.get(3);
			_rpm = (a * 256 + b) / 4;
		}

		return String.format("%d%s", _rpm, "");
	}

	@Override
	public String getName() {
		return AvailableCommandNames.ENGINE_RPM.getValue();
	}

	public int getRPM() {
		return _rpm;
	}
}