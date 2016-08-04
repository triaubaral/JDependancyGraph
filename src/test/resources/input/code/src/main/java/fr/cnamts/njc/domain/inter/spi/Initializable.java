package fr.cnamts.njc.domain.inter.spi;

import java.util.List;

import fr.cnamts.njc.domain.bo.build.VarExecutionContext;
import fr.cnamts.njc.domain.bo.module.Dependance;

public interface Initializable {
	
	Collection<BuildableItem> getBuild();
	ListenableItem getListener();	
	String getParameter(VarExecutionContext varContext);
	String getParameter(VarExecutionContext varContext, String defaultValue);
	List<Dependance> getDependances();	
	void buildContext();

}
