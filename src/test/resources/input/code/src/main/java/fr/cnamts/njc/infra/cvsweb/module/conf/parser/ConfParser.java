package fr.cnamts.njc.infra.cvsweb.module.conf.parser;

import hudson.FilePath;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import fr.cnamts.njc.domain.inter.api.ContentMapper;
import fr.cnamts.njc.domain.inter.api.ContentReader;
import fr.cnamts.njc.infra.adapter.api.BSFactory;
import fr.cnamts.njc.infra.adapter.spi.wrapper.jenkins.InProgressBuild;
import fr.cnamts.njc.infra.adapter.spi.wrapper.jenkins.ListenerBuild;
import fr.cnamts.njc.infra.jenkins.parser.CvsParser;

public class ConfParser {
	
	private InProgressBuild progressBuild;
	private ListenerBuild listenerBuild;
		
	
	public ConfParser(InProgressBuild progressBuild,
			ListenerBuild listenerBuild) {
		super();
		this.progressBuild = progressBuild;
		this.listenerBuild = listenerBuild;
	}

	public ContentMapper extractFromPropertiesConfPath(String propertiesConfPath){
		
		InputStream pProp = findPropertiesConf(propertiesConfPath);		
		String propConfFileContent = getPropertiesFileContent(pProp); 
		
		ContentReader<ContentMapper, String> confReader = BSFactory.INSTANCE.newPropertiesConfReader();
		ContentMapper content = confReader.read(propConfFileContent);
		
		return content;
		
	}
	
	public InputStream findPropertiesConf(String pProp){
		
		CvsParser parser = new CvsParser(progressBuild, listenerBuild);
		
			FilePath prop = progressBuild.getWorkspace().child(pProp);

			if (!isPropertiesConfExists(prop)) {
				prop = progressBuild
						.getWorkspace()
						.child(parser.getCVSModuleName()).child(pProp);
			}

			if (!isPropertiesConfExists(prop)) {
				prop = progressBuild.getWorkspace().child(progressBuild.getProject().getName())
						.child(pProp);
			}
			if (!isPropertiesConfExists(prop)) { // Multi conf
				prop = progressBuild.getWorkspace()
						.child(progressBuild.getProject().getRootProject().getName())
						.child(pProp);
			}

			if (!isPropertiesConfExists(prop)) {
				listenerBuild.getHudsonBuildListener().fatalError("Impossible de trouver le fichier "
						+ prop.getRemote() + " !");
				return null;
			} else {
				listenerBuild.getLogger().println(
						"Fichier properties.conf trouvé : " + prop.getRemote());
			}
			
			return toInputStream(prop);
	}
	
	private String getPropertiesFileContent(InputStream pProp){
		
		try {
			return IOUtils.toString(pProp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
		
	}
	
public boolean isPropertiesConfExists(FilePath prop){
		
		try {
			return prop.exists();
		} catch (IOException pException) {
			
			listenerBuild.getHudsonBuildListener().fatalError("ERROR  injection " + prop + " : "
					+ pException.getMessage());
			pException.printStackTrace(listenerBuild.getLogger());
			
		} catch (InterruptedException pException) {
			listenerBuild.getHudsonBuildListener().error("[WARNING] interruption de l'importation properties.conf "
					+ pException.getMessage());
		}
				
		return false;
		
	}
	
	
	public InputStream toInputStream(FilePath filePath){
		
		try {
			return filePath.read();
		} catch (IOException pException) {
			
			listenerBuild.getHudsonBuildListener().fatalError("ERROR  injection " + filePath + " : "
					+ pException.getMessage());
			pException.printStackTrace(listenerBuild.getLogger());
			
		} catch (InterruptedException pException) {
			listenerBuild.getHudsonBuildListener().error("[WARNING] interruption de l'importation properties.conf "
					+ pException.getMessage());
		}
		
		return null;
	}

}
