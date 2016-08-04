package fr.cnamts.njc.infra.adapter.api;

import java.util.Map;

import fr.cnamts.njc.domain.bs.build.action.DependanceDisplayerBS;
import fr.cnamts.njc.domain.bs.build.action.DependanceDownloaderBS;
import fr.cnamts.njc.domain.bs.build.action.LivrableFromLivDirDeployerBS;
import fr.cnamts.njc.domain.bs.build.action.LivrableFromPubDirDeployerBS;
import fr.cnamts.njc.domain.bs.build.action.SocleCheckerBS;
import fr.cnamts.njc.domain.bs.module.conf.mapper.PropertiesConfMapperBS;
import fr.cnamts.njc.domain.bs.module.conf.reader.CeriseReaderBS;
import fr.cnamts.njc.domain.bs.module.conf.reader.PropertiesConfReaderBS;
import fr.cnamts.njc.domain.bs.module.conf.reader.CeriseReaderBS.CVSPathElement;
import fr.cnamts.njc.domain.inter.api.Checkable;
import fr.cnamts.njc.domain.inter.api.ContentMapper;
import fr.cnamts.njc.domain.inter.api.ContentReader;
import fr.cnamts.njc.domain.inter.api.Deployable;
import fr.cnamts.njc.domain.inter.api.Displayable;
import fr.cnamts.njc.domain.inter.api.Downloadable;
import fr.cnamts.njc.domain.inter.spi.Initializable;

public enum BSFactory {
	
	INSTANCE;
	
	public Checkable newCheckable(Initializable context){
		
		return new SocleCheckerBS(context);
	}
	
	public Downloadable newDownloadable(Initializable context, fr.cnamts.njc.domain.inter.spi.Downloadable downloadable){
		
		return new DependanceDownloaderBS(context, downloadable);
	}
	
	public Deployable newLivDirDeployer(Initializable context, fr.cnamts.njc.domain.inter.spi.Deployable deployable){
		
		return new LivrableFromLivDirDeployerBS(context, deployable);
	}
	
	public Deployable newPubDirDeployer(Initializable context, fr.cnamts.njc.domain.inter.spi.Deployable deployable){
		
		return new LivrableFromPubDirDeployerBS(context, deployable);
	}
	
	public Displayable newDependanceDisplayer(Initializable context, fr.cnamts.njc.domain.inter.spi.Downloadable downloadable){
		
		return new DependanceDisplayerBS(context,downloadable);
	}
	
	public ContentMapper newContentMapper(){
		return new PropertiesConfMapperBS();
	}
	
	public ContentReader<Map<CVSPathElement, String>, String> newCeriseReader(){
		return new CeriseReaderBS();
	}
	
	public ContentReader<ContentMapper, String> newPropertiesConfReader(){
		return new PropertiesConfReaderBS();
	}

}
