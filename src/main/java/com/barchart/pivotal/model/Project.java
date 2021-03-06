package com.barchart.pivotal.model;

import java.lang.reflect.Type;
import java.util.List;

import org.joda.time.DateTime;

import com.google.gson.reflect.TypeToken;

/**
 * 
 */
// https://www.pivotaltracker.com/help/api/rest/v5#project_resource
public class Project extends Any {

	public static final Type LIST_TYPE = new TypeToken<List<Project>>() {
	}.getType();

	/** immutable */
	public Integer id;

	public Integer account_id;

	public String name;

	public String description;

	public Integer current_velocity;

	public String point_scale;

	public String profile_content;

	public DateTime created_at;
	public DateTime updated_at;

	@Override
	public boolean equals(final Object other) {
		if (other instanceof Project) {
			return ((Project) other).id == id;
		}
		return false;
	}

}
