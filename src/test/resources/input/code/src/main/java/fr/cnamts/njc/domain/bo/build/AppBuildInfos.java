package fr.cnamts.njc.domain.bo.build;

import java.util.ArrayList;
import java.util.List;


public class AppBuildInfos implements Comparable<AppBuildInfos> {

	private String referentiel;
	private String module;
	private String label;
	private List<String> osCibles;
	private List<AppBuildInfos> dependances;
	private int profond;

	public AppBuildInfos() {
		dependances = new ArrayList<AppBuildInfos>();
		osCibles =  new ArrayList<String>();
	}
	
	@Override
	public int compareTo(AppBuildInfos pCompare) {
		return this.getLabel().compareTo(pCompare.getLabel());
	}
	
	public int getProfond() {
		return profond;
	}

	public void setProfond(int pProfond) {
		profond = pProfond;
	}

	public String getReferentiel() {
		return referentiel;
	}

	public void setReferentiel(String pReferentiel) {
		referentiel = pReferentiel.trim();
	}

	public String getModule() {
		return module;
	}

	public void setModule(String pModule) {
		module = pModule.trim();
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String pLabel) {
		label = pLabel.trim();
	}

	public List<AppBuildInfos> getDependances() {
		return dependances;
	}

	public void setDependances(List<AppBuildInfos> pLstDepend) {
		dependances = pLstDepend;
	}

	public void addDependance(AppBuildInfos pTreeDependances) {
		if (!getDependances().contains(pTreeDependances)) {
			getDependances().add(pTreeDependances);
		}

	}

	public List<String> getOsCibles() {
		return osCibles;
	}

	public void setOsCibles(List<String> osCibles) {
		this.osCibles = osCibles;
	}

	public void addOsCible(String osCible) {
		this.osCibles.add(osCible);
	}

}