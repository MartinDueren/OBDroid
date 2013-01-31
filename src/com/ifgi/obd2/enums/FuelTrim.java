/*
 * TODO put header
 */
package com.ifgi.obd2.enums;

/**
 * Select one of the Fuel Trim percentage banks to access.
 */
public enum FuelTrim {

	SHORT_TERM_BANK_1(6), LONG_TERM_BANK_1(7), SHORT_TERM_BANK_2(8), LONG_TERM_BANK_2(
			9);

	private final int value;

	/**
	 * 
	 * @param value
	 */
	private FuelTrim(int value) {
		this.value = value;
	}

	/**
	 * 
	 * @return
	 */
	public final int getValue() {
		return value;
	}

	/**
	 * 
	 * @return
	 */
	public final String getObdCommand() {
		return new String("01 0" + value);
	}

	public final String getBank() {
		String res = "NODATA";

		switch (value) {
		case 6:
			res = "Short Term Fuel Trim Bank 1";
			break;
		case 7:
			res = "Long Term Fuel Trim Bank 1";
			break;
		case 8:
			res = "Short Term Fuel Trim Bank 2";
			break;
		case 9:
			res = "Long Term Fuel Trim Bank 2";
			break;
		default:
			break;
		}

		return res;
	}

}