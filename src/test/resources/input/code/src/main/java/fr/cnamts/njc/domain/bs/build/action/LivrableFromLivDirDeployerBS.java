package fr.cnamts.njc.domain.bs.build.action;

import java.io.File;
import java.io.IOException;

import com.google.common.collect.ArrayListMultimap;

import fr.cnamts.njc.domain.bo.build.VarExecutionContext;
import fr.cnamts.njc.domain.bo.project.ProjectDetail;
import fr.cnamts.njc.domain.inter.spi.Deployable;
import fr.cnamts.njc.domain.inter.spi.Initializable;

public class LivrableFromLivDirDeployerBS extends AbstractActionBS implements fr.cnamts.njc.domain.inter.api.Deployable{

	private Deployable deployable;
	
	public LivrableFromLivDirDeployerBS(Initializable executionContext, Deployable deployable) {
		super(executionContext);
		this.deployable = deployable;
	}
	
	public boolean deploy(String livrableName) {
		
		ProjectDetail details = null;
		try {
			details = getDetails();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 String user = this.getParameter(VarExecutionContext.USER_UL);
		 
		 ArrayListMultimap<String, String> hashVerProps = details.getAsPropertiesList();
		
		final String multipub = getParameter(VarExecutionContext.MULTIPUB);
	   	final String socleVersion = getParameter(VarExecutionContext.SOCLE_VERSION).toLowerCase();            	
	   	final String nompub = getParameter(VarExecutionContext.NOMPUB);
        final String verpub = getParameter(VarExecutionContext.VERPUB);
        final String cvsLabel = getParameter(VarExecutionContext.CNQD_CVS_LABEL);     
        final String cnqdCVSModule = getParameter(VarExecutionContext.CNQD_CVS_MODULE);
        final String propertiesConfPath = cnqdCVSModule + File.separator + "install" + File.separator + "properties.conf";
        
    	// user dn ou cnamts
        
        if (null == user
                || !user.equalsIgnoreCase("dn")) {
            user = "cnamts"; // TODO constante

        }
		
		
		 // depot communs
       final StringBuilder ssRep = new StringBuilder(user).append("/");
      

       if (multipub.equalsIgnoreCase("NON")) {
           ssRep.append(socleVersion);
       } else {
           ssRep.append(VarExecutionContext.MULTIPUB.name().toLowerCase());
       }
     
       ssRep.append("/").append(cvsLabel);
     

       // Gestion des verpub
       if (verpub.length() > 0 && !verpub.equalsIgnoreCase(VarExecutionContext.VERPUB.name())) {

           if (nompub.length() > 0 && !nompub.equalsIgnoreCase(VarExecutionContext.NOMPUB.name())) {
               hashVerProps.put(VarExecutionContext.NOMPUB.name().toLowerCase(), nompub);
           
           } else {
               hashVerProps.put(VarExecutionContext.NOMPUB.name().toLowerCase(), cnqdCVSModule);

           }
           
           hashVerProps.put(VarExecutionContext.VERPUB.name().toLowerCase(), verpub);
           final String[] arrVerpub = verpub.split("\\.");
           hashVerProps.put(VarExecutionContext.VERMIN.name().toLowerCase(), arrVerpub[0]);
           hashVerProps.put(VarExecutionContext.VERTECH.name().toLowerCase(), arrVerpub[1]);
        
          
       }
     
       hashVerProps.put("user_ul", user);
       hashVerProps.put("groupId", user); // TODO a reflechir VarEnvHelper.getEnv(build, listener, "USER_UL"));
       hashVerProps.put("artefactId", details.getCVSMod()); //VarEnvHelper.getEnv(build, listener, "CNQD_CVS_MODULE"));
       hashVerProps.put("version", details.getCVSLab()) ; // VarEnvHelper.getEnv(build, listener, "CNQD_CVS_LABEL"));

       // dépot du tar.gz de publication sur artifactory
       this.getListenerBuild().getLogger().println("Dépot " + livrableName +  " sur artifactory " +  ssRep.toString());
       

       
       if (deployToArtifactory(hashVerProps, ssRep.toString(), livrableName, propertiesConfPath)) 
           return true;
		
       return false;
		
	}
       
       public boolean deployToArtifactory(ArrayListMultimap<String, String> hashVerProps, String repDestination, String archiveName, String propertiesConfPath) {
           // Declaration du helper artifactory et des propriétés à ajouter
          
           
       	deployable.setHashProperties(hashVerProps);
       	deployable.addFileToDeploy(
           		repDestination,
           		archiveName);
       	deployable.addFileToDeploy(repDestination, propertiesConfPath);
          
           return deployable.deployDependance(                    
           		this.getParameter(VarExecutionContext.ARTI_COMMUN_DIR),
                   false
                   );
           
       }   
		
	
	
}
