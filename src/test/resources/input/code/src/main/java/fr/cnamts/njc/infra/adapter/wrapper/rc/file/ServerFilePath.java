package fr.cnamts.njc.infra.adapter.wrapper.rc.file;

import java.io.File;

public class ServerFilePath {
	
	private String[] params = new String[3];
	
	public ServerFilePath(String serveurCommun, String socle, String commun) {
		params[0] = serveurCommun;
		params[1] = socle;
		params[2] = commun;
	}	
	
	private File findFile(ServerTargetPath path){
		
		return ServerPathFactory.INSTANCE.resolve(path, params);	
		
	}
	
	public String getAbsolutePath(ServerTargetPath path){
		return findFile(path).getAbsolutePath();
	}
	
	public boolean exists(ServerTargetPath path){
		
		return findFile(path).exists();
	}
	
	public File getFile(ServerTargetPath path){
		
		return findFile(path);
	}

}
