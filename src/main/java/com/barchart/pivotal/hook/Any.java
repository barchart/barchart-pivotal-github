package com.barchart.pivotal.hook;

import com.barchart.pivotal.util.UtilGson;

/**
 * 
 */
public abstract class Any {

	/**
	 * Render as JSON.
	 */
	@Override
	public String toString() {
		return UtilGson.getGson().toJson(this);
	}

}
