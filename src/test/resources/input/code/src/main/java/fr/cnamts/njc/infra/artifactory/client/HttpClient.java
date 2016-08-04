package fr.cnamts.njc.infra.artifactory.client;

import hudson.FilePath;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.jfrog.build.client.ArtifactoryHttpClient;
import org.jfrog.build.client.PreemptiveHttpClient;

public class HttpClient {
	
	private HttpClient() {}
	
	public static final int TIMEOUT = 300;
	
	public static HttpResponse executeGetRequest(final String request, final String userName, final String password)
            throws IOException {
        final PreemptiveHttpClient client = new PreemptiveHttpClient(userName, password, TIMEOUT);
        final HttpGet httpGet = new HttpGet(ArtifactoryHttpClient.encodeUrl(request));
        HttpResponse reponse = client.execute(httpGet);

        return reponse;
    }
	
	public static boolean downloadArchive(final String pUrlStr, final FilePath targzFile) throws IOException, InterruptedException {

        // download sans authentification préalable

        URL tmpFile = new URL(ArtifactoryHttpClient.encodeUrl(pUrlStr));
        final HttpURLConnection huc = (HttpURLConnection) tmpFile.openConnection();
        try
        {
            final int responseCode = huc.getResponseCode();
            
            if (responseCode == 200) {
                  targzFile.copyFrom(tmpFile); 
                  return true;
            } else {
                throw new IOException("Erreur de connexion à l'URL " + pUrlStr + ", code réponse : " + responseCode
                        + " message : " + huc.getResponseMessage());
            }
        } finally {
            if(null!=huc){
                    huc.disconnect();
            }
        }

    }

}
