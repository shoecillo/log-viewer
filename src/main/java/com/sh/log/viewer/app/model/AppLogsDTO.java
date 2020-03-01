package com.sh.log.viewer.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AppLogsDTO {
	
	@JsonProperty("application")
	private String application;
	
	@JsonProperty("path")
	private String path;

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	
	
}
