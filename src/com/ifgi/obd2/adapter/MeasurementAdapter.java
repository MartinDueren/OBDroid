package com.ifgi.obd2.adapter;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

public interface MeasurementAdapter {

	public void uploadMeasurement(Measurement measurement) throws ClientProtocolException, IOException;

}
