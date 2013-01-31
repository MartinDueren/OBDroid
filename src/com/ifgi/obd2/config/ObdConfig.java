/*
 * TODO put header
 */
package com.ifgi.obd2.config;

import java.util.ArrayList;

import com.ifgi.obd2.commands.ObdCommand;
import com.ifgi.obd2.commands.SpeedObdCommand;
import com.ifgi.obd2.commands.control.CommandEquivRatioObdCommand;
import com.ifgi.obd2.commands.control.DtcNumberObdCommand;
import com.ifgi.obd2.commands.control.TimingAdvanceObdCommand;
import com.ifgi.obd2.commands.control.TroubleCodesObdCommand;
import com.ifgi.obd2.commands.engine.EngineLoadObdCommand;
import com.ifgi.obd2.commands.engine.EngineRPMObdCommand;
import com.ifgi.obd2.commands.engine.EngineRuntimeObdCommand;
import com.ifgi.obd2.commands.engine.MassAirFlowObdCommand;
import com.ifgi.obd2.commands.engine.ThrottlePositionObdCommand;
import com.ifgi.obd2.commands.fuel.FindFuelTypeObdCommand;
import com.ifgi.obd2.commands.fuel.FuelLevelObdCommand;
import com.ifgi.obd2.commands.fuel.FuelTrimObdCommand;
import com.ifgi.obd2.commands.pressure.BarometricPressureObdCommand;
import com.ifgi.obd2.commands.pressure.FuelPressureObdCommand;
import com.ifgi.obd2.commands.pressure.IntakeManifoldPressureObdCommand;
import com.ifgi.obd2.commands.protocol.ObdResetCommand;
import com.ifgi.obd2.commands.temperature.AirIntakeTemperatureObdCommand;
import com.ifgi.obd2.commands.temperature.AmbientAirTemperatureObdCommand;
import com.ifgi.obd2.commands.temperature.EngineCoolantTemperatureObdCommand;
import com.ifgi.obd2.enums.FuelTrim;

/**
 * TODO put description
 */
public final class ObdConfig {

	public static ArrayList<ObdCommand> getCommands() {
		ArrayList<ObdCommand> cmds = new ArrayList<ObdCommand>();
		// Protocol
		cmds.add(new ObdResetCommand());

		// Control
		cmds.add(new CommandEquivRatioObdCommand());
		cmds.add(new DtcNumberObdCommand());
		cmds.add(new TimingAdvanceObdCommand());
		cmds.add(new TroubleCodesObdCommand(0));

		// Engine
		cmds.add(new EngineLoadObdCommand());
		cmds.add(new EngineRPMObdCommand());
		cmds.add(new EngineRuntimeObdCommand());
		cmds.add(new MassAirFlowObdCommand());

		// Fuel
		// cmds.add(new AverageFuelEconomyObdCommand());
		// cmds.add(new FuelEconomyObdCommand());
		// cmds.add(new FuelEconomyMAPObdCommand());
		// cmds.add(new FuelEconomyCommandedMAPObdCommand());
		cmds.add(new FindFuelTypeObdCommand());
		cmds.add(new FuelLevelObdCommand());
		cmds.add(new FuelTrimObdCommand(FuelTrim.LONG_TERM_BANK_1));
		cmds.add(new FuelTrimObdCommand(FuelTrim.LONG_TERM_BANK_2));
		cmds.add(new FuelTrimObdCommand(FuelTrim.SHORT_TERM_BANK_1));
		cmds.add(new FuelTrimObdCommand(FuelTrim.SHORT_TERM_BANK_2));

		// Pressure
		cmds.add(new BarometricPressureObdCommand());
		cmds.add(new FuelPressureObdCommand());
		cmds.add(new IntakeManifoldPressureObdCommand());

		// Temperature
		cmds.add(new AirIntakeTemperatureObdCommand());
		cmds.add(new AmbientAirTemperatureObdCommand());
		cmds.add(new EngineCoolantTemperatureObdCommand());

		// Misc
		cmds.add(new SpeedObdCommand());
		cmds.add(new ThrottlePositionObdCommand());

		return cmds;
	}

}