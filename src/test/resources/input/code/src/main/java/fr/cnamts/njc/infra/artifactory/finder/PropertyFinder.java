package fr.cnamts.njc.infra.artifactory.finder;

import java.io.IOException;

import org.jfrog.build.api.dependency.PropertySearchResult;
import org.jfrog.build.extractor.clientConfiguration.client.ArtifactoryDependenciesClient;

import fr.cnamts.njc.domain.bo.module.Dependance;
import fr.cnamts.njc.domain.exception.DependancesException;
import fr.cnamts.njc.infra.adapter.spi.ArtifactoryDeployer;

public class PropertyFinder extends ArtifactFinder{
	
	public PropertyFinder(ArtifactoryDeployer artiHelper, String path) {	
		super(artiHelper, path);		
	}
	
	public PropertySearchResult findDependance(Dependance pDependance) throws DependancesException, IOException{			
		
		String reqArti = "&version=" + pDependance.getNomcomplet();
        if (pDependance.isCommercial()) {
            reqArti = "nompub=" + pDependance.getNompub() + "&vermin=" + pDependance.getVermin();            
        }
		
		return this.find(reqArti);
	}
	
	

}
