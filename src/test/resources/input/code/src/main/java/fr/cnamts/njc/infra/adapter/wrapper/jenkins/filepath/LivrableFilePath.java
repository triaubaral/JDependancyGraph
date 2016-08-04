package fr.cnamts.njc.infra.adapter.wrapper.jenkins.filepath;

import java.io.IOException;

import hudson.FilePath;
import hudson.FilePath.TarCompression;

public class LivrableFilePath extends WorkspaceFilePath{

	public LivrableFilePath(WorkspacePath workspace, String nomLivrable) {
		super(workspace, nomLivrable);		
	}
	
	public void untar() throws IOException, InterruptedException{
		
		FilePath workspacePath = this.getWorkspace().toFilePath();
		
		workspacePath.untar(this.toFilePath(), TarCompression.GZIP);		
		 
	}
	
}
