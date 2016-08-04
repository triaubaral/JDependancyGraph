package fr.cnamts.njc.domain.bo.project;

import java.io.IOException;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;

import fr.cnamts.njc.domain.bo.module.Label;
import fr.cnamts.njc.domain.bo.module.LabelBuilder;

public class ProjectDetail implements Comparable<ProjectDetail>{
    private String CVSLab;
    private String CVSMod;
    private String CVSRef;
    private List<ProjectDetail> lstDepend;
    private List<String> osCibles;
    private int profond;
    private String userBuild = "cnamts";
    private Label label = Label.createEmpty();
    
    public Label getLabel() {
		return label;
	}

	public ProjectDetail() {
    		profond=0;
            lstDepend=Lists.newArrayList();
            osCibles=Lists.newArrayList();
          
    }

    public static ProjectDetail init(String pRepo, String pModule, String pLabel) throws IOException {
		ProjectDetail newPdt = new ProjectDetail();
		newPdt.setCVSRef(pRepo);
		newPdt.setCVSMod(pModule);
		newPdt.setCVSLab(pLabel);
		return newPdt;
	}

	public void addDepend(ProjectDetail pTreeDependances) {
        if(!getLstDepend().contains(pTreeDependances)){
            getLstDepend().add(pTreeDependances);
        }
        
    }

    public void addOsCible(String osCible) {
	 
		this.osCibles.add(osCible);
		
	}

    @Override
    public int compareTo(ProjectDetail pCompare) {
        return this.getCVSLab().compareTo(pCompare.getCVSLab());       
    }

    public String getCVSLab() {
        return CVSLab;
    }

    public String getCVSMod() {
        return CVSMod;
    }

    public String getCVSRef() {
        return CVSRef;
    }

    public List<ProjectDetail> getLstDepend() {
        return lstDepend;
    }
	
    public List<String> getOsCibles() {
		return osCibles;
	}

    public int getProfond() {
        return profond;
    }

	 
	public String getUserBuild() {
		return userBuild;
	}

    public void setCVSLab(String pCVSLab) throws IOException {
        CVSLab = pCVSLab.trim();
        label=new LabelBuilder(CVSLab).build();
    }

    public void setCVSMod(String pCVSMod) {
        CVSMod = pCVSMod.trim();
    }

    public void setCVSRef(String pCVSRef) {
        CVSRef = pCVSRef.trim();
    }

	public void setLstDepend(List<ProjectDetail> pLstDepend) {
        lstDepend = pLstDepend;
    }

	public void setOsCibles(List<String> osCibles) {
		this.osCibles = osCibles;
	}

	public void setProfond(int pProfond) {
        profond = pProfond;
    }
	
	public void setUserBuild(String pUser) {
		userBuild  = pUser;
		
	}

	public String toString(){
		
		StringBuffer lstDetail=new StringBuffer("");
		ArrayListMultimap<String, String> details=getAsPropertiesList();
		for ( String detail : details.keys()) {
			lstDetail.append(detail).append(" = ").append(details.get(detail)).append('\n');
		}
		
		return  lstDetail.toString();
	}

	public ArrayListMultimap<String, String> getAsPropertiesList() {
		 ArrayListMultimap<String, String> lstDet = getLabel().getAsPropertiesList();
		 
		 StringBuffer dependances=new StringBuffer("");
		 for (ProjectDetail depend : getLstDepend()) {
			 dependances.append(depend.getLabel());
			
		}
		 lstDet.put("dependances", dependances.toString());
		 
		 StringBuffer oscibles=new StringBuffer("");
		 for (String osCible : getOsCibles()) {
			 oscibles.append(osCible);
			
		 }
		 lstDet.put("oscibles", oscibles.toString());
		 
		 return lstDet;
	}

}