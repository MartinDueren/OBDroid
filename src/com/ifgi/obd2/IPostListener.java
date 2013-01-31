/*
 * TODO put header 
 */
package com.ifgi.obd2;

import com.ifgi.obd2.io.ObdCommandJob;


/**
 * TODO put description
 */
public interface IPostListener {

	void stateUpdate(ObdCommandJob job);
	
}