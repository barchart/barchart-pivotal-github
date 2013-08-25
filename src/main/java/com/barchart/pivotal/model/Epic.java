package com.barchart.pivotal.model;

import java.lang.reflect.Type;
import java.util.List;

import org.joda.time.DateTime;

import com.barchart.pivotal.util.UtilURL;
import com.google.gson.reflect.TypeToken;

/**
 * 
 */
// https://www.pivotaltracker.com/help/api/rest/v5#epic_resource
public class Epic extends Any {

	public static final Type LIST_TYPE = new TypeToken<List<Epic>>() {
	}.getType();

	public static final String FIELD_ALL = UtilURL.modelFields(Epic.class);

	public static final String FIELD_HEAD = "fields=id,project_id";

	/** immutable */
	public Integer id;

	public Integer project_id;

	public String name;

	public String description;

	/** immutable */
	public String url;

	public Label label;

	public List<Comment> comments;

	public DateTime created_at;
	public DateTime updated_at;

	@Override
	public boolean equals(final Object other) {
		if (other instanceof Epic) {
			return ((Epic) other).id == id;
		}
		return false;
	}

	/**
	 * Equality by name. Epics name must be unique in a project.
	 */
	public boolean equalsName(final Epic that) {
		if (this.name == null) {
			return false;
		}
		return this.name.equals(that.name);
	}

}
