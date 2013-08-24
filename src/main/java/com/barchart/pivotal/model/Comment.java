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
	public Integer story_id;
	public Integer epic_id;

	/** github issue_comment id */
	public String commit_identifier;

	public DateTime created_at;
	public DateTime updated_at;

}
