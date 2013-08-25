package com.barchart.pivotal.model;

import java.lang.reflect.Type;
import java.util.List;

import org.joda.time.DateTime;

import com.google.gson.reflect.TypeToken;

/**
 * 
 */
// https://www.pivotaltracker.com/help/api/rest/v5#label_resource
public class Integration extends Any {

	public static final Type LIST_TYPE = new TypeToken<List<Integration>>() {
	}.getType();

	/** immutable */
	public Integer id;

	public String name;

	public Integer project_id;

	public Boolean active;

	public DateTime created_at;
	public DateTime updated_at;

	@Override
	public boolean equals(final Object other) {
		if (other instanceof Integration) {
			return ((Integration) other).id == id;
		}
		return false;
	}

}
