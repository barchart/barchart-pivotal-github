package com.barchart.pivotal.model;

import java.lang.reflect.Type;
import java.util.List;

import org.joda.time.DateTime;

import com.google.gson.reflect.TypeToken;

/**
 * 
 */
// https://www.pivotaltracker.com/help/api/rest/v5#story_resource
public class Story extends Any {

	public static final Type LIST_TYPE = new TypeToken<List<Story>>() {
	}.getType();

	public Integer id;

	public String name;

	public String description;

	public Float estimate;

	public String current_state;

	public String story_type;

	/** github issue id */
	public String external_id;

	public Integer project_id;

	/** immutable */
	public String url;

	public List<Label> labels;

	public DateTime created_at;
	public DateTime updated_at;

}
