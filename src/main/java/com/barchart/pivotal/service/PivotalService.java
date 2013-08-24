package com.barchart.pivotal.service;

import java.io.IOException;
import java.util.List;

import com.barchart.pivotal.client.PivotalClient;
import com.barchart.pivotal.client.PivotalRequest;
import com.barchart.pivotal.client.PivotalResponse;
import com.barchart.pivotal.model.Epic;
import com.barchart.pivotal.model.Label;
import com.barchart.pivotal.model.Project;
import com.barchart.pivotal.model.Story;

/**
 * 
 */
public class PivotalService {

	private final PivotalClient client;

	/**
	 * 
	 */
	public PivotalService(final PivotalClient client) {
		this.client = client;
	}

	/**
	 * 
	 */
	// https://www.pivotaltracker.com/help/api/rest/v5#Epic
	public Epic epic(final int projectId, final int epicId) throws IOException {

		final StringBuilder uri = new StringBuilder();
		uri.append("/projects");
		uri.append("/").append(projectId);
		uri.append("/epics");
		uri.append("/").append(epicId);

		final PivotalRequest request = new PivotalRequest();
		request.setUri(uri);
		request.setType(Epic.class);

		final PivotalResponse response = client.get(request);

		final Epic item = (Epic) response.getBody();

		return item;

	}

	/**
	 * 
	 */
	//
	public Epic epicCreate(final Epic epic) throws IOException {

		final StringBuilder uri = new StringBuilder();
		uri.append("/projects");
		uri.append("/").append(epic.project_id);
		uri.append("/epics");

		final Epic item = client.post(uri, epic, Epic.class);

		return item;

	}

	/**
	 * 
	 */
	// https://www.pivotaltracker.com/help/api/rest/v5#Epics
	public List<Epic> epicList(final int projectId) throws IOException {

		final StringBuilder uri = new StringBuilder();
		uri.append("/projects");
		uri.append("/").append(projectId);
		uri.append("/epics");
		// uri.append("?test=test");
		// uri.append("&offset=1");
		// uri.append("&limit=1");

		final PivotalRequest request = new PivotalRequest();
		request.setUri(uri);
		request.setType(Epic.LIST_TYPE);

		final PivotalResponse response = client.get(request);

		@SuppressWarnings("unchecked")
		final List<Epic> list = (List<Epic>) response.getBody();

		return list;

	}

	/**
	 * 
	 */
	//
	public Label labelCreate(final Label label) throws IOException {

		final StringBuilder uri = new StringBuilder();
		uri.append("/projects");
		uri.append("/").append(label.project_id);
		uri.append("/labels");

		final Label item = client.post(uri, label, Label.class);

		return item;

	}

	/**
	 * 
	 */
	// https://www.pivotaltracker.com/help/api/rest/v5#Epics
	public List<Label> labelList(final int projectId) throws IOException {

		final StringBuilder uri = new StringBuilder();
		uri.append("/projects");
		uri.append("/").append(projectId);
		uri.append("/labels");
		// uri.append("?test=test");
		// uri.append("&offset=1");
		// uri.append("&limit=1");

		final PivotalRequest request = new PivotalRequest();
		request.setUri(uri);
		request.setType(Label.LIST_TYPE);

		final PivotalResponse response = client.get(request);

		@SuppressWarnings("unchecked")
		final List<Label> list = (List<Label>) response.getBody();

		return list;

	}

	/**
	 * 
	 */
	// https://www.pivotaltracker.com/help/api/rest/v5#Project
	public Project project(final int projectId) throws IOException {

		final StringBuilder uri = new StringBuilder();
		uri.append("/projects");
		uri.append("/").append(projectId);

		final PivotalRequest request = new PivotalRequest();
		request.setUri(uri);
		request.setType(Project.class);

		final PivotalResponse response = client.get(request);

		final Project item = (Project) response.getBody();

		return item;

	}

	/**
	 * 
	 */
	//
	public Project projectCreate(final Project project) throws IOException {

		final StringBuilder uri = new StringBuilder();
		uri.append("/projects");

		final Project item = client.post(uri, project, Project.class);

		return item;

	}

	/**
	 * 
	 */
	// https://www.pivotaltracker.com/help/api/rest/v5#Projects
	public List<Project> projectList() throws IOException {

		final StringBuilder uri = new StringBuilder();
		uri.append("/projects");

		final PivotalRequest request = new PivotalRequest();
		request.setUri(uri);
		request.setType(Project.LIST_TYPE);

		final PivotalResponse response = client.get(request);

		@SuppressWarnings("unchecked")
		final List<Project> list = (List<Project>) response.getBody();

		return list;

	}

	/**
	 * 
	 */
	// https://www.pivotaltracker.com/help/api/rest/v5#Story
	public Story story(final int projectId, final int storyId)
			throws IOException {

		final StringBuilder uri = new StringBuilder();
		uri.append("/projects");
		uri.append("/").append(projectId);
		uri.append("/stories");
		uri.append("/").append(storyId);

		final PivotalRequest request = new PivotalRequest();
		request.setUri(uri);
		request.setType(Story.class);

		final PivotalResponse response = client.get(request);

		final Story item = (Story) response.getBody();

		return item;

	}

	/**
	 * 
	 */
	// https://www.pivotaltracker.com/help/api/rest/v5#Stories
	public Story storyCreate(final Story story) throws IOException {

		final StringBuilder uri = new StringBuilder();
		uri.append("/projects");
		uri.append("/").append(story.project_id);
		uri.append("/stories");

		final Story item = client.post(uri, story, Story.class);

		return item;

	}

	/**
	 * 
	 */
	// https://www.pivotaltracker.com/help/api/rest/v5#Stories
	public List<Story> storyList(final int projectId) throws IOException {

		final StringBuilder uri = new StringBuilder();
		uri.append("/projects");
		uri.append("/").append(projectId);
		uri.append("/stories");
		// uri.append("?test=test");
		// uri.append("&offset=1");
		// uri.append("&limit=1");

		final PivotalRequest request = new PivotalRequest();
		request.setUri(uri);
		request.setType(Story.LIST_TYPE);

		final PivotalResponse response = client.get(request);

		@SuppressWarnings("unchecked")
		final List<Story> list = (List<Story>) response.getBody();

		return list;

	}

}
