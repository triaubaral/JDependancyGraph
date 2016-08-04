package fr.cnamts.njc.domain.bs.build.action;

import fr.cnamts.njc.domain.bo.build.VarExecutionContext;
import fr.cnamts.njc.domain.inter.api.Checkable;
import fr.cnamts.njc.domain.inter.spi.Initializable;

public class SocleCheckerBS extends AbstractActionBS implements Checkable{	
	
	public SocleCheckerBS(Initializable executionContext) {
		super(executionContext);
	}

	public boolean verify() {		
       
            final String OSREFAB = getParameter(VarExecutionContext.OSREFAB).toLowerCase(); 

            final String SOCLE_VERSION = getParameter(VarExecutionContext.SOCLE_VERSION).toLowerCase();

            if (!OSREFAB.contains(SOCLE_VERSION)) {
                this.getListenerBuild().fatalError("[ERROR] ce module ne peut se refabriquer sur le socle " + SOCLE_VERSION);
                this.getListenerBuild().fatalError("Liste des os de refabrication du properties.conf : " + OSREFAB);
                return false;
            }
            
            this.getListenerBuild().getLogger().println("Fabrication sur socle " + SOCLE_VERSION);

            return true;
       
    }

}
