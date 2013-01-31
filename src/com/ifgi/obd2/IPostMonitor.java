/*
 * TODO put header 
 */
package com.ifgi.obd2;

import com.ifgi.obd2.io.ObdCommandJob;


/**
 * TODO put description
 */
public interface IPostMonitor {
	void setListener(IPostListener callback);

	boolean isRunning();

	void executeQueue();
	
	void addJobToQueue(ObdCommandJob job);
}