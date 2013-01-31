/*
 * TODO put header 
 */
package com.ifgi.obd2.commands.fuel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.ifgi.obd2.commands.ObdCommand;
import com.ifgi.obd2.commands.SpeedObdCommand;
import com.ifgi.obd2.commands.control.CommandEquivRatioObdCommand;
import com.ifgi.obd2.commands.engine.EngineRPMObdCommand;
import com.ifgi.obd2.commands.pressure.IntakeManifoldPressureObdCommand;
import com.ifgi.obd2.commands.temperature.AirIntakeTemperatureObdCommand;

/**
 * TODO put description
 */
public class FuelEconomyWithoutMAFObdCommand extends ObdCommand {

	public static final double AIR_FUEL_RATIO = 14.64;
	public static final double FUEL_DENSITY_GRAMS_PER_LITER = 720.0;

	public FuelEconomyWithoutMAFObdCommand() {
		super("");
	}

	/**
	 * As it's a fake command, neither do we need to send request or read
	 * response.
	 */
	@Override
	public void run(InputStream in, OutputStream out) throws IOException,
			InterruptedException {
		// prepare variables
		EngineRPMObdCommand rpmCmd = new EngineRPMObdCommand();
		rpmCmd.run(in, out);
		rpmCmd.getFormattedResult();

		AirIntakeTemperatureObdCommand airTempCmd = new AirIntakeTemperatureObdCommand();
		airTempCmd.run(in, out);
		airTempCmd.getFormattedResult();

		SpeedObdCommand speedCmd = new SpeedObdCommand();
		speedCmd.run(in, out);
		speedCmd.getFormattedResult();

		CommandEquivRatioObdCommand equivCmd = new CommandEquivRatioObdCommand();
		equivCmd.run(in, out);
		equivCmd.getFormattedResult();

		IntakeManifoldPressureObdCommand pressCmd = new IntakeManifoldPressureObdCommand();
		pressCmd.run(in, out);
		pressCmd.getFormattedResult();

		double imap = rpmCmd.getRPM() * pressCmd.getMetricUnit()
				/ airTempCmd.getKelvin();
		// double maf = (imap / 120) * (speedCmd.getMetricSpeed()/100)*()

	}

	@Override
	public String getFormattedResult() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
