package com.barchart.pivotal.model;

import java.lang.reflect.Type;
import java.util.List;

import org.joda.time.DateTime;

import com.google.gson.reflect.TypeToken;

/**
 * 
 */
// https://www.pivotaltracker.com/help/api/rest/v5#comment_resource
public class Comment extends Any {

	public static final Type LIST_TYPE = new TypeToken<List<Comment>>() {
	}.getType();

	/** immutable */
	public Integer id;

	public String text;

	public Integer person_id;

	/** custom filed */
	public Integer project_id;
	/** link to story */
	public Integer story_id;
	/** link to epic */
	public Integer epic_id;

	/** some github type; works only for story, not epic */
	public String commit_type;
	/** some github identity; works only for story, not epic */
	public String commit_identifier;

	public DateTime created_at;
	public DateTime updated_at;

	@Override
	public boolean equals(final Object other) {
		if (other instanceof Comment) {
			return ((Comment) other).id == id;
		}
		return false;
	}

	/**
	 * Equality by commit coordinates.
	 */
	public boolean equalsCommit(final Comment that) {
		if (this.commit_type == null || this.commit_identifier == null) {
			return false;
		}
		return this.commit_type.equals(that.commit_type)
				&& this.commit_identifier.equals(that.commit_identifier);
	}

}
