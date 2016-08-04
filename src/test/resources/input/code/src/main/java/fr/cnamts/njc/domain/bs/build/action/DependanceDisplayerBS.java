package fr.cnamts.njc.domain.bs.build.action;

import java.util.ArrayList;
import java.util.List;

import fr.cnamts.njc.domain.bo.module.Dependance;
import fr.cnamts.njc.domain.inter.api.Displayable;
import fr.cnamts.njc.domain.inter.spi.Downloadable;
import fr.cnamts.njc.domain.inter.spi.Initializable;

public class DependanceDisplayerBS extends AbstractActionBS implements Displayable{
	
	private Downloadable downloadable;
	private List<Dependance> downloadedDependances;

	public DependanceDisplayerBS(Initializable executionContext, Downloadable downloadable) {
		super(executionContext);
		this.downloadable = downloadable;
		this.downloadedDependances = new ArrayList<Dependance>();
	}
	
	public boolean display(String propertiesConfFile){	

                // Récupération des dépendances 
            	DependanceDownloaderBS dependanceBS = new DependanceDownloaderBS(this.getExecutionContext(), downloadable);            	
               
                // enregistrement des dépendances en label
               
                
                downloadedDependances.addAll(dependanceBS.download(this.getDependances()));
                 
                //VarEnvHelper.buildAll(progressBuild.getHudsonBuild(), listenerBuild.getHudsonBuildListener());

                this.getListenerBuild().getLogger().println(
                        "Injection de " + propertiesConfFile + " en variables d'environnement réussie"
                        );
                
                if(downloadedDependances.size()>0){                
                	return true;
                }
                else{
                	return false;
                }
		
	}	
	
	public List<Dependance> getDownloadedDependances() {
		return downloadedDependances;
	}
	
}
