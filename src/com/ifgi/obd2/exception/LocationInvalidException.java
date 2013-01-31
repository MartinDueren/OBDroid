package com.ifgi.obd2.exception;

public class LocationInvalidException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -630826885585474670L;

	public LocationInvalidException() {
		super(
				"Location Coordinates are invalid. Did you turn on GPS? Do you have a connection?");
	}
}
