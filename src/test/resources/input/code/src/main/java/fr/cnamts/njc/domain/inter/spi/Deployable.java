package fr.cnamts.njc.domain.inter.spi;

import java.util.List;

import com.google.common.collect.ArrayListMultimap;

public interface Deployable {
	
	void setHashProperties(ArrayListMultimap<String, String> properties);
	List<String> addFileToDeploy(String localPath, String fileName);
	boolean deployDependance(String storeRepoPath, boolean withBuildInfo);

}
