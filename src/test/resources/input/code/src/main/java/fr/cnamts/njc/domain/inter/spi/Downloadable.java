package fr.cnamts.njc.domain.inter.spi;

import java.util.List;

import fr.cnamts.njc.domain.bo.module.Dependance;

public interface Downloadable {
	
	List<Dependance> findAndDownloadDependancesFromArtiCommun(List<Dependance> dependancesToDownload);

}
