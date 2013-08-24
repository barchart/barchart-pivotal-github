package com.barchart.pivotal.model;

import java.lang.reflect.Type;
import java.util.List;

import org.joda.time.DateTime;

import com.google.gson.reflect.TypeToken;

/**
 * 
 */
// https://www.pivotaltracker.com/help/api/rest/v5#epic_resource
public class Epic extends Any {

	public static final Type LIST_TYPE = new TypeToken<List<Epic>>() {
	}.getType();

	/** immutable */
	public Integer id;

	public String name;

	public String description;

	public Integer project_id;

	/** immutable */
	public String url;

	public Label label;

	public DateTime created_at;
	public DateTime updated_at;

}
