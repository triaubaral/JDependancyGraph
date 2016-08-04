package fr.cnamts.njc.infra.cvsweb.module.conf.net;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.jpatterns.gof.FactoryMethodPattern;

import fr.cnamts.njc.domain.bo.module.conf.TypeURL;

@FactoryMethodPattern.ConcreteCreator(participants=RemoteFile.class)
public enum RemoteFileFactory {
	
	INSTANCE;	
	
	private static final Logger LOGGER = Logger
			.getLogger(RemoteFileFactory.class);
	
	private static final String ROOT_SERVER_URL = "http://cvs.cnqd.cnamts.fr";
	
	
	public RemoteFile newRemoteFile(TypeURL type, String... strings){
		
		String url = createRemotePath(type);				
		
		return new RemoteFile(createURLQuietly(url, strings));
		
	}
	
	private String createRemotePath(TypeURL type){
		
		if(type.equals(TypeURL.CERISE_QUERY)){
			return ROOT_SERVER_URL+"/listeLabelsCerise.php?label=%s&codeNature=A";
		}
		
		return ROOT_SERVER_URL+"/cgi-bin/viewvc.cgi/%s/%s/install/properties.conf?profil=standard&view=co&pathrev=%s";
		
	}
	
	protected URL createURLQuietly(String url, String...strings){
		
		try{
			
			URL configFileUrl = new URL(String.format(url, strings));
			return configFileUrl;
		}
		catch(MalformedURLException e){
			LOGGER.error(e);
			throw new RuntimeException("Impossible d'accéder à l'URL :"+url);
		}
		
	}
	

}
