package fr.cnamts.njc.domain.bs.build.action;

import java.util.List;

import fr.cnamts.njc.domain.bo.module.Dependance;
import fr.cnamts.njc.domain.inter.spi.Downloadable;
import fr.cnamts.njc.domain.inter.spi.Initializable;

public class DependanceDownloaderBS extends AbstractActionBS implements fr.cnamts.njc.domain.inter.api.Downloadable{
	
	private Downloadable downloadable;
	
	public DependanceDownloaderBS(Initializable executionContext, Downloadable downloadable) {
		super(executionContext);
		this.downloadable = downloadable;
	}

	public List<Dependance> download(List<Dependance> pLstDep){		
         
         return downloadable.findAndDownloadDependancesFromArtiCommun(pLstDep);
		
	}

}
