/*
 * TODO put header
 */
package com.ifgi.obd2.commands.engine;

import com.ifgi.obd2.commands.ObdCommand;
import com.ifgi.obd2.commands.PercentageObdCommand;
import com.ifgi.obd2.enums.AvailableCommandNames;

/**
 * Calculated Engine Load value.
 */
public class EngineLoadObdCommand extends PercentageObdCommand {

	/**
	 * @param command
	 */
	public EngineLoadObdCommand() {
		super("01 04");
	}

	/**
	 * @param other
	 */
	public EngineLoadObdCommand(ObdCommand other) {
		super(other);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.lighthouselabs.obd.commands.ObdCommand#getName()
	 */
	@Override
	public String getName() {
		return AvailableCommandNames.ENGINE_LOAD.getValue();
	}

}