package fr.cnamts.njc.infra.adapter.wrapper.jenkins.filepath;

import jenkins.model.Jenkins;
import fr.cnamts.njc.domain.bo.build.VarExecutionContext;
import fr.cnamts.njc.infra.jenkins.plugin.builder.PubCommunBuilderImpl;
import fr.cnamts.njc.infra.jenkins.plugin.builder.TarGzBuilderImpl;

public enum WorkspaceWorkingDirFactory {
	INSTANCE;	
	
	public WorkspaceFilePath resolve(VarExecutionContext workingDir, WorkspacePath workspacePath){
		
		if(workingDir.equals(VarExecutionContext.COMMUN_DIR)){
			
			return new WorkspaceFilePath(workspacePath, "commun");
		}
		
		if(workingDir.equals(VarExecutionContext.LIVRAISON_DIR)){
			
			final TarGzBuilderImpl.DescriptorImpl tarDescriptor = (TarGzBuilderImpl.DescriptorImpl) Jenkins
					.getInstance().getDescriptor(TarGzBuilderImpl.class);
					
			return new WorkspaceFilePath(workspacePath, tarDescriptor.getLivDir());
		}
		
		if(workingDir.equals(VarExecutionContext.PUBLICATION_DIR)){
			
			final PubCommunBuilderImpl.DescriptorImpl pubDescriptor = (PubCommunBuilderImpl.DescriptorImpl) Jenkins
					.getInstance().getDescriptor(PubCommunBuilderImpl.class);
			
			return new WorkspaceFilePath(workspacePath, pubDescriptor.getPubDir());
		}	
		
		if(workingDir.equals(VarExecutionContext.ARTI_COMMUN_DIR)){
			
			final PubCommunBuilderImpl.DescriptorImpl pubDescriptor = (PubCommunBuilderImpl.DescriptorImpl) Jenkins
					.getInstance().getDescriptor(PubCommunBuilderImpl.class);
			
			return new WorkspaceFilePath(workspacePath, pubDescriptor.getArtiCommuns());
		}
		
		if(workingDir.equals(VarExecutionContext.RELEASE_DIR)){			
			
			final TarGzBuilderImpl.DescriptorImpl targzDescriptor = (TarGzBuilderImpl.DescriptorImpl) Jenkins
			.getInstance().getDescriptor(TarGzBuilderImpl.class);
			
			return new WorkspaceFilePath(workspacePath, targzDescriptor.getArtiRelease());
		}
		
		if(workingDir.equals(VarExecutionContext.SNAPSHOT_DIR)){
			
			final TarGzBuilderImpl.DescriptorImpl targzDescriptor = (TarGzBuilderImpl.DescriptorImpl) Jenkins
					.getInstance().getDescriptor(TarGzBuilderImpl.class);
			
			return new WorkspaceFilePath(workspacePath, targzDescriptor.getArtiSnapshots());
		}
		
		
		return null;
	}
	
}