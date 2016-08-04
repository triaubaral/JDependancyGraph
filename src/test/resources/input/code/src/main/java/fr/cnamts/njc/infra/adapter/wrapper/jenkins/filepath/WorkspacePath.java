package fr.cnamts.njc.infra.adapter.wrapper.jenkins.filepath;

import fr.cnamts.njc.infra.adapter.spi.wrapper.jenkins.InProgressBuild;
import hudson.FilePath;

public class WorkspacePath {
	
	private FilePath filePath;	

	public WorkspacePath(InProgressBuild build) {
		super();		
		this.filePath = build.getWorkspace();		
	}
	
	public String getLocation(){
		return filePath.getRemote();
	}
	
	public FilePath toFilePath(){
		return filePath;
	}	

}
