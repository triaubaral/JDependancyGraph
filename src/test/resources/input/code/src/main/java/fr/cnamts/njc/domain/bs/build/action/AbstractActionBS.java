package fr.cnamts.njc.domain.bs.build.action;

import java.io.IOException;
import java.util.List;

import fr.cnamts.njc.domain.bo.build.VarExecutionContext;
import fr.cnamts.njc.domain.bo.module.Dependance;
import fr.cnamts.njc.domain.bo.project.ProjectDetail;
import fr.cnamts.njc.domain.inter.spi.BuildableItem;
import fr.cnamts.njc.domain.inter.spi.Collection;
import fr.cnamts.njc.domain.inter.spi.Initializable;
import fr.cnamts.njc.domain.inter.spi.ListenableItem;

abstract class AbstractActionBS {
	
	private Collection<BuildableItem> progressBuild;
	private ListenableItem listenerBuild;	
	private Initializable executionContext;
	
	public AbstractActionBS(Initializable executionContext) {
		super();
		this.progressBuild = executionContext.getBuild();
		this.listenerBuild = executionContext.getListener();
		this.executionContext = executionContext;
		executionContext.buildContext();
		
	}	
	
	public Initializable getExecutionContext() {
		return executionContext;
	}
	
	public List<Dependance> getDependances() {
		return executionContext.getDependances();
	}
	
	/**
	 * @return the progressBuild
	 */
	public Collection<BuildableItem> getProgressBuild() {
		return progressBuild;
	}
	/**
	 * @return the listenerBuild
	 */
	public ListenableItem getListenerBuild() {
		return listenerBuild;
	}
	
	
	public String getParameter(VarExecutionContext contextVar){
		return executionContext.getParameter(contextVar);
	}
	
	public String getParameter(VarExecutionContext contextVar, String defaultValue){
		String value = executionContext.getParameter(contextVar);
		
		if(value == null || value.isEmpty()){
			return defaultValue;
		}
		
		return value;
	}
	
	
	public ProjectDetail getDetails() throws IOException, InterruptedException{	
		
		String label = getParameter(VarExecutionContext.CNQD_CVS_LABEL);      
		
		if(label!=null && label.length()>1){
		 
			String ref = getParameter(VarExecutionContext.CNQD_CVS_REFERENTIEL);
			String module = getParameter(VarExecutionContext.CNQD_CVS_MODULE);
			ProjectDetail depend=ProjectDetail.init(ref, module, label);
			return depend;
			
		} else{
			return new ProjectDetail();
			
		}
		
	}	

}
