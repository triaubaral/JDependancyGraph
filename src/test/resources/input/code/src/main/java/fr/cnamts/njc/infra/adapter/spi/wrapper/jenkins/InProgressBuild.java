package fr.cnamts.njc.infra.adapter.spi.wrapper.jenkins;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.model.Action;
import hudson.model.AbstractBuild;
import hudson.util.VariableResolver;

import java.io.IOException;
import java.util.Calendar;
import java.util.Map;

import fr.cnamts.njc.domain.inter.spi.BuildableItem;
import fr.cnamts.njc.domain.inter.spi.Collection;
import fr.cnamts.njc.infra.adapter.wrapper.jenkins.filepath.WorkspaceFilePath;
import fr.cnamts.njc.infra.adapter.wrapper.jenkins.filepath.WorkspacePath;

public class InProgressBuild implements Collection<BuildableItem>{
	
	private AbstractBuild build;

	public InProgressBuild(AbstractBuild build) {
		
		this.build = build;
	}
	
	public int getNumber(){
		return build.getNumber();
	}
	
	public <T extends Action> T getAction(Class<T> type){
		return build.getAction(type);
	}
	
	public Calendar getTimestamp(){
		return build.getTimestamp();
	}

	public EnvVars getEnvironment(ListenerBuild listener) {
		
		try {
			return build.getEnvironment(listener.getHudsonBuildListener());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	public Map getBuildVariables() {
		return build.getBuildVariables();
	}

	public Project getProject() {
		return new Project(build.getProject());
	}
		

	public FilePath getWorkspace() {
		return build.getWorkspace();
	}
	
	/**
	 * Détermine le workspace associé au build en cours.
	 * @param modRepName nom du répertoire module à trouver.
	 * @return workspace maître ou esclave en fonction de l'existance ou non du répertoire nommé modRepName sur le maître.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public WorkspaceFilePath findWorkspace(String modRepName){
		
		final WorkspaceFilePath workspaceDir = new WorkspaceFilePath(new WorkspacePath(this), modRepName);
		
		if (workspaceDir.exists()){
			return workspaceDir;
		}
		
		return new WorkspaceFilePath(new WorkspacePath(this));
	}
	
	public AbstractBuild getHudsonBuild(){
		return build;
	}

	public VariableResolver<String> getBuildVariableResolver() {
		
		return build.getBuildVariableResolver();
	}

}
