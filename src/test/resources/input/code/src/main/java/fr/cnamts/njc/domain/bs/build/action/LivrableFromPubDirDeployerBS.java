package fr.cnamts.njc.domain.bs.build.action;

import java.io.IOException;

import com.google.common.collect.ArrayListMultimap;

import fr.cnamts.njc.domain.bo.build.VarExecutionContext;
import fr.cnamts.njc.domain.bo.project.ProjectDetail;
import fr.cnamts.njc.domain.inter.spi.Deployable;
import fr.cnamts.njc.domain.inter.spi.Initializable;

public class LivrableFromPubDirDeployerBS extends AbstractActionBS implements fr.cnamts.njc.domain.inter.api.Deployable{
	
	private Deployable deployable;

	public LivrableFromPubDirDeployerBS(Initializable executionContext, Deployable deployable) {
		super(executionContext);
		this.deployable = deployable;
	}
	
	public boolean deploy(String livrableName){
		
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
		
    	// Déploiement Artifactory si non HEAD
    	String artiRepo= this.getParameter(VarExecutionContext.SNAPSHOT_DIR);
        // user dn ou cnamts pour pose de release : TODO à changer par blocage/deblocage
       
        if ("dn".equalsIgnoreCase(user)) {
        	artiRepo=this.getParameter(VarExecutionContext.RELEASE_DIR);
        	
        }
    	
    	ArrayListMultimap<String, String> hashVerProps = details.getAsPropertiesList();
       
    	hashVerProps.put("user_ul", user);
        hashVerProps.put("groupId", user); // TODO a reflechir VarEnvHelper.getEnv(build, listener, "USER_UL"));
        hashVerProps.put("artefactId", details.getCVSMod()); //VarEnvHelper.getEnv(build, listener, "CNQD_CVS_MODULE"));
        hashVerProps.put("version", details.getCVSLab()) ; // VarEnvHelper.getEnv(build, listener, "CNQD_CVS_LABEL"));

    	// Declaration du helper artifactory et des propriétés à ajouter
        
        StringBuffer ssRep = new StringBuffer(getParameter(VarExecutionContext.SOCLE_VERSION).toLowerCase());
        ssRep.append('/').append(details.getCVSLab());
        
        deployable.setHashProperties(hashVerProps);
        deployable.addFileToDeploy(
                ssRep.toString(),
                livrableName);
        
        
        final boolean deployBool = deployable.deployDependance(
        		artiRepo,
                true
                );
        
        if (deployBool) {
            return true;
        } else {
            this.getListenerBuild().fatalError("[ERROR] Erreur de déploiement dans artifactory");
            return false;
        }
		
	}

}
