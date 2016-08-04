package fr.cnamts.njc.infra.adapter.spi.wrapper.jenkins;

import fr.cnamts.njc.domain.inter.spi.BuildableItem;
import hudson.model.AbstractProject;
import hudson.scm.CVSSCM;
import hudson.scm.SCM;

public class Project implements BuildableItem {
	
	private AbstractProject project;
	
	public Project(Project project) {
		
		this.project = project.getHudsonProject();
	}

	public Project(AbstractProject project) {
		
		this.project = project;
	}
	
	public boolean isCVSProject(){
		
		if(project == null || project.getScm() == null){
			return false;
		}
		
		return project.getScm() instanceof CVSSCM;
	}
	
	public String getName(){
		return project.getName();
	}
	
	public SCM getScm(){
		return project.getScm();
	}
	
	public Project getBuildingUpstream(){
		return new Project((AbstractProject) project.getBuildingUpstream());
	}
	
	public Project getRootProject(){
		return new Project(project.getRootProject());
	}
	
	private AbstractProject getHudsonProject(){
		return project;
	}

}
