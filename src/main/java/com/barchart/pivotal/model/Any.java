package com.barchart.pivotal.model;

import com.barchart.pivotal.util.UtilGson;

/**
 * 
 */
// https://www.pivotaltracker.com/help/api/rest/v5
public abstract class Any {

	/**
	 * Render as JSON.
	 */
	@Override
	public String toString() {
		return UtilGson.getGson().toJson(this);
	}

	/**
	 * Equality by persistent id.
	 */
	@Override
	public abstract boolean equals(Object other);

}
