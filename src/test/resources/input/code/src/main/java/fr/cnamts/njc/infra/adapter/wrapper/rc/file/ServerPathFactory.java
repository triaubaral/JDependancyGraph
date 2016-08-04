package fr.cnamts.njc.infra.adapter.wrapper.rc.file;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

enum ServerPathFactory {
	
	INSTANCE;
	
	private Map<ServerTargetPath, String>  pathes;
	
	private ServerPathFactory() {
		pathes = new HashMap<ServerTargetPath, String>();			 
		
		pathes.put(ServerTargetPath.DN_MULTIPUB_DIR, "${serveurCommun}/dn/multipub/${labelCommun}");
		pathes.put(ServerTargetPath.CNAMTS_MULTIPUB_DIR, "${serveurCommun}/cnamts/multipub/${labelCommun}");
		pathes.put(ServerTargetPath.DN_SOCLE_DIR, "${serveurCommun}/dn/${socle}/${labelCommun}");
		pathes.put(ServerTargetPath.CNAMTS_SOCLE_DIR, "${serveurCommun}/cnamts/${socle}/${labelCommun}");
		
	}
	
	public File resolve(ServerTargetPath targetPath, String[] params){
		
		String serveurCommun = params[0];
		String labelCommun = params[1];
		String socle = params[2];
		
		String tplPath = pathes.get(targetPath);
		
		tplPath = 	tplPath.replace("${serveurCommun}", serveurCommun)
							.replace("${labelCommun}", labelCommun);
		
		if(targetPath.name().contains("SOCLE")){
			tplPath = tplPath.replace("${socle}", socle);
		}
		
		return new File(tplPath);
		
	}

}
