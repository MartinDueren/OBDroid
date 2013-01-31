/*
 * TODO put header
 */
package com.ifgi.obd2.commands.protocol;

import com.ifgi.obd2.commands.ObdCommand;
import com.ifgi.obd2.enums.ObdProtocols;

/**
 * Select the protocol to use.
 */
public class SelectProtocolObdCommand extends ObdCommand {

	private final ObdProtocols _protocol;

	/**
	 * @param command
	 */
	public SelectProtocolObdCommand(ObdProtocols protocol) {
		super("AT SP " + protocol.getValue());
		_protocol = protocol;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.lighthouselabs.obd.commands.ObdCommand#getFormattedResult()
	 */
	@Override
	public String getFormattedResult() {
		return getResult();
	}

	@Override
	public String getName() {
		return "Select Protocol " + _protocol.name();
	}

}