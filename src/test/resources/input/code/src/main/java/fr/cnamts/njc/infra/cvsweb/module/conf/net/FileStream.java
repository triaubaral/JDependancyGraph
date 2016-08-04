package fr.cnamts.njc.infra.cvsweb.module.conf.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.log4j.Logger;

public class FileStream {
	
	private static final Logger LOGGER = Logger.getLogger(FileStream.class);
	
	private InputStream inputStream;
	private String encoding;
	private Connection connection;
	
	public FileStream(URL url) {
		
		connection = new Connection(url);	
		initInputStream(connection);
		initEncoding(connection);
		
		
	}
	
	private void initInputStream(Connection connection){
		
		try {
			inputStream = connection.getHttpURLConnection().getInputStream();
			
		} catch (IOException e) {
			
			LOGGER.error(e);
			
			throw new RuntimeException("Impossible d'accéder à la resource demandée.");
			
		}
	}
	
	private void initEncoding(Connection connection){
		
		encoding = connection.getHttpURLConnection().getContentEncoding();
		
	}
	
	
	public InputStream getInputStream() throws IOException{		
		return inputStream;
	}
	
	public String getEncoding(){
		return encoding;
	}
	
	public void close(){
		connection.close();
		
	}

}
