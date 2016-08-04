package fr.cnamts.njc.domain.inter.api;

import java.util.List;

import fr.cnamts.njc.domain.bo.module.Dependance;

public interface Downloadable {
	
	 List<Dependance> download(List<Dependance> pLstDep);
	 
}
