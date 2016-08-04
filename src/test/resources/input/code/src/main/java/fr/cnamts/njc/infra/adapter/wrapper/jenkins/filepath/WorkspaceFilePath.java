package fr.cnamts.njc.infra.adapter.wrapper.jenkins.filepath;

import fr.cnamts.njc.domain.bo.build.VarExecutionContext;
import hudson.FilePath;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class WorkspaceFilePath {
		
	protected WorkspacePath workspace;
	protected FilePath filePath;
	
	public WorkspaceFilePath(WorkspacePath workspace) {	
		
		this.workspace = workspace;
		filePath = this.workspace.toFilePath();
	}
	
	public WorkspaceFilePath(WorkspacePath workspace, String childPath) {	
		
		this.workspace = workspace;
		filePath = this.workspace.toFilePath().child(childPath);
	}
		
	public WorkspaceFilePath(WorkspacePath workspace, VarExecutionContext path) {	
		
		this.workspace = workspace;
		filePath = WorkspaceWorkingDirFactory.INSTANCE.resolve(path, workspace).toFilePath();	
	}
	
	public void archiveTo(WorkspaceFilePath file){
		
		try {
			filePath.archive(hudson.util.io.ArchiverFactory.TARGZ,                
					file.write(),
			         "**/**");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public OutputStream write(){
		
		try {
			return filePath.write();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public boolean isEmpty(){
		
		try {
			return filePath.list().isEmpty();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
	
	public boolean exists(){
		try {
			return filePath.exists();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	public String getLocation(){
		return filePath.getRemote();
	}
	
	public boolean delete() throws IOException, InterruptedException{
		return filePath.delete();
	}
	
	public WorkspaceFilePath getChild(String path){
		
		return new WorkspaceFilePath(this.workspace, path);
	}
	
	public WorkspacePath getWorkspace(){
		return workspace;
	}
	
	public FilePath toFilePath(){
		return this.filePath;
	}
	
	public static void rmSubDirs(final String pSubDir, final FilePath pDir) throws IOException, InterruptedException {
        if (pDir.getName().equals(pSubDir)) {
            pDir.deleteRecursive();
        } else {
            if (pDir.isDirectory()) {
                final List<FilePath> subsDirs = pDir.listDirectories();
                for (final FilePath dirPath : subsDirs) {
                    rmSubDirs(pSubDir, dirPath);
                }

            }
        }
	}

}
