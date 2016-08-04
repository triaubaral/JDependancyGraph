package fr.cnamts.njc.infra.cvsweb.module.conf.net;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;


public class Connection {
	
	private static final Logger LOGGER = Logger.getLogger(Connection.class);
	
	private HttpURLConnection httpURLConnection;
	
	public Connection(URL url) {
		 try {
			 httpURLConnection = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			LOGGER.error(e);
		}
	}
	
	public HttpURLConnection getHttpURLConnection() {
		return httpURLConnection;
	}
	
	public void close(){
		httpURLConnection.disconnect();
	}

}
