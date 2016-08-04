package fr.cnamts.njc.infra.cvsweb.module.conf.net;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jpatterns.gof.FactoryMethodPattern;

@FactoryMethodPattern.ConcreteProduct
public class RemoteFile {
	
	private static final Logger LOGGER = Logger.getLogger(RemoteFile.class);
	
	private FileStream fileStream;
		
	public RemoteFile(URL urlRemoteFile) {
		
		this.fileStream = new FileStream(urlRemoteFile);
		
	}

	public String getContent() {

		try {
			
			return IOUtils.toString(fileStream.getInputStream(), fileStream.getEncoding());
			
		} catch (IOException e) {
			LOGGER.error(e);
		}

		return "";
	}
	
	public void close(){
		fileStream.close();
	}
	
	
	
	

}
