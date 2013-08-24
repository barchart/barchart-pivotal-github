package com.barchart.pivotal.model;

import java.lang.reflect.Type;
import java.util.List;

import org.joda.time.DateTime;

import com.google.gson.reflect.TypeToken;

/**
 * 
 */
// https://www.pivotaltracker.com/help/api/rest/v5#label_resource
public class Label extends Any {

	public static final Type LIST_TYPE = new TypeToken<List<Label>>() {
	}.getType();

	/** immutable */
	public Integer id;

	public String name;

	public Integer project_id;

	public DateTime created_at;
	public DateTime updated_at;

}
