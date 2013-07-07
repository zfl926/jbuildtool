package org.comanche.jbuildtool.project;

public class Project {
	public enum ProjectType { JAR, WAR, APP, ROOT };
	public enum BuildType {POM};
	
	private String projectName;
	private String path;
	private ProjectType projectType;
	private BuildType buildType;
	
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public ProjectType getProjectType() {
		return projectType;
	}
	public void setProjectType(ProjectType projectType) {
		this.projectType = projectType;
	}
	public BuildType getBuildType() {
		return buildType;
	}
	public void setBuildType(BuildType buildType) {
		this.buildType = buildType;
	}
	
}
