package com.barchart.pivotal.model;

import java.lang.reflect.Type;
import java.util.List;

import org.joda.time.DateTime;

import com.barchart.pivotal.util.UtilURL;
import com.google.gson.reflect.TypeToken;

/**
 * 
 */
// https://www.pivotaltracker.com/help/api/rest/v5#story_resource
public class Story extends Any {

	public static final Type LIST_TYPE = new TypeToken<List<Story>>() {
	}.getType();

	public static final String FIELD_ALL = UtilURL.modelFields(Story.class);

	public static final String FIELD_HEAD = "fields=id,project_id";

	/** immutable */
	public Integer id;

	public Integer project_id;

	/**  */
	public Integer integration_id;
	/**  */
	public String external_id;

	/** immutable */
	public String url;

	public String name;

	public String description;

	public Float estimate;

	public String current_state;

	public String story_type;

	public List<Label> labels;

	public DateTime created_at;
	public DateTime updated_at;

	@Override
	public boolean equals(final Object other) {
		if (other instanceof Story) {
			return ((Story) other).id == id;
		}
		return false;
	}

}
