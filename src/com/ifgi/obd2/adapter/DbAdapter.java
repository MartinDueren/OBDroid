package com.ifgi.obd2.adapter;

import java.util.ArrayList;

public interface DbAdapter {

	public DbAdapter open();

	public void close();

	public void insertMeasurement(Measurement measurement);

	public ArrayList<Measurement> getAllMeasurements();

	public Measurement getMeasurement(int id);

	public void deleteAllMeasurements();

}
