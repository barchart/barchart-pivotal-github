package com.barchart.pivotal.hook;

import java.util.List;

public class Event extends Any {

	public String kind;

	public Project project;

	public User performed_by;

	public List<Change> changes;

	public List<Resource> primary_resources;

	/**
	 * Find first story resource.
	 */
	public Resource storyResource() {
		if (primary_resources == null) {
			return null;
		}
		for (final Resource resource : primary_resources) {
			if ("story".equals(resource.kind)) {
				return resource;
			}
		}
		return null;
	}

}
