package com.barchart.github;

import java.util.Date;

import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.util.DateUtils;

import com.barchart.pivotal.util.UtilGson;

/**
 * Extend to reflect new Github API.
 */
public class MilestoneExtra extends Milestone {
	private static final long serialVersionUID = 1L;

	private Date updatedAt;

	/**
	 * 
	 */
	public Date getUpdatedAt() {
		return DateUtils.clone(updatedAt);
	}

	/**
	 * 
	 */
	public Milestone setUpdatedAt(final Date updatedAt) {
		this.updatedAt = DateUtils.clone(updatedAt);
		return this;
	}

	/**
	 * Render as JSON.
	 */
	@Override
	public String toString() {
		return UtilGson.getGson().toJson(this);
	}

}
