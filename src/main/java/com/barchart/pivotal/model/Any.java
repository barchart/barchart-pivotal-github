package com.barchart.pivotal.model;

import com.barchart.pivotal.util.UtilGson;

/**
 * 
 */
// https://www.pivotaltracker.com/help/api/rest/v5
public class Any {

	@Override
	public String toString() {
		return UtilGson.getGson().toJson(this);
	}

}
