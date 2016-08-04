package fr.cnamts.njc.infra.artifactory.finder;

import java.io.IOException;

import org.jfrog.build.api.dependency.PropertySearchResult;
import org.jfrog.build.extractor.clientConfiguration.client.ArtifactoryDependenciesClient;

import fr.cnamts.njc.domain.exception.DependancesException;
import fr.cnamts.njc.infra.adapter.spi.ArtifactoryDeployer;

public abstract class ArtifactFinder {
	
	private ArtifactoryDependenciesClient client;
	

	public ArtifactFinder(ArtifactoryDeployer helper, String path) {
		super();
		try {
			this.client = helper.initClient(path);
		} catch (DependancesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	protected PropertySearchResult find(String query) throws IOException{
		
		return this.client.searchArtifactsByProperties(query);
		
	}

}
